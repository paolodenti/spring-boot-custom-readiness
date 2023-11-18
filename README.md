# Spring Boot Custom Readiness Probe

This example shows how to implement a custom readiness probe for Spring Boot,

If your service has a slow startup,
you might want to delay the readiness of the service until the startup is completed,
letting k8s to manage when to expose the API port.

The readiness will go `up` 15 seconds after the application has fully started (simulating a slow startup).

Also, to manage the shutdown of the service, the app exposes a `/actuator/notready` endpoint.

The `notready` endpoint can be invoked by the preStop hook in k8s to signal the pod to stop receiving traffic.

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

```yaml
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
