
import java.io.*;
import java.util.Scanner;

/**
 * This program handles the URI
 */

public class FileOpener {
    static String OSName = System.getProperty("os.name").toLowerCase();
    static Runtime rt           = Runtime.getRuntime();
    static Scanner input        = new Scanner(System.in);
    // all commands must always contain a '?' one and only at end.
    static String[] commands    = {"open-file?", "clone-url?", "open-file/?", "clone-url/?"};
    static String path = null;
    public static void main(String[] args){
        if (args.length == 0 || args[0].length() <= 4) {
            // Only ide: specified (no commands specified)
            printUsage();
            return;
        }

        System.out.println("\n\t*******Starting*******");

        String[] op_data = parseInput(args);

        if (op_data == null || op_data.length != 2) {
            System.out.println("Invalid Input");
            input.nextLine();
            return;
        } else {
            switch(op_data[0]) {
                case "open-file":
                case "open-file/":
                    openFileInVSCode(op_data[1]);
                    break;
                case "clone-url":
                case "clone-url/":
                    isGitLink(op_data[1]+".git");
                    cloneURL(op_data[1]);
                    if (path != null) 
                        openFileInVSCode(path);
                    else
                        System.out.println("Cannot open file");
                    break;
                default:
                    System.out.println("Unknown Command " + op_data[0]);
            }
        }
        System.out.println("Press Enter : " );
        input.nextLine();
    }

    /**
     * returns command and link as String[] in this order
     *          if no command found then returns null;
     * */
    public static String[] parseInput(String[] args) {
        String[] returnFile = null;
        String link = args[0];

        if (!link.startsWith("ide://") ) {
            return null;
        }

        //remove ide:// from link
        link = link.substring(6);

        String link_LC = link.toLowerCase();

        for (String command : commands) {
            if (link_LC.startsWith(command) ) {
                returnFile = link.split("[?]", 2);
            }
        }

        return returnFile;
    }

    public static void openFileInVSCode(String path) {
        try {
            path = path.replace("\\", "/");
            System.out.println(path);
            if (OSName.contains("win") )
                rt.exec("cmd /c start vscode://file/" + path); //FIXME: call cli code function
            else if (OSName.contains("linux") ) {
                rt.exec("xdg-open vscode://file/" + path);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //FIXME: check if it's a valid git repository
    public static boolean isGitLink(String path) {
        return true;
    }

    public static void cloneURL(String link) {
        System.out.print("Specify Path : ");
        path = input.nextLine(); // TODO: Create Path object and check this path
        
        String[] parts_of_link = link.split("/");
        String folderName = parts_of_link[parts_of_link.length - 1];

        try {
            Process proc = rt.exec("git clone " + link + " " + path + "/" + folderName);
            proc.waitFor();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("Path Not Found = " + path);
        }
    }

    public static void printUsage() {
        System.out.println("url -> ide://open-file?path_to_file  - to open file");
        System.out.println("write ide://clone-url? before url link in browser git to clone and open the repository");
        System.out.println("\t\t It should be valid git repository");
    }
}