FROM openjdk:17
ADD target/journal-backend-0.0.1-SNAPSHOT.jar journal-backend.jar
COPY wait-for-it.sh wait-for-it.sh
EXPOSE 8080
ENTRYPOINT ["./wait-for-it.sh", "db:3306", "--", "java", "-jar", "journal-backend.jar"]