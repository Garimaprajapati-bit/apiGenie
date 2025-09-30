apiVersion: apps/v1
kind: Deployment
metadata:
  name: mock-api-backend
spec:
  replicas: 2
  selector:
    matchLabels:
      app: mock-api-backend
  template:
    metadata:
      labels:
        app: mock-api-backend
    spec:
      containers:
      - name: backend
        image: mock-api-backend:latest
        ports:
        - containerPort: 8080
        env:
        - name: SPRING_PROFILES_ACTIVE
          value: "prod"
---
apiVersion: v1
kind: Service
metadata:
  name: mock-api-backend-service
spec:
  selector:
    app: mock-api-backend
  ports:
  - protocol: TCP
    port: 8080
    targetPort: 8080
  type: LoadBalancer
---
apiVersion: apps/v1
kind: Deployment
metadata:
  name: mock-api-frontend
spec:
  replicas: 2
  selector:
    matchLabels:
      app: mock-api-frontend
  template:
    metadata:
      labels:
        app: mock-api-frontend
    spec:
      containers:
      - name: frontend
        image: mock-api-frontend:latest
        ports:
        - containerPort: 80
---
apiVersion: v1
kind: Service
metadata:
  name: mock-api-frontend-service
spec:
  selector:
    app: mock-api-frontend
  ports:
  - protocol: TCP
    port: 80
    targetPort: 80
  type: LoadBalancer
