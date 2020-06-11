package dk.xam.starfish;

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
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

public class Starfish implements QuarkusApplication {
  @Override
  public int run(String... args) throws Exception {   
    
    
    /*for(String s:args)
    System.out.println("\nTarget : "+s);//Printing Received Arguements */

    if(args[0].equalsIgnoreCase("config")){
    //Incase user wants to configure starfish
    editConfig();//Calling function that enables to edit configuration
    return 10; //Incase user typed "starfish config" we only want to edit configuration
    }

    //URL Validation to check a valid git repository
    if (!validate_url(args[0])){ //Incase URI doesn't  macth our scheme we'll terminate
        System.out.println("Not a valid URI for git repository");
        return 10;
    }

    

    //Configuration identifiers
    String clone_path="";//Holds path to  destination Where the Repository Must Be Clonned
    String ide="";  //Holds command for IDE to open upon
  
    File configFile= getConfigFile();//Calling functiono to fetch config file 

    //Reading Configuration File
    try {
        if (!configFile.exists()) {
            //Incase no config file exists
            editConfig(); //Calling function that lets user to configure 
        }
        
        //System.out.println("\nLoading configurations.....\n");
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
    
        Config config = mapper.readValue(configFile, Config.class);
        
        ide=config.ide;
        clone_path=config.clone_path;
        if(ide==null||clone_path==null)editConfig(); //Incase of absence of configuration in file Launch Config 
        
        
    }
    catch (Exception e) {
        e.printStackTrace();
        
    }
    


    
    String repo_name=args[0].substring(args[0].lastIndexOf("/"),args[0].lastIndexOf(".")); //Extracts the Name of Repository
   
    String originUrl = args[0];
    Path directory = Paths.get(clone_path+repo_name);

    if(!Files.exists(directory)) //Check if the user cloned the repo previously and in that case no cloning is needed
    gitClone(directory, originUrl);
   



    //Launching Editor on the Cloned Directory 
    System.out.println("Launching  Editor Now...");
    launch_editor(directory.getParent(), ide,clone_path+repo_name);
    return 10;
    
}//Main ends here


//Function to validate URL using with Regex
public static boolean validate_url(String url){
    //URL Validation to check a valid git repository
    String pattern="((git|ssh|http(s)?)|(git@[\\w\\.]+))(:(//)?)([\\w\\.@\\:/\\-~]+)(\\.git)(/)?";
    Pattern r = Pattern.compile(pattern);
    // Now create matcher object.
    Matcher m = r.matcher(url);
    return m.matches();
}

//Function yo determine if the current OS is Windows
public static boolean isWindows(){
return  System.getProperty("os.name").toLowerCase().indexOf("windows")>=0;
}

//Function to fetch config file
public static File getConfigFile(){
    String userHome = System.getProperty( "user.home" ); //Get User Home Directory: /home/user_name

    File configFile = new File(userHome + "/.config/starfish.yaml"); //Loading YAML

    return configFile;

}


//Function to edit configuration and serves for command line starfish config editor
public static void editConfig()throws Exception{
    System.out.println("\n-------------------------------------------------------");
    System.out.println("\t\tStarfish Configuration Editor");
    System.out.println("-------------------------------------------------------\n");
    String clone_path="";//Holds path to  destination Where the Repository Must Be Clonned
    String ide="";  //Holds command for IDE to open upon
   

    File configFile = getConfigFile();
    
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
            
            if(id==1){ide=isWindows()?"code.cmd":"code";System.out.println("\n--------Selected IDE:VsCode--------");break;}
            else
            if(id==2){ide="eclipse";System.out.println("\n--------Selected IDE:Eclipse--------");break;}
            else
            if(id==3){ide="idea";System.out.println("\n--------Selected IDE:IntelliJ_IDEA--------");break;}
            else
            System.out.println("\n--------Invalid Input!! Try Again--------");
            }

            //-----------Now we'll get preferred clone path on local file system from user--------------
            while(true){
            System.out.println("\n--------Enter preferred Clonning path--------");
            clone_path=reader.readLine();
            //We'll check if the path enterd by user is a valid path or not
            File tmp=new File(clone_path);
            if(tmp.exists())break;
            //Incase of Invalid path he'll be shown an error and directed to try again
            System.out.println("\n--------Invalid Path!! Try Again--------");
            }
            //----------Now we'll write configurations to the YAML FILE------------
            ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
            Config configuration=new Config(ide,clone_path);
            mapper.writeValue(configFile, configuration); //Writing to YAML File
            
        
        
    }
    catch (Exception e) {
        e.printStackTrace();
     
    }

}

//Function to Launch the Editor
public static void launch_editor(Path directory,String ide,String final_clone_path)throws IOException, InterruptedException{    
runCommand(directory.getParent(), ide,final_clone_path);//Launching the editor now


}


public static void gitClone(Path directory, String originUrl) throws IOException, InterruptedException {
    //Function for git clonning
    runCommand(directory.getParent(), "git", "clone", originUrl, directory.getFileName().toString());
}




public static String runCommand(Path directory, String... command) throws IOException, InterruptedException {
    //Function to Run Commands using Process Builder
    Process p=process_runner(directory,command);
    return gobbleStream(p);
    

}

public static Process process_runner(Path directory, String... command)throws IOException, InterruptedException{
    Objects.requireNonNull(directory, "directory");
    if (!Files.exists(directory)) {
        throw new RuntimeException("can't run command in non-existing directory '" + directory + "'");
    }
    ProcessBuilder pb = new ProcessBuilder()
            .command(command)
            .directory(directory.toFile());
    Process p = pb.start();

    int exit = p.waitFor();

    if (exit != 0) {
        throw new AssertionError(String.format("runCommand returned %d", exit));
    }
    return p;
}


public static String gobbleStream(Process p) throws IOException, InterruptedException{
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
    return outputGobbler.getExecResult()+errorGobbler.getExecResult();
}



private static class StreamGobbler extends Thread {
    private volatile String exec_result;
    private final InputStream is;
    private final String type;

    private StreamGobbler(InputStream is, String type) {
        this.is = is;
        this.type = type;
    }

    @Override
    public void run() {
        exec_result=""; //Resets result variable for every new process execution 
        try (BufferedReader br = new BufferedReader(new InputStreamReader(is));) {
            String line;

            while ((line = br.readLine()) != null) {
                System.out.println(type + "> " + line);
                exec_result+=line;
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
            
        }
    }

    public String getExecResult(){
        return exec_result;
    }
}








}
