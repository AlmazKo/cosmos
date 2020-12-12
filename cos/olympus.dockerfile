FROM openjdk:15-oracle as builder
COPY . /src/
WORKDIR /src
RUN ./gradlew olympus:image -i

FROM greyfoxit/alpine-glibc
EXPOSE 6666
COPY --from=builder /src/olympus/image /game/
COPY res/ /game/res/
ENTRYPOINT ["/game/bin/launch"]
STOPSIGNAL SIGINT
CMD ["/game/res"]
