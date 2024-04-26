import java.util.Map;

import soot.*;

public class PA4 {

	public static void main(String[] args) {
		String classPath = "."; // Change to the appropriate path to the test class
		String mainClass = "DispatchBenchmark";
		String dir = "./benchmark"; // Directory containing classes to analyze

		String[] sootArgs = {
				"-cp", classPath,
				"-pp",
				"-whole-program",
				"-f", "c",
				"-O",
				"-keep-line-number",
				"-keep-bytecode-offset",
				"-p", "cg.spark", "on", 
				"-main-class", mainClass,
				"-process-dir", dir,

			};
		
		ResolveVirtual resolveVirtual = new ResolveVirtual();
		PackManager.v().getPack("wjtp").add(new Transform("wjtp.vr", resolveVirtual));
		soot.Main.main(sootArgs);

	}

}
