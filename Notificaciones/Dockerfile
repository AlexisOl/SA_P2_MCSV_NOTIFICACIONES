FROM bellsoft/liberica-openjdk-alpine:21.0.3

WORKDIR /app

COPY target/mcsv-notificaciones.jar /app/mcsv-notificaciones.jar

EXPOSE 8089

ENTRYPOINT ["java", "-jar", "mcsv-notificaciones.jar"]
