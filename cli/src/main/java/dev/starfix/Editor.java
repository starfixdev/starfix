package dev.starfix;

import java.nio.file.Path;
import java.io.IOException;
import static dev.starfix.Starfix.runCommand;


public class Editor{

    public static void launch_editor(Path directory, String ide, String path, String filePath) throws IOException, InterruptedException {
        
        if(filePath.indexOf("#")>0){
                    filePath = filePath.replace("#L","#");
                    // ==== VS CODE ====
                    // code -g file:line
                    if(ide.equals("code")||ide.equals("code.cmd")){
                        filePath = filePath.replace("#",":");
                        runCommand(directory.getParent(), ide,"-g",path,filePath);
                    }
                    // ===== IntelliJ =====
                    // idea64.exe [--line <number>] [--column <number>] <path ...>
                    // idea /home/fahad/MyProjects/testings/jbang --line 3 /home/fahad/MyProjects/testings/jbang/otp.java
                    if(ide.equals("idea")||ide.equals("idea.exe")||ide.equals("idea64.exe")){
                        String lineNumber  = filePath.substring(filePath.lastIndexOf("#")+1);
                        filePath = filePath.substring(0,filePath.lastIndexOf("#"));
                        runCommand(directory.getParent(), ide,path,"--line",lineNumber,filePath);
                    }
                    // === Eclipse =====
                    // eclipse.exe file.txt:22 
                    if(ide.equals("eclipse")){
                        filePath = filePath.replace("#",":");
                        runCommand(directory.getParent(), ide,path,filePath);
                    }
                    
        }
        else{
             runCommand(directory.getParent(),ide,path,filePath);// Launching the editor now
        }
    }
}