package dev.starfix.starfix;
import io.quarkus.runtime.Quarkus;
import io.quarkus.runtime.annotations.QuarkusMain;

@QuarkusMain
public class JavaMain {

    public static void main(String... args) {
        Quarkus.run(Starfix.class, args);
    }
}