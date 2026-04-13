# ── Etapa 1: Build ────────────────────────────────────────────────────────────
FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /app

# Copiamos solo el pom.xml para descargar dependencias (optimiza caché de capas)
COPY pom.xml .
RUN mvn dependency:go-offline

# Copiamos el código fuente y compilamos sin tests
COPY src ./src
RUN mvn clean package -DskipTests

# ── Etapa 2: Runtime ──────────────────────────────────────────────────────────
FROM eclipse-temurin:21-jre
WORKDIR /app

COPY --from=build /app/target/apptaxis-api-1.0.0.jar app.jar

# Puerto configurable via variable de entorno (Railway lo inyecta automáticamente)
ENV PORT=8080
EXPOSE ${PORT}

ENTRYPOINT ["sh", "-c", "java -Dserver.port=${PORT} -jar app.jar"]