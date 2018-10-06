FROM maven:3.5-jdk-8-alpine

COPY config.yml /opt/dropwizard/

WORKDIR /opt/dropwizard

COPY target/transactions-service-1.0-SNAPSHOT.jar /opt/dropwizard/

RUN java -jar /opt/dropwizard/transactions-service-1.0-SNAPSHOT.jar db migrate config.yml

#COPY target/h2.mv.db /opt/dropwizard/

CMD java -jar transactions-service-1.0-SNAPSHOT.jar server config.yml