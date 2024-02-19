
FROM openjdk:11

COPY . /app

WORKDIR /app

RUN ./mvnw clean package

CMD ["java", "-jar", "target/backend-0.0.1-SNAPSHOT.jar"]