import java.util.Map;

import soot.*;

public class PA4 {

	public static void main(String[] args) {
		String classPath = "."; // Change to the appropriate path to the test class
		String mainClass = "Harness";

		if (args.length != 2) {
			System.out.println("USAGE: PA4 processDir outputDir");
			System.exit(1);
		}


		String[] sootArgs = {
			"-cp", classPath,
			"-pp",
			"-whole-program",
			"-f", "c",
			"-d", args[1],
			"-O",
			"-keep-line-number",
			"-keep-bytecode-offset",
			"-p", "cg.spark", "on", 
			"-main-class", mainClass,
			"-process-dir", args[0],
		};
		
		ResolveVirtual resolveVirtual = new ResolveVirtual();
		PackManager.v().getPack("wjtp").add(new Transform("wjtp.vr", resolveVirtual));
		soot.Main.main(sootArgs);

	}

}
