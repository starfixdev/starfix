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
		System.out.println("\nTarget : "+s);//Printing Received Arguements for Debugging

		if(args[0].equalsIgnoreCase("config")){
		//Incase user wants to configure starfish
		editConfig();//Calling function that enables to edit configuration
		return; //Incase user typed "starfish config" we only want to edit configuration
		}

		//Calling function for Fetching installed Packages 
		//fetch_installed_packages(); //This function will print all installed packages

		//Configuration identifiers
		String clone_path="";//Holds path to  destination Where the Repository Must Be Clonned
		String ide="";  //Holds command for IDE to open upon
		String userHome = System.getProperty( "user.home" ); //Get User Home Directory: /home/user_name

		YamlFile yamlFile = new YamlFile(userHome + "/starfish-config.yml"); //Loading YAML


		
		BufferedReader reader= new BufferedReader(new InputStreamReader(System.in));
		
		
		//Reading Configuration File
		try {
			if (!yamlFile.exists()) {
				//Incase no config file exists
				System.out.println("\nThis is the first time you're using starfish...You'll need to  configure it");
				editConfig(); //Calling function that lets user to configure 
			}
			
			System.out.println("\nLoading configurations.....\n");
			yamlFile.load(); // Loads the entire file
			ide=yamlFile.getString("ide");
			clone_path=yamlFile.getString("clonning_dir");
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
        String cmd = "git clone "+args[0];
        String originUrl = args[0];
		Path directory = Paths.get(clone_path+repo_name);
        gitClone(directory, originUrl);
        



        //Launching Vscode on the Cloned Directory 
        System.out.println("Launching  IDE Now");
		runCommand(directory.getParent(), ide,clone_path+repo_name);
		
        
    }


	//Function to edit configuration and serves for command line starfish config
	public static void editConfig()throws Exception{
	System.out.println("\n------Starfish Configuration Editor------");
	String clone_path="";//Holds path to  destination Where the Repository Must Be Clonned
		String ide="";  //Holds command for IDE to open upon
		String userHome = System.getProperty( "user.home" ); //Get User Home Directory: /home/user_name

		YamlFile yamlFile = new YamlFile(userHome + "/starfish-config.yml"); //Loading YAML
		
		BufferedReader reader= new BufferedReader(new InputStreamReader(System.in));
		
		
		//Reading Configuration File
		try {
			if (!yamlFile.exists()) {
				//Incase no config file exists
				System.out.println("\n-No configurations exist for starfish...You'll have to  configure it");
				System.out.println("\n-New  configuration file will be created at: " + yamlFile.getFilePath() + "\n");
				yamlFile.createNewFile(true);
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

				//---------Save the received configurations in /home/user_name/starfish-config.yml
				yamlFile.set("ide",ide);                 //Storing IDE configuration in YAML file
				yamlFile.set("clonning_dir",clone_path ); //Storing  Clone Path configuration  in YAML
				//--------Save the New Config in YML file-------
				try{
					yamlFile.save();
					System.out.println("--------Configuration updated and saved successfully--------");
				}
				catch(Exception e){
					e.printStackTrace();
					System.out.println("Couldn't save the configurations :(");
					System.out.print("Press any key to continue . . . ");
					reader.readLine();
				}
			
			
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