# Backend de SGVA: compila el JAR y lo ejecuta con Java 17.
FROM eclipse-temurin:17-jdk AS compilacion
WORKDIR /app
COPY . .
RUN ./gradlew bootJar -x test --no-daemon

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=compilacion /app/build/libs/sgva-*-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
