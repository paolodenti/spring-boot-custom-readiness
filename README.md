# Spring Boot Custom Readiness Probe

[![Integration Tests](https://github.com/paolodenti/spring-boot-custom-readiness/actions/workflows/integration-tests.yaml/badge.svg)](https://github.com/paolodenti/spring-boot-custom-readiness/actions/workflows/integration-tests.yaml)
[![Build and Publish](https://github.com/paolodenti/spring-boot-custom-readiness/actions/workflows/build-publish.yaml/badge.svg)](https://github.com/paolodenti/spring-boot-custom-readiness/actions/workflows/build-publish.yaml)

This example shows how to implement a custom readiness probe for Spring Boot for k8s or Docker compose deployments.

If your service has a slow startup,
you might want to delay the readiness of the service until the startup is completed,
letting k8s to manage when to expose the API port (8080).

Also you might want to implement a graceful shutdown shutdown,
setting the readiness to down, when k8s sends a preStop signal.

## How it works

`CustomReadinessStateHealthIndicator` provide a custom readiness actuator probe,
asking every `CustomReadinessProvider` implementation if the service is ready.

The probe is registered at `/actuator/health/readiness` as an additional readiness component
with name `customReadinessState`.

`CustomReadinessStateController` implements an actuator endpoint to set the readiness state of the service,
invokable by the preStop lifecycle hook in k8s.

As an implementation example, `Slow1StartupSimulatorService` sets ready to `up` after 10 seconds and
`Slow2StartupSimulatorService` sets ready to `up` after 15 seconds.

Te readiness will go to `up` after 15 seconds, when both services are ready.

## How to run

* Start the app with `./mvnw spring-boot:run`
* run `curl http://localhost:9000/actuator/health/readiness` to see the current readiness state.
* after 15 seconds, the readiness state should change to `UP`.
* run `curl http://localhost:9000/actuator/notready` to signal the application (pod) to stop receiving traffic.

## Docker image

A docker image is available at `paolodenti/readiness`.

`docker run --rm -p 8080:8080 -p 9000:9000 paolodenti/readiness`

## Use in k8s

The contrived example below can be used to use the custom readiness probe in k8s.

The readiness probe is set to wait 20 seconds before it starts checking (`initialDelaySeconds`).

After the initial delay, the probe will check every 10 seconds (`periodSeconds`)
and it is allowed to fail 30 times in a row (`failureThreshold`)
before the pod is considered unhealthy.

Also, the `preStop` is set to `/actuator/notready`, to have k8s setting to `down` the readiness of the service.

```yaml
apiVersion: v1
kind: Service
metadata:
  name: readiness
spec:
  type: ClusterIP
  ports:
    - port: 8080
      targetPort: 8080
  selector:
    app: readiness
---
apiVersion: apps/v1
kind: Deployment
metadata:
  name: readiness
spec:
  selector:
    matchLabels:
      app: readiness
  template:
    metadata:
      labels:
        app: readiness
    spec:
      containers:
        - name: thepod
          image: "paolodenti/readiness:latest"
          ports:
            - name: http
              containerPort: 8080
              protocol: TCP
            - name: monitoring
              containerPort: 9000
              protocol: TCP
          livenessProbe:
            httpGet:
              path: /actuator/health/liveness
              port: monitoring
            initialDelaySeconds: 20
          readinessProbe:
            httpGet:
              path: /actuator/health/readiness
              port: monitoring
            initialDelaySeconds: 20
            failureThreshold: 30
            periodSeconds: 10
          lifecycle:
            preStop:
              httpGet:
                path: /actuator/notready
                port: monitoring
```
