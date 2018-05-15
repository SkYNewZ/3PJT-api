FROM maven:3.5.3-jdk-8-alpine as BUILDER
WORKDIR /app
COPY . .
RUN mvn clean install -DskipTests

FROM java:8u111-jdk
WORKDIR /app
COPY --from=BUILDER /app/target/supdrive.jar .
ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=prod", "supdrive.jar"]
