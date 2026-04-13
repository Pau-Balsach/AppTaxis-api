# Etapa 1: Usamos una imagen que YA tiene Maven y Java 21
FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /app

# Copiamos solo el pom.xml para descargar dependencias (optimiza caché)
COPY pom.xml .
RUN mvn dependency:go-offline

# Copiamos el código y compilamos
COPY src ./src
RUN mvn clean package -DskipTests

# Etapa 2: Imagen ligera para ejecutar
FROM eclipse-temurin:21-jre
WORKDIR /app
# El archivo JAR se genera en /app/target/
COPY --from=build /app/target/apptaxis-api-1.0.0.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]