# LostAndFoundService

LostAndFoundService is an API that reads lost items from a file (e.g. PDF) provided by Admin and stores the extracted information. Users of this application can read the stored lost items and claim it.

## Depends On :
This service depends on UserService (https://github.com/hitendra1908/user-service.git) which runs on localhost:/8090, first run LostAndFound service and then UserService. Both these service shares same database, this service is just to fetch name of the user based on the id.

## Technologies Used
This project utilizes the following technologies:
* Spring Boot 3.3.2
* Spring Data JPA
* mysql:8.0
* Maven 3.3.2
* Java 21
* JUnit 5
* Docker
* Spring-security with basic authentication
* RestClient to connect with UserService
* OpenAPI for swagger documentation
* Kafka ui
* [Testcontainers](https://testcontainers.com/) (for Spring integration tests using a container)


## How to Run the Project

1. Clone the repository:
   ```sh
   https://github.com/hitendra1908/lostandfound-service.git

2. Navigate to the root directory and start Docker:
   ```sh
   docker compose up

3. Build the project:
   ```sh
   mvn clean install

4. Run the Spring Boot application:
   ```sh
   mvn spring-boot:run

Application runs on localhost:8080

## Note:
After this you need to start/run UserService (https://github.com/hitendra1908/user-service.git)

## Projects Endpoints:
There are 2 different types of endpoints one for Admin and one for regular User.
There are 2 default users created at the start of the application:
1. Admin User: username: admin & password: admin123
2. User User: username: user123 & password: user123
There is also an endpoint for Admin, where admin can create more user with diff Roles

## Viewing Swagger Documentation
Swagger docs will be available at : http://localhost:8080/swagger-ui/index.html
You can also download Json & Yaml file from http://localhost:8080/v3/api-docs and http://localhost:8080/v3/api-docs.yaml respectively.
