package dk.xam.starfish;
import io.quarkus.runtime.Quarkus;
import io.quarkus.runtime.annotations.QuarkusMain;

@QuarkusMain
public class JavaMain {

    public static void main(String... args) {
        Quarkus.run(Starfish.class, args);
    }
}