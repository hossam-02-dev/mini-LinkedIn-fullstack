FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Copier le fichier jar généré localement
COPY target/miniLinkedin-0.0.1-SNAPSHOT.jar app.jar

# Exposer le port par défaut de Spring Boot (8080)
EXPOSE 8080

# Commande de démarrage
ENTRYPOINT ["java", "-jar", "app.jar"]
