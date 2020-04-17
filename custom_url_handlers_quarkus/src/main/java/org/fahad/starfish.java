import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

class starfish{
    public static void main(String[]args)throws Exception{
        for(String s:args)
        System.out.println(s);//Printing Received Arguements for Debugging



        //Clonnning Git Repo
        //Expected parameter: https://github.com/user-name/repo-name.git
        String repo_name=args[0].substring(args[0].lastIndexOf("/"),args[0].lastIndexOf(".")); //Extracts the Name of Repository
        String clone_path= "/home/fahad/MyProjects/starfish_clonned/"; //Place Where the Repository Must Be Clonned
        String cmd = "git clone "+args[0];
        String originUrl = args[0];
		Path directory = Paths.get(clone_path+repo_name);
        gitClone(directory, originUrl);
        



        //Launching Vscode on the Cloned Directory 
        System.out.println("Launching Vscode Now");
        runCommand(directory.getParent(), "code",clone_path+repo_name);
        
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
		StreamGobbler errorGobbler = new StreamGobbler(p.getErrorStream(), "ERROR");
		StreamGobbler outputGobbler = new StreamGobbler(p.getInputStream(), "OUTPUT");
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
    

 
    

}