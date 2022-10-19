FROM openjdk:11
ARG JAR_FILE=build/libs/tourguide-1.0.0.jar
ADD ${JAR_FILE} tourguide.jar
ENTRYPOINT ["java","-jar","/tourguide.jar"]
