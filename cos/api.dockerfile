FROM openjdk:15-oracle as builder
COPY . /src/
WORKDIR /src
RUN ls -l && ./gradlew api:shadowJar -i

FROM openjdk:15-alpine
EXPOSE 80
COPY --from=builder /src/api/build/libs/api.jar /server/
CMD ["java", "-Xmx200m", "--enable-preview", "/server/api.jar"]
