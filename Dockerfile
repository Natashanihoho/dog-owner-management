FROM openjdk:17-jdk-alpine3.14
RUN mkdir /app
WORKDIR /app
COPY --from=build /gradle/build/libs/*.jar /app/
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "dog-owner-management-0.0.1-SNAPSHOT.jar"]