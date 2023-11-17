# Spring Boot Custom Readiness custom probe

Custom readiness probe to set readiness to `up` when the service is ready.

Every 10 seconds the readiness state flips between `up` and `down`.

Start the app with `mvn spring-boot:run` and
then run `curl http://localhost:8080/actuator/health/readiness`
to see the current readiness state.