# Dockerfile

# Imagen base de Java
FROM adoptopenjdk:17-jdk-hotspot

# Directorio de trabajo en el contenedor
WORKDIR /app

# Copiar los archivos generados por Maven
COPY target/*.jar /app/app.jar

# Exponer el puerto 8080
EXPOSE 8080

# Comando para ejecutar la aplicación Spring Boot
CMD ["java", "-jar", "/app/app.jar"]
