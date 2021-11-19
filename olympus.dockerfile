FROM gradle:7.3-jdk17 as builder
COPY cos /src/
WORKDIR /src
RUN gradle olympus:image

FROM alpine:3.14
EXPOSE 6666
COPY --from=builder /src/olympus/image /game/
COPY --from=builder /src/resources /resources
##COPY ../resources/ /game/res/
CMD ["uname", "-am"]
#CMD ["file", "/game/bin/java"]
#ENTRYPOINT /game/bin/launch
#STOPSIGNAL SIGINT
##CMD ["/game/res"]
