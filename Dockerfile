FROM openjdk:19
#ARG JAR_FILE=target/*.jar

#COPY ${JAR_FILE} transcenda-latest.jar
COPY target/transcenda.jar transcenda.jar
ENTRYPOINT ["java","-jar","transcenda.jar"]

EXPOSE 8080

