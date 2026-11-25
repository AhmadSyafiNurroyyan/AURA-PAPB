FROM eclipse-temurin:21-jdk-noble AS build

WORKDIR /workspace

COPY gradlew ./gradlew
COPY gradle ./gradle
COPY gradle.properties settings.gradle.kts build.gradle.kts ./
COPY src ./src

RUN sed -i 's/\r$//' gradlew \
    && chmod +x gradlew \
    && ./gradlew --no-daemon installDist

FROM eclipse-temurin:21-jre-noble AS runtime

WORKDIR /app
ENV UPLOAD_DIR=/data/uploads

COPY --from=build /workspace/build/install/aura-server/ ./

EXPOSE 8080
CMD ["./bin/aura-server"]
