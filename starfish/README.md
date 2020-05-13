# starfish project
This apps receives a git repository URI,clones the repository and opens an editor(vscode,eclipse,idea etc) on it 
![Starfish](https://github.com/fahad-israr/Images-for-Readme/raw/master/starfish.png)

#### This sub-directory contains the source files for the starfish application. Insatallation and Usage are elaborated in the `../install/os_name` directory as some instructions are platform specific.

This project uses Quarkus, the Supersonic Subatomic Java Framework.

If you want to learn more about Quarkus, please visit its website: https://quarkus.io/ .

## Running the application in dev mode

- You can run your application in dev mode that enables live coding using:
```
./mvnw quarkus:dev
```

- Incase you want to pass arguements you may use:
```
mvn compile quarkus:dev -Dquarkus.args='your_arguement_here'
```

## Packaging and running the application

The application can be packaged using `./mvnw package`.
It produces the `starfish-1.0.0-SNAPSHOT-runner.jar` file in the `/target` directory.
Be aware that it’s not an _über-jar_ as the dependencies are copied into the `target/lib` directory.

The application is now runnable using `java -jar target/starfish-1.0.0-SNAPSHOT-runner.jar`.

## Creating a native executable

You can create a native executable using: `./mvnw package -Pnative`.

Or, if you don't have GraalVM installed, you can run the native executable build in a container using: `./mvnw package -Pnative -Dquarkus.native.container-build=true`.

You can then execute your native executable with: `./target/starfish-1.0.0-SNAPSHOT-runner`

If you want to learn more about building native executables, please consult https://quarkus.io/guides/building-native-image.
