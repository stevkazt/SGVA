# SGVA con el panel de pruebas: la puerta de la demo (Node), los backends (Java 17)
# y el frontend con el panel, todo en un solo contenedor.
FROM eclipse-temurin:17-jdk AS compilacion
WORKDIR /app
COPY . .
RUN ./gradlew bootJar -x test --no-daemon

FROM node:22-bookworm-slim
COPY --from=eclipse-temurin:17-jre /opt/java/openjdk /opt/java/openjdk
ENV JAVA_HOME=/opt/java/openjdk PATH=/opt/java/openjdk/bin:$PATH
RUN apt-get update && apt-get install -y --no-install-recommends postgresql-client curl \
    && rm -rf /var/lib/apt/lists/*
WORKDIR /app
COPY --from=compilacion /app/build/libs/sgva-*-SNAPSHOT.jar sgva.jar
COPY demo demo
COPY src/test/resources/fixtures src/test/resources/fixtures
COPY frontend frontend
RUN cd frontend && npm ci
ENV SGVA_JAR=/app/sgva.jar LOGS_DIR=/app/demo/logs PGUSER=sgva
EXPOSE 5173
CMD ["demo/entrada-contenedor.sh"]
