FROM openjdk:17-oracle as builder
COPY . /src/
WORKDIR /src
RUN ls -l && ./gradlew api:shadowJar -i

FROM openjdk:17-alpine
EXPOSE 80
COPY --from=builder /src/api/build/libs/api.jar /server/
CMD ["ls -l", "/server"]
#ENTRYPOINT java /server/api.jar
#STOPSIGNAL SIGINT
