# Load balancer application

### Prerequisites

- Java 11
- Maven (optional since the maven wrapper is included)

### Notes

- It is stated that there is no need for a runnable application and there should be no framework within the codebase. Therefore the application
  currently does not contain a main method and also no framework was used (e.g. Spring Boot).
- In step 2 it was unclear if it is necessary to be able to provide the full list in one call or one-by-one to the load balancer. Currently both
  options are possible.
- The load balancer maintains a registry of the providers.
- At the moment the constants mentioned in the assignment (Y, 2 times heartbeat check, maximum number of providers accepted) are hardcoded. In a
  real-life application the values would be configurable (for example, in the application.properties in a Spring application).
- It is stated that the assignment should require around 2 hours to complete, thus the main focus was on the design and implementation instead of the
  test coverage.

### Tests

* The tests can be run from the command line:
  `./mvnw clean verify`

### Technologies

* Lombok
* JUnit 5
* AssertJ
* Maven
* Git

### Built With

* [Maven 3.6.3](https://maven.apache.org) - Build & Dependency Management
* [Java 11](https://docs.aws.amazon.com/corretto/latest/corretto-11-ug/downloads-list.html) - Amazon Corretto - jdk11.0.9_11