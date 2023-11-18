FROM amazoncorretto:17.0.9 as build

WORKDIR /app

COPY . .

RUN ./mvnw --no-transfer-progress clean package -DskipTests=true

FROM amazoncorretto:17.0.9

COPY --from=build /app/target/*.jar /app.jar

ENTRYPOINT ["java", "-jar", "/app.jar"]
