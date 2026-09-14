FROM eclipse-temurin:21-jre

WORKDIR /app

COPY target/stats-server-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 9090

ENTRYPOINT ["java","-Duser.timezone=UTC", "-jar", "app.jar"]