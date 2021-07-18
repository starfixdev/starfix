///usr/bin/env jbang "$0" "$@" ; exit $?
//DEPS io.quarkus:quarkus-jackson:1.8.1.Final
//DEPS com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.9.2
//DEPS io.quarkus:quarkus-picocli:1.8.1.Final
//Q:CONFIG quarkus.banner.enabled=false
//Q:CONFIG quarkus.log.level=WARN

package dev.starfix;
import io.quarkus.runtime.annotations.RegisterForReflection;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.ExitCode;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;
import io.quarkus.runtime.QuarkusApplication;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

@CommandLine.Command(mixinStandardHelpOptions = true)
public class Starfix implements Runnable{

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
            throw new IllegalArgumentException("Not a valid URI for git repository");
        }

        // Configuration identifiers
        String clone_path = "";// Holds path to destination Where the Repository Must Be Clonned
        String ide = ""; // Holds command for IDE to open upon

        File configFile = getConfigFile();// Calling functiono to fetch config file

        // Reading Configuration File
        try {
            if (!configFile.exists()) {
                // Incase no config file exists
                editConfig(); // Calling function that lets user to configure
            }

            // System.out.println("\nLoading configurations.....\n");
            ObjectMapper mapper = new ObjectMapper(new YAMLFactory());

            Config config = mapper.readValue(configFile, Config.class);

            ide = config.ide;
            clone_path = config.clone_path;
            if (ide == null || clone_path == null)
                editConfig(); // Incase of absence of configuration in file Launch Config

        } catch (Exception e) {
            e.printStackTrace();

        }

        try {
            String filePath = "";
            String branch = "";
            if(isBlob(url))
            {   // Example URL : https://github.com/starfixdev/starfix/blob/master/cli/pom.xml

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
            System.out.println("Launching  Editor Now...");
            launch_editor(directory, ide, directory.toAbsolutePath().toString(),filePath);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
        return ExitCode.OK;
    }
    
    @Parameters(arity = "0..1")
    String uri;
    
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
        String pattern = "((git|ssh|http(s)?)|(git@[\\w\\.]+))(:(//)?)([\\w\\.@\\:/\\-~]+)(\\.git)?(/)?";
        Pattern r = Pattern.compile(pattern);
        // Now create matcher object.
        Matcher m = r.matcher(url);
        return m.matches();
    }

    // Function to check if the URL points to a file
    public static boolean isBlob(String url){
        String pattern = "^https://github.com/(.*)/blob/(.*)$";
        Pattern r = Pattern.compile(pattern);
        // Now create matcher object.
        Matcher m = r.matcher(url);
        return m.matches();
    }

    // Function yo determine if the current OS is Windows
    public static boolean isWindows() {
        return System.getProperty("os.name").toLowerCase().indexOf("windows") >= 0;
    }

    // Function to fetch config file
    public static File getConfigFile() {
        String userHome = System.getProperty("user.home"); // Get User Home Directory: /home/user_name

        File configFile = new File(userHome + "/.config/starfix.yaml"); // Loading YAML

        return configFile;

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
            while (id != 1 || id != 2 || id != 3) {
                System.out.println(
                        "\n--------Chose the preferred IDE --------\n 1.for vscode \n 2.for eclipse \n 3.for IntelliJ_IDEA ");

                id = Integer.parseInt(reader.readLine());

                if (id == 1) {
                    ide = isWindows() ? "code.cmd" : "code";
                    System.out.println("\n--------Selected IDE:VsCode--------");
                    break;
                } else if (id == 2) {
                    ide = "eclipse";
                    System.out.println("\n--------Selected IDE:Eclipse--------");
                    break;
                } else if (id == 3) {
                    ide = "idea";
                    System.out.println("\n--------Selected IDE:IntelliJ_IDEA--------");
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
            Config configuration = new Config(ide, clone_path);
            mapper.writeValue(configFile, configuration); // Writing to YAML File

        } catch (Exception e) {
            e.printStackTrace();

        }

    }

    // Function to Launch the Editor
    public static void launch_editor(Path directory, String ide, String path, String filePath) throws IOException, InterruptedException {
        runCommand(directory.getParent(), ide, path,filePath);// Launching the editor now

    }

    public static void gitClone(Path directory, String originUrl) throws IOException, InterruptedException {
        // Function for git clonning
        runCommand(directory.getParent(), "git", "clone", originUrl, directory.getFileName().toString());
    }

    public static String runCommand(Path directory, String... command) throws IOException, InterruptedException {
        // Function to Run Commands using Process Builder
        Process p = process_runner(directory, command);
        return gobbleStream(p);

    }

    public static Process process_runner(Path directory, String... command) throws IOException, InterruptedException {
        Objects.requireNonNull(directory, "directory");
        if (!Files.exists(directory)) {
            throw new RuntimeException("can't run command in non-existing directory '" + directory + "'");
        }
        ProcessBuilder pb = new ProcessBuilder().command(command).directory(directory.toFile());
        Process p = pb.start();

        int exit = p.waitFor();

        if (exit != 0) {
            throw new AssertionError(
                    String.format("runCommand %s in %s returned %d", Arrays.toString(command), directory, exit));
        }
        return p;
    }

    public static String gobbleStream(Process p) throws IOException, InterruptedException {
        StreamGobbler errorGobbler = new StreamGobbler(p.getErrorStream(), "E");
        StreamGobbler outputGobbler = new StreamGobbler(p.getInputStream(), "O");
        outputGobbler.start();
        errorGobbler.start();
        int exit = p.waitFor();
        if (exit != 0) {
            throw new AssertionError(String.format("runCommand returned %d", exit));
        }
        errorGobbler.join();
        outputGobbler.join();
        return outputGobbler.getExecResult() + errorGobbler.getExecResult();
    }

    private static class StreamGobbler extends Thread {
        private String exec_result;

        private final InputStream is;
        private final String type;

        private StreamGobbler(InputStream is, String type) {
            this.is = is;
            this.type = type;
        }

        @Override
        public void run() {
            exec_result = ""; // Resets result variable for every new process execution
            try (BufferedReader br = new BufferedReader(new InputStreamReader(is));) {
                String line;

                while ((line = br.readLine()) != null) {
                    System.out.println(type + "> " + line);
                    exec_result += line;
                }
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }

        public String getExecResult() {
            return exec_result;
        }
    }

    @RegisterForReflection
    public static class Config {
        public String ide;
        public String clone_path;

        public Config() {
            // Default constructor
        }

        public Config(String ide, String clone_path) {
            this.ide = ide;
            this.clone_path = clone_path;

        }

        public String getIde() {
            return ide;
        }

        public void setIde(String ide) {
            this.ide = ide;
        }

        public String getClone_path() {
            return clone_path;
        }

        public void setClone_path(String clone_path) {
            this.clone_path = clone_path;
        }

    }
}
