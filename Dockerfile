# Build con Maven
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY . .
# Eseguiamo i test durante la build, se falliscono la build si ferma
RUN mvn clean package

# Runtime leggero
FROM eclipse-temurin:21-jre
WORKDIR /app

# Copiamo il JAR generato
COPY --from=build /app/target/quiz-server.jar quiz-server.jar
# Copiamo il database delle domande
COPY src/main/resources/Questions.json Questions.json

# Esponiamo la porta del registro RMI
EXPOSE 1099

# Sostituiamo localhost con l'hostname del container
ENTRYPOINT ["java", "-Djava.rmi.server.hostname=server-quiz", "-jar", "quiz-server.jar"]