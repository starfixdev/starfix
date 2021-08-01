///usr/bin/env jbang "$0" "$@" ; exit $?
//DEPS io.quarkus:quarkus-jackson:2.1.0.CR1
//DEPS com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.9.2
//DEPS io.quarkus:quarkus-picocli:2.1.0.CR1
//DEPS org.zeroturnaround:zt-exec:1.12
//FILES application.properties=../../../resources/application.properties
//SOURCES YAMLDefaultProvider.java

package dev.starfix;
import org.zeroturnaround.exec.ProcessExecutor;
import org.zeroturnaround.exec.ProcessResult;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.ExitCode;
import picocli.CommandLine.Parameters;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.io.File;
import java.util.Properties;
import java.util.concurrent.TimeoutException;
import java.util.regex.Pattern;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

@CommandLine.Command(name = "starfix", mixinStandardHelpOptions = true, defaultValueProvider = YAMLDefaultProvider.class)
public class Starfix implements Runnable{

    @Parameters(arity = "0..1")
    String uri;

    // Holds path to destination Where the Repository Must Be Clonned
    @CommandLine.Option(names = "--clone-path", descriptionKey = "clone_path")
    String clone_path;

    @CommandLine.Option(names = "--editor", descriptionKey = "ide")
    String ide = ""; // Holds command for IDE to open

    @Command
    public int config() throws Exception {
        editConfig();
        return ExitCode.OK;
    }

    @Command(name = "clone")
    public int cloneCmd(@Parameters(index = "0") String url) {

        if (url.startsWith("ide://")) {
            // stripping out ide:// to simplify launcher scripts.
            url = url.substring(6);
        }

        // URL Validation to check a valid git repository
        if (!validate_url(url)) { // Incase URI doesn't macth our scheme we'll terminate
            System.out.println(url);
            throw new IllegalArgumentException("Not a valid URI for git repository");
        }


        if(ide==null) {
            throw new IllegalArgumentException("Editor not specifed, please specify via --editor or use config to set it up.");
        }

        if(clone_path==null) {
            throw new IllegalArgumentException("Clone path not specifed, please specify via --clone-path or use config to set it up.");
        }

        try {
            String filePath = "";
            String branch = "";
            if(isBlob(url))
            {   // Example URL : https://github.com/starfixdev/starfix/blob/master/cli/pom.xml
                // Example URL2: https://github.com/hexsum/Mojo-Webqq/blob/master/script/check_dependencies.pl#L17

                String temp = url.substring(url.indexOf("blob/")+5);
                branch = temp.substring(0,temp.indexOf("/"));
                filePath = temp.substring(temp.indexOf("/")+1);
                url = url.substring(0,url.indexOf("/blob"));
            }

            URI uri = new URI(url);
            
            // extract name of repository
            String repo_name = Path.of(uri.getPath()).getFileName().toString();
            repo_name = repo_name.replace(".git", "");

            String originUrl = url;
            Path directory = Paths.get(clone_path, repo_name);

            if (!Files.exists(directory)) // Check if the user cloned the repo previously and in that case no cloning is
                                          // needed
                gitClone(directory, originUrl);

            if(filePath.length()>0)
            filePath = Paths.get(clone_path,repo_name,filePath).toAbsolutePath().toString();

            // Launching Editor on the Cloned Directory
            System.out.println("Opening " + filePath);
            launch_editor(directory, ide, directory.toAbsolutePath().toString(),filePath);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
        return ExitCode.OK;
    }
    

    
    @Override
    public void run() {
        if(uri==null||uri.isEmpty())
        {   
            new CommandLine(new Starfix()).usage(System.out); // Will invoke Picocli Help
            return;
        }
        cloneCmd(uri);
    }

    // Function to validate URL using with Regex
    public static boolean validate_url(String url) {
        // URL Validation to check a valid git repository
        String pattern = "((git|ssh|http(s)?)|(git@[\\w.]+))(:(//)?)([\\w.@:/\\-~]+)(.*)?";
        return Pattern.matches(pattern,url);
    }

    // Function to check if the URL points to a file
    public static boolean isBlob(String url){
        String pattern = "^https://github.com/(.*)/blob/(.*)$";
        return Pattern.matches(pattern,url);
    }

    // Function yo determine if the current OS is Windows
    public static boolean isWindows() {
        return System.getProperty("os.name").toLowerCase().contains("windows");
    }

    // Function to fetch config file
    public static File getConfigFile() {
        String userHome = System.getProperty("user.home"); // Get User Home Directory: /home/user_name

        return new File(userHome + "/.config/starfix.yaml");

    }

    // Function to edit configuration and serves for command line starfix config
    // editor
    public static void editConfig() throws Exception {
        System.out.println("\n-------------------------------------------------------");
        System.out.println("\t\tStarfix Configuration Editor");
        System.out.println("-------------------------------------------------------\n");
        String clone_path = "";// Holds path to destination Where the Repository Must Be Clonned
        String ide = ""; // Holds command for IDE to open upon

        File configFile = getConfigFile();

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        // Reading Configuration File
        try {
            if (!configFile.exists()) {
                // Incase no config file exists
                System.out.println("\n-No configurations exist for Starfix...You'll have to  configure it");
                System.out.println(
                        "\n-New  configuration file will be created at: " + configFile.getAbsolutePath() + "\n");
                configFile.createNewFile();
            }

            // --------------------------First we'll input preferred IDE from
            // user------------------------------

            int id = 0;
            while (true) {
                System.out.println(
                        "\n--------Chose the preferred IDE --------\n 1.for vscode \n 2.for eclipse \n 3.for IntelliJ_IDEA ");

                id = Integer.parseInt(reader.readLine());

                if (id == 1) {
                    ide = isWindows() ? "code.cmd" : "code";
                    System.out.println("\n--------Selected IDE:VsCode--------");
                    break;
                } else if (id == 2) {
                    ide = isWindows() ? "eclipse.exe":"eclipse";
                    System.out.println("\n--------Selected IDE:Eclipse--------");
                    break;
                } else if (id == 3) {
                    ide = isWindows() ?"idea64.exe":"idea";
                    System.out.println("\n--------Selected IDE:IntelliJ_IDEA--------");
                    break;
                } else if (id == 4) {
                    ide = isWindows() ?"emacsclientw.exe":"emacsclient";
                    System.out.println("\n--------Selected IDE:EMACS Client--------");
                    break;
                } else
                    System.out.println("\n--------Invalid Input!! Try Again--------");
            }

            // -----------Now we'll get preferred clone path on local file system from
            // user--------------
            while (true) {
                System.out.println("\n--------Enter preferred Clonning path--------");
                clone_path = reader.readLine();
                // We'll check if the path enterd by user is a valid path or not
                File tmp = new File(clone_path);
                if (tmp.exists())
                    break;
                // Incase of Invalid path he'll be shown an error and directed to try again
                System.out.println("\n--------Invalid Path!! Try Again--------");
            }
            // ----------Now we'll write configurations to the YAML FILE------------
            ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
            Properties configuration = new Properties();
            configuration.put("ide", ide);
            configuration.put("clone_path", clone_path);
            mapper.writeValue(configFile, configuration); // Writing to YAML File

        } catch (Exception e) {
            e.printStackTrace();

        }

    }

    public static void gitClone(Path directory, String originUrl) throws IOException, InterruptedException {
        // Function for git clonning
        runCommand(directory.getParent(), "git", "clone", originUrl, directory.toString());
    }

    public static String runCommand(Path directory, String... command) throws IOException, InterruptedException {
        // Function to Run Commands using Process Builder
        ProcessResult presult;
        try {
            System.out.println("Running " + String.join(" ", command));
            presult = new ProcessExecutor().command(command).redirectOutput(System.out).redirectErrorStream(true).readOutput(true)
                    .execute();
        } catch (TimeoutException e) {
            throw new RuntimeException("Error running command", e);
        }

        int exit = presult.getExitValue();
        if (exit!=0) {
            throw new AssertionError(
                    String.format("runCommand %s in %s returned %d", Arrays.toString(command), directory, exit));
        }

        return presult.outputUTF8();
    }


    public static void launch_editor(Path directory, String ide, String path, String filePath) throws IOException, InterruptedException {

        if(filePath.indexOf("#")>0){
            filePath = filePath.replace("#L","#");
            // ==== VS CODE ====
            // code -g file:line
            if(ide.equals("code")||ide.equals("code.cmd")){
                filePath = filePath.replace("#",":");
                runCommand(directory.getParent(), ide,"-g",path,filePath);
            }
            // ===== IntelliJ =====
            // idea64.exe [--line <number>] [--column <number>] <path ...>
            // idea /home/fahad/MyProjects/testings/jbang --line 3 /home/fahad/MyProjects/testings/jbang/otp.java
            if(ide.equals("idea")||ide.equals("idea.exe")||ide.equals("idea64.exe")){
                String lineNumber  = filePath.substring(filePath.lastIndexOf("#")+1);
                filePath = filePath.substring(0,filePath.lastIndexOf("#"));
                runCommand(directory.getParent(), ide,path,"--line",lineNumber,filePath);
            }
            // === Eclipse =====
            // eclipse.exe file.txt:22
            if(ide.equals("eclipse")){
                filePath = filePath.replace("#",":");
                runCommand(directory.getParent(), ide,path,filePath);
            }
            if(ide.equals("emacsclientw.exe")||ide.equals("emacsclient")){
                //emacsclient +4 info.txt
                String lineNumber  = "+"+filePath.substring(filePath.lastIndexOf("#")+1);
                filePath = filePath.substring(0,filePath.lastIndexOf("#"));

                runCommand(directory.getParent(), ide,lineNumber,filePath);
            }

        }
        else{
            runCommand(directory.getParent(),ide,path,filePath);// Launching the editor now
        }
    }



}
