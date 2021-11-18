FROM gradle:7.3-jdk17 as builder
COPY cos /src/
WORKDIR /src
RUN ./gradlew olympus:image -i --no-daemon

FROM openjdk:17.0.1-slim
RUN uname -am
EXPOSE 6666
COPY --from=builder /src/olympus/image /game/
COPY --from=builder /src/resources /resources
##COPY ../resources/ /game/res/
#CMD ["uname", "-am"]
ENTRYPOINT /game/bin/launch
STOPSIGNAL SIGINT
##CMD ["/game/res"]
