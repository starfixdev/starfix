import java.io.*;
/**
 * This Program Adds URI Handler to OS
 * (currently supports windows and linux)
 */
public class CreateProject {
    static String OSName = System.getProperty("os.name").toLowerCase();
    public static void main(String[] args) throws IOException{
        if (OSName.contains("win")) {
            createHandlerForWindows();
            
        } else if (OSName.contains("linux")) {
      	    createHandlerForLinux(); 
        
        } else if (OSName.indexOf("mac") > 0) {
            System.out.println("MAC-OS NOT SUPPORTED :)");
            //TODO: ADD URI handler
        } else {
            System.out.println("Not supported in OS " + OSName);
        }
    }

    public static void createHandlerForWindows(){

        try {
            Runtime rt = Runtime.getRuntime();
            
            //Makes a folder for project
            String path = "\"c:/Program Files/Starfish\"";
            String[] commands = {"mkdir", path}; 
            Process proc = rt.exec(commands);
            
            //Copies FileOpener.class file which handles the URI
            commands = new String[]{"xcopy" , "FileOpener.class", path};
            proc = rt.exec(commands);
            
            // adds registry for ide:
            commands = new String[]{"reg", "import", "windows_uri\\wuri_ide.reg"};
            proc = rt.exec(commands);
            
            BufferedReader stdInput = new BufferedReader(new
                    InputStreamReader(proc.getInputStream()) );

            BufferedReader stdError = new BufferedReader(new
                    InputStreamReader(proc.getErrorStream()) );
            
            //output from command execution
            String s = null;
            while ((s = stdInput.readLine()) != null) {
                System.out.println(s);
            }
            while ((s = stdError.readLine()) != null) {
                System.out.println(s);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void createHandlerForLinux() {
	    try {
		    Runtime rt = Runtime.getRuntime();	
		    Process proc = rt.exec("sudo bash install.bash");
            BufferedReader stdInput = new BufferedReader(new
                    InputStreamReader(proc.getInputStream()) );

            BufferedReader stdError = new BufferedReader(new
                    InputStreamReader(proc.getErrorStream()) );

            //Output from command execution
            String s = null;
            while ((s = stdInput.readLine()) != null) {
                System.out.println(s);
            }
            while ((s = stdError.readLine()) != null) {
                System.out.println(s);
            }

    	} catch (IOException e) {
		e.printStackTrace();	
	    }

    }
}
