import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.Properties;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.*;
import org.simpleyaml.configuration.ConfigurationSection;
import org.simpleyaml.configuration.file.YamlFile;

class starfish{
    public static void main(String[]args)throws Exception{
        for(String s:args)
		System.out.println(s);//Printing Received Arguements for Debugging

		//Calling function for Fetching installed Packages 
		//fetch_installed_packages(); //This function will print all installed packages

		//Configuration identifiers
		String clone_path="";//Holds path to  destination Where the Repository Must Be Clonned
		String ide="";  //Holds command for IDE to open upon

		YamlFile yamlFile = new YamlFile("starfish-config.yml"); //Loading YAML


		//Reading input from user
		//java.io.Console cnsl=System.console();
		BufferedReader reader= new BufferedReader(new InputStreamReader(System.in));
		
		System.out.println("Do you wish to continue with previous config(Y/N) ?");
		char ch=(char)reader.read();
		reader.readLine();
		if(ch=='Y'||ch=='y'){
		//If user wants to go with previous config then
		//We'll read YAML File
		
		
		// Load the YAML file if is already created or create new one otherwise
		try {
			if (!yamlFile.exists()) {
				//Incase no config file exists
				System.out.println("New  config file has been created: " + yamlFile.getFilePath() + "\n");
				yamlFile.createNewFile(true);
			}
			else {
				System.out.println(" loading configurations...\n");
			}
			yamlFile.load(); // Loads the entire file
		} catch (Exception e) {
			e.printStackTrace();
		}
		ide=yamlFile.getString("ide");
		clone_path=yamlFile.getString("clonning_dir");

		}
		else
		{
		// we need to get configurations from user
		
		//First we'll input IDE from user
		int id=0;
		while(id!=1||id!=2||id!=3){
		System.out.println("Chose the preferred IDE \n 1.for vscode \n 2.for eclipse \n 3.for IntelliJ_IDEA ");
		
		id=Integer.parseInt(reader.readLine());
		
		if(id==1){ide="code";System.out.println("Selected IDE:VsCode");break;}
		else
		if(id==2){ide="eclipse";System.out.println("Selected IDE:Eclipse");break;}
		else
		if(id==3){ide="idea";System.out.println("Selected IDE:IntelliJ_IDEA");break;}
		else
		System.out.println("Invalid Input Try Again");
		}
		
		//Now we'll input clonning path from user
		System.out.println("Enter Clonning path or leave blank to continue with previous config");
		String temp_dir=reader.readLine();
		//System.out.println(temp_dir.equals(""));
		if(temp_dir.equals("")){
		//If user leaves the cloning path blank we'll read the previous config from YML File
		System.out.println("Blank path entered-will load from config file instead" );
		
		
		// Load the YAML file if is already created or create new one otherwise
		try {
			if (!yamlFile.exists()) {
				//Incase no config file exists
				System.out.println("New  config file has been created: " + yamlFile.getFilePath() + "\n");
				yamlFile.createNewFile(true);
			}
			else {
				System.out.println(" loading configurations...\n");
			}
			yamlFile.load(); // Loads the entire file
		} catch (Exception e) {
			e.printStackTrace();
		}
		clone_path=yamlFile.getString("clonning_dir");
		}

		else
		clone_path=temp_dir;

		//Now we will store the configurations in starfish-config.yml file

		yamlFile.set("ide",ide);                 //Storing IDE configuration in YAML file
		yamlFile.set("clonning_dir",clone_path ); //Storing  Clone Path configuration  in YAML
		//Save the New Config in YML file
		try{
			yamlFile.save();
			System.out.println("Configuration updated and saved successfully");
		}
		catch(Exception e){
			e.printStackTrace();
			System.out.print("Press any key to continue . . . ");
			reader.readLine();
		}


		}
		

		reader.readLine();

        //Clonnning Git Repo
        //Expected parameter: https://github.com/user-name/repo-name.git
        String repo_name=args[0].substring(args[0].lastIndexOf("/"),args[0].lastIndexOf(".")); //Extracts the Name of Repository
        //String clone_path= "/home/fahad/MyProjects/starfish_clonned/"; 
        String cmd = "git clone "+args[0];
        String originUrl = args[0];
		Path directory = Paths.get(clone_path+repo_name);
        gitClone(directory, originUrl);
        



        //Launching Vscode on the Cloned Directory 
        System.out.println("Launching Vscode Now");
		runCommand(directory.getParent(), ide,clone_path+repo_name);
		reader.readLine();
        
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