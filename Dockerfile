FROM maven:3.6-jdk-8 as MMM
WORKDIR /app
COPY ./pom.xml ./pom.xml
COPY ./src ./src
RUN mvn clean package

FROM openjdk:7-jre
RUN mkdir /tgn_dbs
WORKDIR /tgn
COPY --from=MMM /app/target/tgn-1.0-TGN.jar ./
EXPOSE 8080
CMD ["java", "-Xmx3g", "-jar", "/tgn/tgn-1.0-TGN.jar", "/tgn_dbs", "8080"]
