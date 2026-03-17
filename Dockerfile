# STAGE 1: Build con Maven
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY . .
# Eseguiamo i test durante la build. Se falliscono, la build si ferma!
RUN mvn clean package

# STAGE 2: Runtime leggero
FROM eclipse-temurin:21-jre
WORKDIR /app

# Copiamo il JAR generato
COPY --from=build /app/target/quiz-server.jar quiz-server.jar
# Copiamo il database delle domande
COPY src/main/resources/Questions.json Questions.json

# Esponiamo la porta del registro RMI
EXPOSE 1099

# Configurazione RMI fondamentale per Docker
# Sostituiamo localhost con l'hostname del container
ENTRYPOINT ["java", "-Djava.rmi.server.hostname=server-quiz", "-jar", "quiz-server.jar"]