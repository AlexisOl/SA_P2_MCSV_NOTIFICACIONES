FROM bellsoft/liberica-openjdk-alpine:21.0.3

WORKDIR /app

COPY target/mcsv-anuncios.jar /app/mcsv-anuncios.jar

EXPOSE 8084

ENTRYPOINT ["java", "-jar", "mcsv-anuncios.jar"]
