FROM eclipse-temurin:21-jdk

WORKDIR /app

COPY . .

RUN ./gradlew bootJar

EXPOSE 8080

CMD ["java", "-jar", "build/libs/products-0.0.1-SNAPSHOT.jar"]