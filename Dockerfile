FROM eclipse-temurin:17-jre-alpine AS bundle
EXPOSE 80 88
COPY ./target/*.jar /opt/application/app.jar
COPY ./src/main/resources/application.yml /opt/application/config/application.yml
WORKDIR /opt/application/
ENTRYPOINT ["java", "-jar", "/opt/application/app.jar"]