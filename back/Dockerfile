FROM openjdk:17-jdk-alpine

CMD ["./gradlew", "clean", "build"]

VOLUME /tmp

ARG JAR_FILE=build/libs/*SNAPSHOT.jar

#ARG SPRING_DATASOURCE_URL \SPRING_DATASOURCE_USERNAME \
#    SPRING_DATASOURCE_PASSWORD \
#    JWT_KEY \
#    CONSUMER_KEY \
#    CONSUMER_SECRET \
#    SHORT_TERM_FORECAST_API_KEY

COPY ${JAR_FILE} app.jar

EXPOSE 8080

ENTRYPOINT ["java","-jar","/app.jar"]