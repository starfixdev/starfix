
import java.io.*;
import java.util.Scanner;

public class FileOpener {
    static Runtime rt           = Runtime.getRuntime();
    static Scanner input        = new Scanner(System.in);
    // all commands must always contain a '?' one and only at end.
    static String[] commands    = {"open-file/?", "clone-url/?"};

    public static void main(String[] args){
        if (args.length == 0 || args[0].length() <= 4) {
            // Only ide: specified (no commands specified)
            return;
        }

        System.out.println("\n\t*******Starting*******");
        System.out.println("MAIN => " + args[0]);

        String[] op_data = parseInput(args);

        if (op_data == null || op_data.length != 2) {
            System.out.println("UNKNOWN COMMAND");
            input.nextLine();
            return;
        } else {
            switch(op_data[0]) {
                case "open-file/":
                    openFile(op_data[1]);
                    break;
                case "clone-url/":
                    System.out.println("cloning url=>" + op_data[1] );
                    isGitLink(op_data[1]+".git");
                    cloneURL(op_data[1]);
                    break;
                default:
                    System.out.println("Do you know what you're typing.");
            }
        }
        System.out.println("IN MAIN");
        input.nextLine();
    }

    /**
     * Input as URI I can get
     * __while accessing file
     *      ide://path(represented as link)
     *      ide:path
     *      ide:\\path
     *      ide:
     * __while accessing link
     *      ide:https://wwww.jobhi/
     *      ide:\\https://LINK
     *      ide://link
     *      ide\\ => ide%5C%5C
     *      ide:link
     */

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
        link = link.substring(6); // 6 is length of ide://

        System.out.println("parseInput => " + link); //FIXME:

        String link_LC = link.toLowerCase();

        for (String command : commands) {
            if (link_LC.startsWith(command) ) {
                System.out.println("IDENTIFIED ");
                returnFile = link.split("[?]", 2);
            }
        }


        // for (String com : returnFile) System.out.println("returnFile => " + com);

        return returnFile;
    }

    public static void openFile(String path) {
        try {
            System.out.println("GOT HERE => " + path);
            path = path.replace("\\", "/");
            System.out.println(path);
            rt.exec("cmd /c start vscode://file/" + path); //FIXME: call cli code function
            System.out.println("-->EXECUTION COMPLETE");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean isGitLink(String path) {
        return true;
    }

    public static void cloneURL(String link) {
        System.out.println(link);
        System.out.print("Specify Path : ");
        String path = input.nextLine(); // TODO: Create Path object and check this path
        System.out.println(link +  " <= link\n" + "path => " + path);
        String[] parts_of_link = link.split("/");
        String folderName = parts_of_link[parts_of_link.length - 1];

        try {
            Process proc = rt.exec("git clone " + link + " " + path + "/" + folderName);
            proc.waitFor();
        } catch (IOException|InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Successfully Cloned");
    }
}