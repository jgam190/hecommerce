FROM eclipse-temurin:21-jdk

WORKDIR /app

COPY . .

RUN bash ./gradlew bootJar

EXPOSE 8080

CMD ["java", "-jar", "build/libs/gateway-0.0.1-SNAPSHOT.jar"]
