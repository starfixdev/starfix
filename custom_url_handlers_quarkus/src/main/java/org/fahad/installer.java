package org.fahad;

import java.io.*;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/install")
public class installer {

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String install()throws Exception {
        System.out.println("Welcome to Installer");
        String OS = System.getProperty("os.name").toLowerCase();
        System.out.println("Your Operating System is "+ OS);

        if(OS.indexOf("mac") >= 0){
        //MAC OS Detected

            Runtime run = Runtime.getRuntime();  
            //The best possible I found is to construct a command which you want to execute  
            //as a string and use that in exec. If the batch file takes command line arguments  
            //the command can be constructed a array of strings and pass the array as input to  
            //the exec method. The command can also be passed externally as input to the method.  

            Process p = null;  
            String cmd = "mv ide_handler.app ~/Applications";  //Moving to Applications
            try {  
                p = run.exec(cmd);  

                p.getErrorStream();  
                p.waitFor();

            }  
            catch (Exception e) {  
                e.printStackTrace();  
                System.out.println("ERROR While Moving to Applications");  
                return "unable to install";
            }finally{
                p.destroy();
            }

            cmd = "open -a ide_handler"; //Executing our Application
            try {  
                p = run.exec(cmd);  

                p.getErrorStream();  
                p.waitFor();
                System.out.println("installation successful");
                return "Detected Operationg System is" + OS + "\nInstallation successful";
            }  
            catch (Exception e) {  
                e.printStackTrace();  
                System.out.println("ERROR while executing application");  
                return "Detected Operationg System is"+ OS +"\nSorry!!unable to install :( \nCheck logs to know more";
            }finally{
                p.destroy();
            }
            

        }



        if(OS.indexOf("nux") >= 0){
        //Linux OS Detected
         Runtime run = Runtime.getRuntime();  
            //The best possible I found is to construct a command which you want to execute  
            //as a string and use that in exec. If the batch file takes command line arguments  
            //the command can be constructed a array of strings and pass the array as input to  
            //the exec method. The command can also be passed externally as input to the method.  

            Process p = null;  
            String cmd = "sudo bash install";  
            try {  
                p = run.exec(cmd);  

                p.getErrorStream();  
                p.waitFor();
                System.out.println("installation successful");
                return "Detected Operationg System is "+ OS +"\nInstallation Successful!!";
            }  
            catch (Exception e) {  
                e.printStackTrace();  
                System.out.println("ERROR.RUNNING.CMD");  
                return "Detected Operationg System is"+ OS + "\nSorry!!unable to install :( \nCheck logs for errors";
            }finally{
                p.destroy();
            }  
           

        }
        else{
            return "Detected OS: "+OS +"\nYour OS Not Supported..We are working on it!! ";
        }
        
        
    }
}