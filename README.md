# Spring Boot Custom Readiness Probe

Custom readiness probe to set readiness to `up` when the service is ready.

Every 10 seconds the readiness state flips between `up` and `down`.

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
