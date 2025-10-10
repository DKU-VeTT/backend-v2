FROM openjdk:21-jdk-slim AS build

WORKDIR /app
COPY . .
RUN chmod +x ./gradlew && ./gradlew clean build -x test

FROM openjdk:21-jdk-slim
WORKDIR /app
COPY --from=build /app/build/libs/*-SNAPSHOT.jar app.jar

ENV SPRING_PROFILES_ACTIVE=prod \
    TZ=Asia/Seoul

EXPOSE 8761
ENTRYPOINT ["java", "-jar", "app.jar"]