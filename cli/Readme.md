# Frequently used Commands used while development: 

### Development mode:
`./mvnw compile quarkus:dev `

Running with Arguments:
`./mvnw compile quarkus:dev -Dquarkus.args="some arguments here"`

### Packaging:
- Mutable-jar: `./mvnw package`

- Native Executable: `./mvnw package -Dnative`