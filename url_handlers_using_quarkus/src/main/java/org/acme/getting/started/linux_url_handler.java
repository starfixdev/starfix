package org.acme.getting.started;
import java.io.*;
/*Adding Custom URL Handler to Linux for ide:// */
 public class linux_url_handler{
     public static void main(String[] args) throws Exception{

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

            }  
            catch (Exception e) {  
                e.printStackTrace();  
                System.out.println("ERROR.RUNNING.CMD");  

            }finally{
                p.destroy();
            }  
     }
 }