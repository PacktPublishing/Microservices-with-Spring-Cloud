FROM openjdk:8-jre-alpine3.8
RUN apk --update add curl
HEALTHCHECK CMD curl -v --fail http://localhost:11111/actuator/info || exit 1

COPY target/service.jar /usr/app/service.jar
EXPOSE 8080 11111
ENV ENVIRONMENT="local"
ENV DB_NAME="db"
ENTRYPOINT  java -Djava.net.preferIPv4Stack=true -XX:+UnlockExperimentalVMOptions -XX:+UseCGroupMemoryLimitForHeap -XX:MaxRAMFraction=1 -jar /usr/app/service.jar --spring.profiles.active=$ENVIRONMENT,docker --db.name=$DB_NAME --spring.cloud.config.uri=http://configserver:8888