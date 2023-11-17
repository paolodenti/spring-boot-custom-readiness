# Spring Boot Custom Readiness Probe

If your service has a slow startup,
you might want to delay the readiness of the service until the startup is completed,
letting k8s to manage when to expose the API port.

This example shows how to implement a custom readiness probe for Spring Boot,

The readiness will go `up` 15 seconds after the application has fully started (simulating a slow startup).

## How to run

Start the app with `./mvnw spring-boot:run` and
then run `curl http://localhost:9000/actuator/health/readiness`
to see the current readiness state.

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
  name: thepod
spec:
  selector:
    matchLabels:
      app: thepod
  template:
    metadata:
      labels:
        app: thepod
    spec:
      containers:
        - name: thepod
          image: "theimage:thetag"
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
```
