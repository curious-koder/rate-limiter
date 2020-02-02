# Spring Boot Rate Limiter

- Rate limiter can be effectively implemented in distributed systems using API gateway like Kong or Istio in Kubernetes.
- For the coding challenge the below assumptions are made
    - It is implemented using spring boot filter      
    - Local In-Memory based Sliding window log algorithm used
    - It uses more memory than other techniques like Fixed Window or Sliding Widow counter algo, but it works perfectly in boundary conditions
    - Clustering not considered. Can implement clustering using distributed cache like Hazelcast or Redis
    - 'client_id' header is used to identify the requester. In real world, this can be obtained from JWT token
    - If 'client_id' header is not passed, 'public_user' is used as default

### Prerequisites

Java 11 and Maven 3.5

### Running the unit tests
To run all the unit tests
```
mvn clean test
```
Build will fail if the code coverage goes below 95%  
Code coverage generated at the below location
```
open target/site/jacoco/index.html
```

### Running the spring boot application
Use the below command to run the spring boot application
```
mvn spring-boot:run
```

### Load testing

To test the 100 request per hour limit. 
First run the spring boot application using the above command and in a new terminal run the below scritp
```
./test.sh 110
```

Where the argument in the number of request to make. After 100 request, it will respond with 429 
