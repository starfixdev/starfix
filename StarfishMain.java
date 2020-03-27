import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.util.ArrayList;

/**
 * Handles all program
 */
public class StarfishMain {
    private static String OSName = System.getProperty("os.name").toLowerCase();
    private static OS os = null;


    public static void main(String[] args) throws IOException{
        ArrayList<String> commandsToExecute = new ArrayList<>();
        try {
            // String test = "ide://clone-url?path=d:/working/java/&ide=vscode&url=https://github.com/DhyanCoder/starfish";
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
                }
            }
        } catch (IOException e) {
            throw new IOException("Cannot execute command successfully");
        }
        System.out.println("Successfully run");
    }

    /**
     * get what commands to execute according to uriData parameters
     * @param uriData
     * @return
     */
    private static String getCloneCommand(final URIData uriData) {
        return "git clone " + uriData.getUrl() + ".git" + " " + uriData.getPath();
    }

    //TODO: add support for other ide
    private static String getCommandToOpen(final URIData uriData) {
        if (uriData.getIde().equalsIgnoreCase("vscode") ) {
            return "code " + uriData.getPath() + "\\" + uriData.getRepoName();
        }
        else throw new IllegalArgumentException("Unsupported ide");
        //TODO: Move this to class URIData
    }
}