FROM openjdk:8-jre-alpine
COPY /build/libs/kafka-interface-0.1.0.jar /usr/src/kafka-interface/
WORKDIR /usr/src/kafka-interface
CMD ["java", "-jar", "kafka-interface-0.1.0.jar"]