FROM openjdk:17
ADD target/journal-backend-0.0.1-SNAPSHOT.jar journal-backend.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "journal-backend.jar"]