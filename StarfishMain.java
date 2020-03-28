import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.NoSuchFileException;
import java.util.ArrayList;

/**
 * Handles all program
 */
public class StarfishMain {
    private static String OSName = System.getProperty("os.name").toLowerCase();
    private static OS os = null;


    public static void main(String[] args) throws IOException, InterruptedException{
        ArrayList<String> commandsToExecute = new ArrayList<>();
        try {
            URIData uriData = new URIData(args[0]);
            if (uriData.getCommand().equals("clone-url") ) {
                commandsToExecute.add(getCloneCommand(uriData) );
                commandsToExecute.add(getCommandToOpen(uriData) );
            } else if (uriData.getCommand().equals("open-file") ) {
                commandsToExecute.add(getCommandToOpen(uriData) );
            }

            if (OSName.contains("win")) {
                os = OS.WINDOWS;
            } else if (OSName.contains("linux")) {
                os = OS.LINUX;
            } else if (OSName.contains("mac")) {
                os = OS.MACOS;
            } else {
                System.out.println("Unknown OS");
                System.exit(1);
            }
        } catch (NoSuchFileException e) {
            e.printStackTrace();
        }

        try {
            Process proc = null;
            Runtime rt = Runtime.getRuntime();

            if (os == OS.WINDOWS) {
                for (String cmd : commandsToExecute) {
                    proc = rt.exec("cmd /c " + cmd);
                    proc.waitFor();
                    readAndPrintOutput(proc);
                }
            }
        } catch (IOException e) {
            throw new IOException("Cannot execute command successfully");
        } catch (InterruptedException e) {
            throw new InterruptedException("cannot execute command successfully");
        }
        System.out.println("Successfully run");
    }

    /**
     * get what commands to execute according to uriData parameters
     * @param uriData
     * @return
     */
    private static String getCloneCommand(final URIData uriData) {
        return "git clone " + uriData.getUrl() + ".git" + " " + uriData.getPath() + "\\" + uriData.getRepoName();
    }

    //TODO: add support for other ide
    private static String getCommandToOpen(final URIData uriData) throws NullPointerException{

        if (uriData.getIde().equalsIgnoreCase("vscode")) {
            return "code " + uriData.getPath() + "\\" + uriData.getRepoName();
        }
        //TODO: Move this to class URIData
        throw new IllegalArgumentException("Unknown IDE");
    }

    /**
     *
     * @param process to read output to console
     * @throws IOException
     */
    private static void readAndPrintOutput(final Process process) throws IOException{
        // int taskSuccess = 0; // 0 for success | 1 for failure

        BufferedReader stdInput = new BufferedReader(new
                InputStreamReader(process.getInputStream()) );

        BufferedReader stdError = new BufferedReader(new
                InputStreamReader(process.getErrorStream()) );

        //output from command execution
        String s = null;
        while ((s = stdInput.readLine()) != null) {
            System.out.println(s);
        }
        while ((s = stdError.readLine()) != null) {
            System.out.println(s);
        }
    }
}