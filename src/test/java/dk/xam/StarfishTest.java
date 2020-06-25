package dk.xam;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.IOException;



import static dk.xam.starfish.Starfish.validate_url;
import static dk.xam.starfish.Starfish.launch_editor;
import static dk.xam.starfish.Starfish.isWindows;
import static dk.xam.starfish.Starfish.runCommand;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;



@QuarkusTest
public class StarfishTest {

    @Test
    public void testURLValidator() {
        
        assertEquals(true,validate_url("https://github.com/user-name/repo-name.git"),"Plain github url with https");
        assertEquals(true,validate_url("http://github.com/user-name/repo-name.git"),"Plain github url with http");
        assertEquals(true,validate_url("git@github.com:user-name/repo-name.git"),"Github URL with SSH");

        assertEquals(false,validate_url("git@github.com:user-name/repo-name"),"Invalid Github URL with SSH");
        assertEquals(false,validate_url("https://github.com/repo-name"),"Invalid Plain github url");


        
    }

    @Test
    public void testEcho()throws Exception {
        Path directory = Paths.get(System.getProperty( "user.home" ));
        String test_string="This is some random String that I want to Echo";
        assertEquals(test_string,runCommand(directory,"echo",test_string),"Echo Random String");
    }

  

}