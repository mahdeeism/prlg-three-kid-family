# prlg-three-kid-family
A Spring Boot REST API that provides a solution to the PRLG Three Kid Family Challenge

## How to build and test the application

`mvn clean install`

## How to run the application

`mvn spring-boot:run`

## Accessing the database

The application makes use of an in-memory H2 database.
To access a GUI for this database navigate to `localhost:8080/h2-console` once the application is running.

## Endpoints

The application exposes a single endpoint as follows:

`POST -> /api/v1/people`

The endpoint only accepts requests with a JSON body abiding by the following format:

```
{
  "id": 11,
  "name": "Sponge Bob",
  "birthDate": "1986-12-10",
  "parent1": { "id": 32 },
  "parent2": { "id": 33 },
  "partner": { "id": 10 },
  "children": [{ "id": 1 }, { "id": 2 }, { "id": 3 }]
}
```

and will respond in one of 2 ways:

1. `444 -> No Response` if no suitable matches are found
2. `200 -> OK` if one or more suitable matches are found. The matches are returned as JSON in the response body.
