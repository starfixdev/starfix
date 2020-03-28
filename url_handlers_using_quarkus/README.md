# Starfish

This project uses Quarkus, the Supersonic Subatomic Java Framework.

If you want to learn more about Quarkus, please visit its website: https://quarkus.io/ .

## Running the application in dev mode

You can run your application in dev mode that enables live coding using:
```
./mvnw quarkus:dev
```

## Packaging and running the application

The application can be packaged using `./mvnw package`.
It produces the `getting-started-1.0-SNAPSHOT-runner.jar` file in the `/target` directory.
Be aware that it’s not an _über-jar_ as the dependencies are copied into the `target/lib` directory.

The application is now runnable using `java -jar target/getting-started-1.0-SNAPSHOT-runner.jar`.

## Creating a native executable

You can create a native executable using: `./mvnw package -Pnative`.

Or, if you don't have GraalVM installed, you can run the native executable build in a container using: `./mvnw package -Pnative -Dquarkus.native.container-build=true`.

You can then execute your native executable with: `./target/getting-started-1.0-SNAPSHOT-runner`


## Creating a container

By default, the native executable is tailored for your operating system (Linux, macOS, Windows etc). Because the container may not use the same executable format as the one produced by your operating system,
- we will instruct the Maven build to produce an executable from inside a container:

`./mvnw package -Pnative -Dquarkus.native.container-build=true`

- You can also select the container runtime to use with:

    ``` 
    # Docker
    ./mvnw package -Pnative -Dquarkus.native.container-runtime=docker
    # Podman
    ./mvnw package -Pnative -Dquarkus.native.container-runtime=podman
    ```

These are normal Quarkus config properties, so if you always want to build in a container it is recommended you add these to your `application.properties` so you do not need to specify them every time.

- you can build the docker image with:
`docker build -f src/main/docker/Dockerfile.native -t quarkus-quickstart/getting-started`

- And finally, run it with:
`docker run -i --rm -p 8080:8080 quarkus-quickstart/getting-started`
