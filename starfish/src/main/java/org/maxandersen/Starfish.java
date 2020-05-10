package org.maxandersen;

import io.quarkus.runtime.QuarkusApplication;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.io.File;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

public class Starfish implements QuarkusApplication {
  @Override
  public int run(String... args) throws Exception {   
    
    
    for(String s:args)
    System.out.println("\nTarget : "+s);//Printing Received Arguements for Debugging

    if(args[0].equalsIgnoreCase("config")){
    //Incase user wants to configure starfish
    editConfig();//Calling function that enables to edit configuration
    return 10; //Incase user typed "starfish config" we only want to edit configuration
    }

    //Calling function for Fetching installed Packages 
    //fetch_installed_packages(); //This function will print all installed packages

    //Configuration identifiers
    String clone_path="";//Holds path to  destination Where the Repository Must Be Clonned
    String ide="";  //Holds command for IDE to open upon
    String userHome = System.getProperty( "user.home" ); //Get User Home Directory: /home/user_name

    File configFile = new File(userHome + "/starfish-config.yml"); //Loading YAML


  
    
    
    //Reading Configuration File
    try {
        if (!configFile.exists()) {
            //Incase no config file exists
            System.out.println("\nThis is the first time you're using starfish...You'll need to  configure it");
            editConfig(); //Calling function that lets user to configure 
        }
        
        System.out.println("\nLoading configurations.....\n");
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
    
        Config config = mapper.readValue(configFile, Config.class);
        
        ide=config.ide;
        clone_path=config.clone_path;
        if(ide==null||clone_path==null)editConfig(); //Incase of absence of configuration in file Launch Config 
        
        
    }
    catch (Exception e) {
        e.printStackTrace();
        System.out.print("Press any key to continue . . . ");
    }
    


    //Clonnning Git Repo
    //Expected parameter: https://github.com/user-name/repo-name.git
    String repo_name=args[0].substring(args[0].lastIndexOf("/"),args[0].lastIndexOf(".")); //Extracts the Name of Repository
    //String clone_path= "/home/fahad/MyProjects/starfish_clonned/"; 
    String originUrl = args[0];
    Path directory = Paths.get(clone_path+repo_name);
    gitClone(directory, originUrl);
    



    //Launching Vscode on the Cloned Directory 
    System.out.println("Launching  IDE Now");
    runCommand(directory.getParent(), ide,clone_path+repo_name);
    return 10;
    
}


//Function to edit configuration and serves for command line starfish config
public static void editConfig()throws Exception{
System.out.println("\n------Starfish Configuration Editor------");
String clone_path="";//Holds path to  destination Where the Repository Must Be Clonned
    String ide="";  //Holds command for IDE to open upon
    String userHome = System.getProperty( "user.home" ); //Get User Home Directory: /home/user_name

    File configFile = new File(userHome + "/starfish-config.yml"); //Loading YAML
    
    BufferedReader reader= new BufferedReader(new InputStreamReader(System.in));
    
    
    //Reading Configuration File
    try {
        if (!configFile.exists()) {
            //Incase no config file exists
            System.out.println("\n-No configurations exist for starfish...You'll have to  configure it");
            System.out.println("\n-New  configuration file will be created at: " + configFile.getAbsolutePath() + "\n");
            configFile.createNewFile();
            }
            
            
            //--------------------------First we'll input preferred IDE from user------------------------------

            int id=0;
            while(id!=1||id!=2||id!=3){
            System.out.println("\n--------Chose the preferred IDE --------\n 1.for vscode \n 2.for eclipse \n 3.for IntelliJ_IDEA ");
            
            id=Integer.parseInt(reader.readLine());
            
            if(id==1){ide="code";System.out.println("Selected IDE:VsCode");break;}
            else
            if(id==2){ide="eclipse";System.out.println("Selected IDE:Eclipse");break;}
            else
            if(id==3){ide="idea";System.out.println("Selected IDE:IntelliJ_IDEA");break;}
            else
            System.out.println("--------Invalid Input Try Again--------");
            }

            //-----------Now we'll get preferred clone path on local file system from user--------------
            System.out.println("--------Enter preferred Clonning path--------");
            clone_path=reader.readLine();

            //----------Now we'll write configurations to the YAML FILE------------
            ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
            Config configuration=new Config(ide,clone_path);
            mapper.writeValue(configFile, configuration); //Writing to YAML File
            
        
        
    }
    catch (Exception e) {
        e.printStackTrace();
        System.out.print("Press any key to continue . . . ");
        reader.readLine();
    }

}

public static void gitClone(Path directory, String originUrl) throws IOException, InterruptedException {
    //Function for git clonning
    runCommand(directory.getParent(), "git", "clone", originUrl, directory.getFileName().toString());
}




public static void runCommand(Path directory, String... command) throws IOException, InterruptedException {
    //Function to Run Commands using Process Builder
    
    Objects.requireNonNull(directory, "directory");
    if (!Files.exists(directory)) {
        throw new RuntimeException("can't run command in non-existing directory '" + directory + "'");
    }
    ProcessBuilder pb = new ProcessBuilder()
            .command(command)
            .directory(directory.toFile());
    Process p = pb.start();
    StreamGobbler errorGobbler = new StreamGobbler(p.getErrorStream(), "E");
    StreamGobbler outputGobbler = new StreamGobbler(p.getInputStream(), "O");
    outputGobbler.start();
    errorGobbler.start();
    int exit = p.waitFor();
    errorGobbler.join();
    outputGobbler.join();
    if (exit != 0) {
        throw new AssertionError(String.format("runCommand returned %d", exit));
    }
}



private static class StreamGobbler extends Thread {

    private final InputStream is;
    private final String type;

    private StreamGobbler(InputStream is, String type) {
        this.is = is;
        this.type = type;
    }

    @Override
    public void run() {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(is));) {
            String line;
            while ((line = br.readLine()) != null) {
                System.out.println(type + "> " + line);
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
            
        }
    }
}


//Function to fetch all installed packages on System
public static void fetch_installed_packages()throws Exception{
    
    String [] extCmdArgs = new String[]{"dpkg-query", "-W", "-f=${Package} ${Version}\n"};// Fetches installed Packages on System
    //String [] extCmdArgs = new String[]{"sudo apt list --installed};
    
    Process fetchInstalledPackagesProcess = Runtime.getRuntime().exec(extCmdArgs);
    BufferedReader reader = null;
            try {
                reader = new BufferedReader(new InputStreamReader(fetchInstalledPackagesProcess.getInputStream()));
                String line;
                while(true){
                    line = reader.readLine();
                    if (line == null)
                        break;
                    System.out.println(line);
            }
        } catch (Exception e) {
                e.printStackTrace();
        }
        finally {
            reader.close();
        }


}





}