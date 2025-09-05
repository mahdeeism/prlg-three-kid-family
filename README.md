# prlg-three-kid-family
A Spring Boot REST API that provides a solution to the PRLG Three Kid Family Challenge

### Time breakdown

In total I spent between 4-5 hours on this project, roughly split as follows:
1. Project setup: 30 minutes
2. Database repository and entity: 20 minutes
3. Business logic: 2 hours (mostly trying to achieve bidirectional integrity)
4. Unit tests: 1 hour - 1.5 hours
5. Documentation: 30 minutes

### Assumptions and design decisions

1. I manually created the DTO with getters and setters as well as the PersonMapper class. Here I could have used lombok and mapstruct but felt that the development time saved didn't justify the cost of adding 2 additional dependencies.
2. ApiController is the interface, PersonControllerV1 is the implementation. This allows for easily handling API versioning by adding a PersonControllerV2 that also implements the ApiController as an example.
3. For bidirectional integrity, if A says B is a child, but B already has parents C and D, B is not updated. This is an assumption I made, IRL this would be a question for the PO/BA.
4. For unit testing I followed the testing pyramid. This means that there are a lot of smaller unit (fast) tests covering each unit of work and less integration (slow) tests.
5. I allowed Hibernate to create the database table, IRL I would use either liquibase or flyway for this.
6. I've listed the endpoints below, for applications that have multiple endpoints and DTOs I would instead make use of a OpenAPI.

PS: There is an issue with the bidirectional integrity. If A says B is a child, B is updated with A as a parent. If in a following request A says B is no longer a child, B is not updated. 
This can be fixed by checking if there are any records that reference A as a parent, and if so does A still reference them as a child. If not then remove A as a parent on that child.

PPS: For the optional part of this assignment, some psuedocode:

```
    1. Add a DELETE mapping to the ApiController and its implementations
    2. When a DELETE request comes in delete any records in the database containing those IDs
    3. Enforce bidirectional integrity, update any records where that ID may appear as a parent or child
    4. Store those IDs in a IgnorePerson table
    5. Update the savePerson functionality to first check if any IDs in the request should be ignored
        5.1 To prevent an additional call to the database on every savePerson request, the ignored IDs could also be stored in a cache on the service class that gets updated each time a new DELETE request arrives
```

## How to build and test the application

`mvn clean install`

A Jacoco test coverage report can be found in the `target/site/jacoco` directory after building and testing the application.

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
2. `200 -> OK` if one or more suitable matches are found. The matches are returned as JSON in the response body as follows:

```
[
  {
    "id": 1,
    "name": "Sponge Bob parent 1",
    "birthDate": "1986-12-10",
    "parent1": {
      "id": 32
    },
    "parent2": {
      "id": 33
    },
    "partner": {
      "id": 10
    },
    "children": [
      {
        "id": 5
      },
      {
        "id": 6
      },
      {
        "id": 7
      }
    ]
  }
]
```
