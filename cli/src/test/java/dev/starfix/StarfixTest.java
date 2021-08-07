package dev.starfix;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;


import static dev.starfix.Starfix.validate_url;
import static dev.starfix.Starfix.runCommand;
import static dev.starfix.Starfix.isBlob;
import static dev.starfix.Starfix.processCloneURL;
import static dev.starfix.Starfix.CloneUrl;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.jupiter.api.Assertions.*;


@QuarkusTest
public class StarfixTest {

    @Test
    public void testURLValidator() {

        assertTrue(validate_url("https://github.com/user-name/repo-name.git"), "Plain github url with https");
        assertTrue(validate_url("http://github.com/user-name/repo-name.git"), "Plain github url with http");
        assertTrue(validate_url("git@github.com:user-name/repo-name.git"), "Github URL with SSH");
        assertTrue(validate_url("https://github.com/user-name/repo-name"), "Plain github url without .git");

        assertFalse(validate_url("github.com/repo-name"), "Invalid: Plain github url without any protocol");

    }

    @Test
    public void testEcho()throws Exception {
        Path directory = Paths.get(System.getProperty( "user.home" ));
        String test_string="This is some random String that I want to Echo";
        String result = runCommand(directory,"echo",test_string);
        assertEquals(test_string+System.lineSeparator(),result,"Echo Random String");
    }

    @Test
    public void testIsBlob(){

        //=== Simple Blob URL ===
        assertTrue(isBlob("https://github.com/starfixdev/starfix/blob/master/cli/pom.xml"), "Simple Blob URL");

        //=== Blob URL with Line Number
        assertTrue(isBlob("https://github.com/hexsum/Mojo-Webqq/blob/master/script/check_dependencies.pl#L17"), "Blob url having Line Number");

        //=== Blob URL with File Name Having Spaces(Encoded) ===
        assertTrue(isBlob("https://github.com/maxandersen/reallyevil/blob/main/here%20is%20a%20space.txt"), "Blob url with file name having spaces");
        
        //=== Simple Github Repository URL ===
        assertFalse(isBlob("https://github.com/user-name/repo-name"), "Simple Github Repository URL");

    }

    @Test
    public void testProcessUrl() throws UnsupportedEncodingException, URISyntaxException {
        // ==== Test Set 1: ===
        CloneUrl cloneUrl = new CloneUrl("https://github.com/maxandersen/reallyevil/blob/main/here%20is%20a%20space.txt");
        processCloneURL(cloneUrl);
        assertTrue(cloneUrl.getIsBlob());
        assertEquals("reallyevil",cloneUrl.getRepo_name(),"Repo Name");
        assertEquals("https://github.com/maxandersen/reallyevil",cloneUrl.getOriginUrl(),"Origin URL");
        assertEquals("here is a space.txt",cloneUrl.getFilePath(),"File Name with Space");
        assertEquals("main",cloneUrl.getBranch(),"Branch Name");
    }

  

}