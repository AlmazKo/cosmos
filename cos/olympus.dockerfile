FROM openjdk:17-alpine as builder
COPY . /src/
WORKDIR /src
RUN ./gradlew olympus:image -i --no-daemon

FROM alpine:3.14
EXPOSE 6666
COPY --from=builder /src/olympus/image /game/
COPY --from=builder /src/resources /resources
#COPY ../resources/ /game/res/
ENTRYPOINT /game/bin/launch
STOPSIGNAL SIGINT
#CMD ["/game/res"]
