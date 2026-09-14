FROM gradle:jdk25 AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle buildFatJar --no-daemon

FROM eclipse-temurin:25-jre-alpine

ENV TZ=Europe/Warsaw
RUN apk add --no-cache tzdata \
    && cp /usr/share/zoneinfo/$TZ /etc/localtime \
    && echo $TZ > /etc/timezone \
    && apk del tzdata

RUN addgroup -S appgroup && adduser -S appuser -G appgroup

WORKDIR /app
RUN chown -R appuser:appgroup /app
COPY --from=build /home/gradle/src/build/libs/*-all.jar /app/ktor-backend.jar

USER appuser

EXPOSE 8080
ENTRYPOINT ["java", "-Duser.timezone=Europe/Warsaw", "-XX:MaxRAMPercentage=75.0", "-jar", "/app/ktor-backend.jar"]