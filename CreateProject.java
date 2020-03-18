import java.io.*;

/**
 * Remember: System.getProperty("os.name" or "user.dir");
 * runtimeProcess = Runtime.getRuntime().exe("");
 */

public class CreateProject {
    // static String cwd       = System.getProperty("user.dir");
    static String OSName    = System.getProperty("os.name").toLowerCase();

    public static void main(String[] args) throws IOException{
        if (OSName.contains("win")) {
            System.out.println("THIS IS WINDOWS");

            createHandlerForWindows();

            //TODO: Compile complete project

            //TODO: ADD support of a Code-editor

        }
        else if (OSName.contains("linux")) {
	    System.out.println("This is linux");
      	    createHandlerForLinux(); 
	 } else if (OSName.indexOf("mac") > 0) {
            //ADD URI handler
        }
        System.out.println("DONE"); //FIXME: THIS LINE IS JUST FOR TEST PURPOSES
    }

    public static void createHandlerForWindows(){

        try {
            Runtime rt = Runtime.getRuntime();
            String[] commands = {"reg", "import", "src\\wuri_ide.reg"};
            // Process proc = rt.exec(commands);
            // FIXME: Can remove all the following lines.
            Process proc = rt.exec(commands);
            BufferedReader stdInput = new BufferedReader(new
                    InputStreamReader(proc.getInputStream()) );

            BufferedReader stdError = new BufferedReader(new
                    InputStreamReader(proc.getErrorStream()) );

            System.out.println("Here is the standard output of the command:\n");
            String s = null;
            while ((s = stdInput.readLine()) != null) {
                System.out.println(s);
            }

            System.out.println("Here is the errors:");
            while ((s = stdError.readLine()) != null) {
                System.out.println(s);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void createHandlerForLinux() {
	try {
		Runtime rt = Runtime.getRuntime();	
		// rt.exec("bash linux_uri/linux_app_uri.bash");
		 Process proc = rt.exec("sudo bash linux_uri/linux_add_uri.bash");
            BufferedReader stdInput = new BufferedReader(new
                    InputStreamReader(proc.getInputStream()) );

            BufferedReader stdError = new BufferedReader(new
                    InputStreamReader(proc.getErrorStream()) );

            System.out.println("Here is the standard output of the command:\n");
            String s = null;
            while ((s = stdInput.readLine()) != null) {
                System.out.println(s);
            }

            System.out.println("Here is the errors:");
            while ((s = stdError.readLine()) != null) {
                System.out.println(s);
            }

    	} catch (IOException e) {
		e.printStackTrace();	
	}

}
}
