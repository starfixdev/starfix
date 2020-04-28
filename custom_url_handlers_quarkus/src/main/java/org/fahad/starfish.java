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
import org.simpleyaml.configuration.ConfigurationSection;
import org.simpleyaml.configuration.file.YamlFile;

class starfish{
    public static void main(String[]args)throws Exception{
        for(String s:args)
		System.out.println(s);//Printing Received Arguements for Debugging

		//Calling function for Fetching installed Packages 
		fetch_installed_packages(); //This function will print all installed packages

		
		

		String clone_path="";//Holds path to  destination Where the Repository Must Be Clonned
		String ide="";  //Holds command for IDE to open upon

		//Fetching Configurations from Config File: starfish.yaml
		YamlFile yamlFile = new YamlFile("starfish-config.yml");
		
		// Load the YAML file if is already created or create new one otherwise
		try {
			if (!yamlFile.exists()) {
				System.out.println("New file has been created: " + yamlFile.getFilePath() + "\n");
				yamlFile.createNewFile(true);
			}
			else {
				System.out.println("File already exists, loading configurations...\n");
			}
			yamlFile.load(); // Loads the entire file
		} catch (Exception e) {
			e.printStackTrace();
		}

		ide=yamlFile.getString("ide");
		clone_path=yamlFile.getString("clonning_dir");




		/*Fetching Configurations from Config.properties File
		try{
		File propertiesFile = new File("config.properties");
        FileReader reader = new FileReader(propertiesFile);
        Properties props = new Properties();
        props.load(reader);
		String test = props.getProperty("test");
		System.out.println(test);
		clone_path= props.getProperty("clonning_dir");//Place Where the Repository Must Be Clonned
		ide=props.getProperty("ide"); //IDE to open upon
		}
		catch(Exception e){
			e.printStackTrace();
			System.out.println(e.getMessage());
		}*/






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