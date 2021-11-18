FROM gradle:7.3-jdk17 as builder
COPY cos /src/
COPY resources /resources/
WORKDIR /src
RUN gradle api:shadowJar

FROM openjdk:17.0.1-slim
EXPOSE 443
COPY --from=builder /src/api/build/libs/api.jar /server/
COPY --from=builder /resources /resources
ENTRYPOINT java -jar /server/api.jar
STOPSIGNAL SIGINT
