
# Holiday Detail Service

A Spring Boot REST API to retrieve, compare, and analyze public holiday data for multiple countries. Supports endpoints for recent holidays, non-weekend holiday counts, and common holidays between countries.

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

## Schemas

- **Holiday**: `{ date: string, name: string }`
- **CountryHolidayCount**: `{ country: string, count: integer }`
- **CommonHoliday**: `{ date: string, localNameCountry1: string, localNameCountry2: string }`

## Getting Started

### Prerequisites

- Java 17+
- Maven

### Setup

1. Clone the repository:
   ```
   git clone https://gitlab.com/vinodbhatia/assignmen-project.git
   ```
2. Build the project:
   ```
   mvn clean install
   ```
3. Run the application:
   ```
   mvn spring-boot:run
   ```

The API will be available at `http://localhost:8888/`.

