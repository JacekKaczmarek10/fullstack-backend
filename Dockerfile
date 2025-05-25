FROM maven:3.9.6-amazoncorretto-21 AS build
WORKDIR /build
COPY . .
RUN mvn clean install -Dmaven.test.skip=true

FROM openjdk:21
COPY --from=build /build/target/fullstack-backend-0.0.1-SNAPSHOT.jar /usr/local/lib/message-app-1.0.0.jar
CMD ["java", "-jar", "/usr/local/lib/message-app-1.0.0.jar"]
