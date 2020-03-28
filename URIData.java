import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.NoSuchFileException;

public class URIData {

    /**
     * command -> Possible commands
     *
     */
    private String command;
    private File path;
    private URL url;
    private String ide = "vscode";
    private String repoName = "";

    public URIData(String str) throws NoSuchFileException{
        if (str == null) throw new NullPointerException("Cannot Create URICommand object.");
        parseInput(str);
    }

    public String getRepoName() {
        return repoName;
    }

    public File getPath() {
        return path;
    }

    public URL getUrl() {
        return url;
    }

    public String getIde() {
        return ide;
    }

    public String getCommand() {
        return command;
    }

    /**
     * breaks String URI to different command and argument
     *
     *  if any of the parameter {instance variables} are not defined
     *  then corresponding property remains null
     *
     *  example - for input ->
     *      "ide://clone-url?path=d:/working/java&ide=vscode&url=https://github.com/DhyanCoder/starfish"
     *      sets instance variable  command = clone-url
     *                              path = d:/working/java
     *                              ide = vscode
     *                              url = https://github.com/DhyanCoder/starfish
     *
     * @param str - input string to provide command
     */
    private void parseInput(String str) throws IllegalArgumentException,
                                NoSuchFileException{
        // Remove ide://
        str = str.substring(6);

        // Obtain Command
        String[] command_and_rest = str.split("[?]", 2);
        switch (command_and_rest[0]) {
            // windows put / before ? while passing uri so open-file becomes open-file/
            //      same for all other commands
            case "open-file/":
                //fall-through
            case "OPEN-FILE/":
                //fall-through
            case "open-file":
                //fall-through
            case "OPEN-FILE":
                command = "open-file";
                break;
            case "clone-url/":
                //fall-through
            case "CLONE-URL/":
                //fall-through
            case "clone-url":
                //fall-through
            case "CLONE-URL":
                command = "clone-url";
                break;
            default:
                throw new IllegalArgumentException("Cannot identify command");
        }

        String[] parts_of_str;
        // breakFileIn & sign
        if (command_and_rest[1].contains("&") )
            parts_of_str = command_and_rest[1].split("[&]");
        else
            parts_of_str = new String[] { command_and_rest[1] };
        // break it into variable=value
        for (String s : parts_of_str) {
            String[] tmp = s.split("=");
            switch(tmp[0]) {
                case "path":
                    //fall through
                case "PATH":
                    this.path = getFile(tmp[1]);
                    break;
                case "url":
                    // fall through
                case "URL":
                    setUrl(tmp[1]);
                    this.repoName = obtainRepoName(tmp[1]);
                    break;
                case "ide":
                    //fall through
                case "IDE":
                    this.ide = tmp[1];
                    break;
                default:
                    throw new IllegalArgumentException("Cannot identify command");
            }
        }
    }

    @Override
    public String toString() {
        return  "command = " + command + "\n" +
                "URL = " + url + "\n" +
                "path = " + path + "\n" +
                "ide = " + ide + "\n";
    }

    /**
     *
     * @param file_str path to save or open file
     * @return a file object if file_str is a valid path otherwise
     * @throws NoSuchFileException
     */
    private File getFile(String file_str) throws NoSuchFileException{
        File f1 = new File(file_str);
        if (f1.exists() ) {
            return f1;
        } else {
            throw new NoSuchFileException("Provided path is invalid");
        }
    }

    //TODO: correct this method
    private boolean isValidUrl(String str) {
        return true;
    }

    private void setUrl(String str) {
        try {
            isValidUrl(str);
            this.url = new URL(str);
        } catch (MalformedURLException e){
            url = null;
        }
    }

    /**
     *
     * @param str
     * @return repoName from uri
     */
    private String obtainRepoName(String str) {
        String[] tempo = str.split("[/]");
        return tempo[tempo.length-1];
    }
}