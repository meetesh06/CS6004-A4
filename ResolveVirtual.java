import java.util.*;

import fj.P;
import soot.*;
import soot.jimple.InvokeExpr;
import soot.jimple.InvokeStmt;
import soot.jimple.Jimple;
import soot.jimple.JimpleBody;
import soot.jimple.ReturnStmt;
import soot.jimple.StaticInvokeExpr;
import soot.jimple.ThisRef;
import soot.jimple.VirtualInvokeExpr;
import soot.jimple.internal.JAssignStmt;
import soot.jimple.internal.JIdentityStmt;
import soot.jimple.internal.JInterfaceInvokeExpr;
import soot.jimple.internal.JVirtualInvokeExpr;
import soot.jimple.internal.JimpleLocal;
import soot.jimple.toolkits.callgraph.CallGraph;
import soot.jimple.toolkits.callgraph.Edge;

class PTGWLElement {
    PointsToAnalysis pta;
}

public class ResolveVirtual extends SceneTransformer {

    int callSitesTransformed = 0;
    int staticMethodsAdded = 0;

    @Override
    public String toString() {
        System.out.println("======== ResolveVirtual Statistics ========");
        System.out.println("  Call Sites Transformed = " + callSitesTransformed);
        System.out.println("  Static Methods Added   = " + callSitesTransformed);
        return ("===========================================");
    }

    static CallGraph cg;
    HashMap<SootMethod, SootMethod> staticReplacementMap = new HashMap<>();

    public SootMethod ensureDuplicateEntryFor(SootMethod m) {
        callSitesTransformed++;
        if (staticReplacementMap.containsKey(m))
            return staticReplacementMap.get(m);
        else {
            staticMethodsAdded++;
            SootClass sc = m.getDeclaringClass();

            List<Type> args = new ArrayList<>();
            args.addAll(m.getParameterTypes());
            args.add(RefType.v(m.getDeclaringClass().getName()));
            // Create a copy of the method
            SootMethod sm = new SootMethod(
                    m.getName() + "_copied", // Create a new name for the copied method
                    args,
                    m.getReturnType(),
                    Modifier.PUBLIC | Modifier.STATIC);
            JimpleBody body = (JimpleBody) m.getActiveBody().clone();

            // Replace the reference to this pointer with argument reference
            body.getUnits().forEach(u -> {
                if (u instanceof JIdentityStmt) {
                    JIdentityStmt idenStmt = (JIdentityStmt) u;
                    Value leftVal = idenStmt.leftBox.getValue();
                    Value rightVal = idenStmt.rightBox.getValue();
                    if (leftVal instanceof JimpleLocal && rightVal instanceof ThisRef) {
                        // Replace reference from this pointer to local var
                        idenStmt.setRightOp(Jimple.v().newParameterRef(RefType.v(m.getDeclaringClass().getName()),
                                args.size() - 1));
                    }
                }
            });

            sm.setActiveBody(body);
            body.validate();
            sc.addMethod(sm);
            staticReplacementMap.put(m, sm);
            return sm;
        }
    }

    @Override
    protected void internalTransform(String arg0, Map<String, String> arg1) {
        Set<SootMethod> methods = new HashSet<>();
        cg = Scene.v().getCallGraph();
        // Get the main method
        SootMethod mainMethod = Scene.v().getMainMethod();
        // Get the list of methods reachable from the main method
        // Note: This can be done bottom up manner as well. Might be easier to model.
        getlistofMethods(mainMethod, methods);

        for (SootMethod m : methods) {
            // Identify methods that need to be cloned
            m.getActiveBody().getUnits().forEach((u) -> {
                if (u instanceof InvokeStmt) {
                    InvokeExpr expr = ((InvokeStmt) u).getInvokeExpr();
                    if (expr instanceof VirtualInvokeExpr) {
                        CallGraph cg = Scene.v().getCallGraph();
                        Iterator<Edge> outEdges = cg.edgesOutOf(u);
                        SootMethod targetMethod = null;
                        int size = 0;
                        while (outEdges.hasNext()) {
                            size++;
                            targetMethod = outEdges.next().tgt();
                        }

                        System.out.println(u.getJavaSourceStartLineNumber() + " : " + size);

                        if (size > 1)
                            return;

                        if (targetMethod == null)
                            targetMethod = expr.getMethod().getActiveBody().getMethod();

                        if (targetMethod.isJavaLibraryMethod() || targetMethod.isConstructor())
                            return;

                        InvokeStmt ivs = (InvokeStmt) u;

                        List<Value> newArgs = new ArrayList<Value>();
                        newArgs.addAll(expr.getArgs());
                        newArgs.add(((VirtualInvokeExpr) expr).getBase());

                        StaticInvokeExpr sve = Jimple.v()
                                .newStaticInvokeExpr(ensureDuplicateEntryFor(targetMethod).makeRef(), newArgs);
                        ivs.setInvokeExpr(sve);
                    }
                }

                if (u instanceof JAssignStmt) {
                    JAssignStmt stmnt = (JAssignStmt) u;
                    if (stmnt.leftBox.getValue() instanceof JimpleLocal
                            && stmnt.rightBox.getValue() instanceof JVirtualInvokeExpr) {

                        InvokeExpr expr = (InvokeExpr) stmnt.rightBox.getValue();

                        if (expr instanceof VirtualInvokeExpr) {
                            CallGraph cg = Scene.v().getCallGraph();
                            Iterator<Edge> outEdges = cg.edgesOutOf(u);
                            SootMethod targetMethod = null;
                            int size = 0;
                            while (outEdges.hasNext()) {
                                size++;
                                targetMethod = outEdges.next().tgt();
                            }
                            if (size > 1)
                                return;

                            if (targetMethod == null)
                                targetMethod = expr.getMethod().getActiveBody().getMethod();

                            if (targetMethod.isJavaLibraryMethod() || targetMethod.isConstructor())
                                return;


                            List<Value> newArgs = new ArrayList<Value>();
                            newArgs.addAll(expr.getArgs());
                            newArgs.add(((VirtualInvokeExpr) expr).getBase());

                            StaticInvokeExpr sve = Jimple.v()
                                    .newStaticInvokeExpr(ensureDuplicateEntryFor(targetMethod).makeRef(), newArgs);
                            
                            stmnt.setRightOp(sve);
                        }

                    }
                }
            });

            // System.out.println(m);
            // System.out.println(m.getActiveBody());
        }

        
        System.out.println(this);

    }

    private static void getlistofMethods(SootMethod method, Set<SootMethod> reachableMethods) {
        // Avoid revisiting methods
        if (reachableMethods.contains(method)) {
            return;
        }
        // Add the method to the reachable set
        reachableMethods.add(method);

        // Iterate over the edges originating from this method
        Iterator<Edge> edges = Scene.v().getCallGraph().edgesOutOf(method);
        while (edges.hasNext()) {
            Edge edge = edges.next();
            SootMethod targetMethod = edge.tgt();
            // Recursively explore callee methods
            if (!targetMethod.isJavaLibraryMethod()) {
                getlistofMethods(targetMethod, reachableMethods);
            }
        }
    }
}
