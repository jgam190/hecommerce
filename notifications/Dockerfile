FROM eclipse-temurin:21-jdk

WORKDIR /app

COPY . .

RUN bash ./gradlew bootJar

EXPOSE 8085

CMD ["java", "-jar", "build/libs/notifications-0.0.1-SNAPSHOT.jar"]
