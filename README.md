## Holiday Detail Service

It has endpoints to retrieve, compare, and analyze public holiday data for multiple countries, including recent holidays, non-weekend counts, and common holidays.

## Features

- Get last 3 celebrated holidays for a country
- Get non-weekend holiday counts for countries in a year
- Get deduplicated common holidays between two countries in a year

## API Endpoints

### 1. Recent Holidays

**GET** `/api/v1/holidays/recent?country=US`

Returns the last 3 holidays for the specified country.

### 2. Non-Weekend Holiday Counts

**GET** `/api/v1/holidays/non-weekend-count?year=2025&countries=US,CA,GB`

Returns non-weekend holiday counts for the given countries in a year.

### 3. Common Holidays

**GET** `/api/v1/holidays/common?year=2025&country1=US&country2=CA`

Returns deduplicated common holidays between two countries in a year.



# Technologies Used
   - Spring Boot 3.5.6
   - Java 17
   - Maven 3.9
   - OpenAPI 3 (Swagger)
   - RESTFull endpoints
   - Docker

## Project Structure
      src — Source code of the holiday-detail-service
      postman — Postman collection JSON file to test endpoints

## Swagger Documentation
      Swagger UI: http://localhost:8080/swagger-ui/index.html
      OpenAPI JSON: http://localhost:8080/v3/api-docs
      OpenAPI YAML: http://localhost:8080/v3/api-docs.yaml

## How to Run Locally
     1. Clone the repository:
            git clone https://github.com/vinodbhatia83/holiday-detail-service.git
      
     2. Navigate to the project directory and start Docker:
            cd holiday-detail-service
            docker compose up
     3. BUILD AND RUN THE APPLICATION:
            mvn clean install
            mvn spring-boot:run

    Application will be running at http://localhost:8080

   
   ## Validation
      All incoming requests are validated.
      Invalid requests return meaningful error messages.


##  Code Coverage
    JaCoCo is integrated for code coverage analysis.

## To generate a coverage report, run:
      mvn clean test

##  The coverage report will be available at:
      /target/site/jacoco/index.html
      Coverage is 95% as of now

