///usr/bin/env jbang "$0" "$@" ; exit $?
//DEPS io.quarkus.platform:quarkus-bom:2.6.0.CR1@pom
//DEPS io.quarkus:quarkus-jackson
//DEPS com.fasterxml.jackson.dataformat:jackson-dataformat-yaml
//DEPS io.quarkus:quarkus-picocli
//DEPS org.zeroturnaround:zt-exec:1.12
//FILES application.properties=../../../resources/application.properties
//SOURCES YAMLDefaultProvider.java

package dev.starfix;
import io.quarkus.runtime.annotations.RegisterForReflection;
import org.zeroturnaround.exec.ProcessExecutor;
import org.zeroturnaround.exec.ProcessResult;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.ExitCode;
import picocli.CommandLine.Parameters;

import java.io.UnsupportedEncodingException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
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
@RegisterForReflection(classNames = "java.util.Properties")
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
        File configFilePath = getConfigFilePath(); // Get path for config file 

        if (!configFilePath.exists()) {// Check if config file exist
            defaultConfig(); // Sets up default config
        }

        CloneUrl cloneUrl = new CloneUrl(url);
        // URL Validation to check a valid git repository
        if (!validate_url(cloneUrl.url)) { // Incase URI doesn't macth our scheme we'll terminate
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
            processCloneURL(cloneUrl);

            Path directory = Paths.get(clone_path, cloneUrl.getRepo_name());
            if (!Files.exists(directory)) { // Check if the user cloned the repo previously and in that case no cloning is  needed
                gitClone(directory, cloneUrl.getOriginUrl());
            }

            if(cloneUrl.getFilePath().length()>0) {
                cloneUrl.setFilePath(Paths.get(clone_path, cloneUrl.getRepo_name(), cloneUrl.getFilePath()).toAbsolutePath().toString());
            }
            // Launching Editor on the Cloned Directory
            System.out.println("Launching  Editor Now...");
            getIDE(ide).launch_editor(directory, ide, directory.toAbsolutePath().toString(),cloneUrl.getFilePath());
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

    // Function to determine if the current OS is Windows
    public static boolean isWindows() {
        return System.getProperty("os.name").toLowerCase().contains("windows");
    }

    // Function to determine if the current OS is Linux
    public static boolean isLinux() {
        return System.getProperty("os.name").toLowerCase().contains("linux");
    }

    // Function to determine if the current OS is MacOS
    public static boolean isMac() {
        return System.getProperty("os.name").toLowerCase().contains("mac");
    }

    // Function to fetch config file path
    public static File getConfigFilePath(){
        String userHome = System.getProperty("user.home");
        return new File(userHome + "/.config/starfix.yaml");
    }

    // Function to fetch config file
    public static File getConfigFile() {
        File configFilePath = getConfigFilePath();
        
        if(!configFilePath.getParentFile().exists()){// Check if parent directory exists
            if(!configFilePath.getParentFile().mkdirs()){// Create parent dir if not exist
                throw new IllegalStateException("Cannot create .config directory: " + configFilePath.getParentFile().getAbsolutePath());
            }
        }
        return configFilePath;

    }
    
    // Function to setup default config 
    void defaultConfig() {
        String path_env = System.getenv("PATH"); // System PATH variable
        clone_path =  System.getProperty("user.home") + "/code"; // set clone_path to /home/user_name/code

        if(isWindows()){// check if Windows OS
            if(path_env.contains("Microsoft VS Code")){ // If PATH has VScode
                ide = "code.cmd";
            } else if(path_env.contains("IntelliJ IDEA")){ // If PATH has IntelliJ
                ide = "idea64.exe";
            }
        }

        if(isLinux()){// check if Linux OS
            String[] sub_paths = path_env.split(":");

            for (String sub_path : sub_paths) {
                if(Files.exists(Paths.get(sub_path+"/code"))){
                    ide = "code";
                }else if(Files.exists(Paths.get(sub_path+"/idea"))){
                    ide = "idea";
                }else if(Files.exists(Paths.get(sub_path+"/eclipse"))){
                    ide = "eclipse";
                }
            }
        }
        
        // check if Mac OS
        // if(isMac()){}
    }
    // Function to edit configuration and serves for command line starfix config
    // editor
    public  void editConfig() throws Exception {
        System.out.println("\n-------------------------------------------------------");
        System.out.println("\t\tStarfix Configuration Editor");
        System.out.println("-------------------------------------------------------\n");
        //String clone_path = "";// Holds path to destination Where the Repository Must Be Clonned
        //String ide = ""; // Holds command for IDE to open upon
        System.out.println("IDE: "+ide);
        System.out.println("Clone Path: "+clone_path);

        // return a File
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
            }/*else{
                XConfig config = new ObjectMapper(new YAMLFactory()).readValue(configFile, XConfig.class);
                ide = config.getIde();
                clone_path = config.getClone_path();
            }*/

            // --------------------------First we'll input preferred IDE from
            // user------------------------------

            int id = 0;
            while (true) {
                System.out.println(
                        "\n--------Chose the preferred IDE --------\n 1.for vscode \n 2.for eclipse \n 3.for IntelliJ_IDEA \n 4.for Other(You'll have to enter launch command)");
                String ideInput = reader.readLine().trim();
                if(ideInput==null || ideInput.isEmpty()){
                    System.out.println("Empty/blank input provided - reseting to existing/default setting");
                    break;
                }
                id = Integer.parseInt(ideInput);
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
                }else if (id == 4) {
                    System.out.println("Enter launch command ");
                    ide = reader.readLine();
                    System.out.println("\n--------Launch command: "+ide);
                    break;
                } else
                    System.out.println("\n--------Invalid Input!! Try Again--------");
                    
            }

            // -----------Now we'll get preferred clone path on local file system from
            // user--------------

            System.out.println("\n--------Enter preferred Clonning path--------");
            String clonePathInput = reader.readLine();
            if(clonePathInput == null || clonePathInput.isEmpty()){
                System.out.println("Empty/blank input provided - reseting to existing/default setting");
            }else{
                clone_path = clonePathInput;
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

        String result = presult.outputUTF8();

        // for windows
        if (isWindows()){
            if (result.contains("\r\n")){
                result = result.replaceAll("\r\n","");
                result = result+System.lineSeparator();
            }
        }

        return result;
    }

    public static class CloneUrl{
        String url;
        String filePath ;
        String branch ;
        String originUrl;
        boolean isBlob ;
        String repo_name ;

        CloneUrl(String url){
            setUrl(url);
            setIsBlob(false);
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            if (url.startsWith("ide://")) {
                // stripping out ide:// to simplify launcher scripts.
                url = url.substring(6);
            }
            this.url = url ;
        }

        public String getFilePath() {
            return filePath;
        }

        public void setFilePath(String filePath) {
            this.filePath = filePath;
        }

        public String getBranch() {
            return branch;
        }

        public void setBranch(String branch) {
            this.branch = branch;
        }

        public String getOriginUrl() {
            return originUrl;
        }

        public void setOriginUrl(String originUrl) {
            this.originUrl = originUrl;
        }

        public boolean getIsBlob() {
            return isBlob;
        }

        public void setIsBlob(boolean blob) {
            this.isBlob = blob;
        }

        public String getRepo_name() {
            return repo_name;
        }

        public void setRepo_name(String repo_name) {
            this.repo_name = repo_name;
        }

    }

    public static void processCloneURL(CloneUrl cloneUrl) throws URISyntaxException, UnsupportedEncodingException {
        String url = cloneUrl.getUrl();
        String filePath = "";
        String branch = "";
        if(isBlob(url))
        {   // Example URL : https://github.com/starfixdev/starfix/blob/HEAD/cli/pom.xml
            // Example URL2: https://github.com/hexsum/Mojo-Webqq/blob/master/script/check_dependencies.pl#L17
            cloneUrl.setIsBlob(true);
            String temp = url.substring(url.indexOf("blob/")+5);
            branch = temp.substring(0,temp.indexOf("/"));
            filePath = temp.substring(temp.indexOf("/")+1);
            filePath = URLDecoder.decode(filePath,"UTF-8");
            url = url.substring(0,url.indexOf("/blob"));
        }
        URI uri = new URI(url);

        // extract name of repository
        String repo_name = Path.of(uri.getPath()).getFileName().toString();
        repo_name = repo_name.replace(".git", "");

        String originUrl = url;

        cloneUrl.setOriginUrl(originUrl);
        cloneUrl.setBranch(branch);
        cloneUrl.setFilePath(filePath);
        cloneUrl.setRepo_name(repo_name);

    }


    public static abstract class IDE{
        public abstract void launch_editor(Path directory, String ide, String path, String filePath)throws IOException, InterruptedException;
    }

    public static IDE getIDE(String ide){
        // Will return IDE based on String
        switch(ide){
            case "code":
            case "code.cmd":
                return new VsCode();

           case "idea":
           case "idea64.exe":
                return new IntelliJIdea();

           case "eclipse":
           case "eclipse.exe":
                return new Eclipse();
           default:
                return new CustomIDE();
        }

    }

    public static class VsCode extends IDE{
        
        public  void launch_editor(Path directory, String ide, String path, String filePath) throws IOException, InterruptedException {
            if(filePath.indexOf("#")>0){
                // code -g file:line
                filePath = filePath.replace("#L",":");
                runCommand(directory.getParent(), ide,"-g",path,filePath);
            }
            else{
                runCommand(directory.getParent(),ide,path,filePath);
            }
        }
    }

    public static class IntelliJIdea extends IDE{

        public  void launch_editor(Path directory, String ide, String path, String filePath) throws IOException, InterruptedException {
            if(filePath.indexOf("#")>0){
                filePath = filePath.replace("#L","#");
                // idea64.exe [--line <number>] [--column <number>] <path ...>
                // idea /home/fahad/MyProjects/testings/jbang --line 3 /home/fahad/MyProjects/testings/jbang/otp.java
                String lineNumber  = filePath.substring(filePath.lastIndexOf("#")+1);
                filePath = filePath.substring(0,filePath.lastIndexOf("#"));
                runCommand(directory.getParent(), ide,path,"--line",lineNumber,filePath);
            }
            else{
                runCommand(directory.getParent(),ide,path,filePath);
            }
        }
    }

    public static class Eclipse extends IDE{

        public  void launch_editor(Path directory, String ide, String path, String filePath) throws IOException, InterruptedException {
            if(filePath.indexOf("#")>0){
                filePath = filePath.replace("#L",":");
                // eclipse.exe file.txt:22
            }
            runCommand(directory.getParent(), ide,path,filePath);
        }
    }

    public static class CustomIDE extends IDE{

        public  void launch_editor(Path directory, String ide, String path, String filePath) throws IOException, InterruptedException {
            if(filePath.indexOf("#")>0){
                filePath = filePath.replace("#L",":");
                // eclipse.exe file.txt:22
            }
            runCommand(directory.getParent(), ide,path,filePath);
        }
    }

}
