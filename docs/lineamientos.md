# Project Context

This repository is used for an AI-Assisted Software Development with Java training course.

## Communication Language

- Always respond to the user in Spanish.
- Write all source code in English: classes, methods, variables, packages, comments and log messages.

## Technology Stack

- Java 25 (LTS)
- Spring Boot 4.1.1 (Spring Framework 7, Jakarta EE)
- Maven 3.9+
- Spring Data JPA for persistence
- MySQL as the database
- REST APIs
- JUnit 5 for unit tests
- Bean Validation
- Swagger / OpenAPI (springdoc-openapi) for API documentation
- Postman for endpoint testing

## Spring Boot 4 Rules

- Target Spring Boot 4.1.1 only.
- Do not use APIs, starters, properties or configuration styles deprecated or removed since Spring Boot 3.x.
- Do not generate code written for Spring Boot 2.x or 3.x when a Spring Boot 4.x equivalent exists.
- Use jakarta.* APIs instead of legacy javax.* APIs.

## Project Structure

- Use the three Maven modules defined by the course model: `domain`, `application`, and `infrastructure`.
- Keep Maven's standard source layout inside each module: `src/main/java`, `src/main/resources`, `src/test/java`, and `src/test/resources`.
- Keep the `com.solgases` namespace and use the layer-root packages shown below; feature names belong in class names and DTO names.
- Place pure business models and rules in `domain`; use cases, ports, application DTOs, and application exceptions in `application`; and REST, persistence, framework configuration, and the Spring Boot entry point in `infrastructure`.
- Do not create global technical packages that mix unrelated feature code. Cross-feature application exceptions belong under `com.solgases.application.exception`; shared Spring/API infrastructure belongs under `com.solgases.infrastructure`.

### Architecture boundaries

Organize the project as a Maven reactor with three modules, following the Clean Architecture model used in class:

```text
solgases/
├── domain/
│   └── src/main/java/com/solgases/domain/{model,exception,event}
├── application/
│   └── src/main/java/com/solgases/application/{port,usecase,dto,exception}
└── infrastructure/
    └── src/main/java/com/solgases/infrastructure
        ├── api/dto           # HTTP request/response DTOs
        ├── api/rest          # Controllers and API mappers
        ├── api/rest/error    # HTTP exception translation
        ├── config            # Use-case beans and framework configuration
        └── persistence       # Adapters, entities, mappers and repositories
```

Dependency direction:

```text
infrastructure → application → domain
infrastructure/persistence → application output ports
infrastructure/api/rest → application input ports
```

- The domain must not import Spring, Jakarta Persistence, Jackson or Lombok.
- Application defines input and output ports. Persistence adapters implement output ports; REST controllers call input ports.
- JPA entities are persistence models and must remain distinct from domain models. Map between them inside infrastructure.
- Keep API request/response mapping in API mappers and domain/JPA mapping in persistence mappers.
- Application use cases do not use `@Service`; register them as beans in `infrastructure/config`.
- Application may use Spring's `@Transactional` only to establish a use-case transaction boundary. Do not spread transaction annotations into the domain.
- Keep the existing feature boundaries across the three Maven modules. `infrastructure` depends on `application`; `application` depends on `domain`; `domain` has no runtime framework dependencies.
- Preserve existing REST contracts and approved business behavior during architectural refactoring. A refactor does not approve or resolve business rules marked pending elsewhere.
- Apply these boundaries incrementally to existing code. Do not migrate unrelated features as part of a focused change.

## Design Principles

- Apply SOLID principles:
  - Single Responsibility: each class has one reason to change.
  - Open/Closed: extend behavior without modifying stable code.
  - Liskov Substitution: subtypes must be usable wherever their base type is expected.
  - Interface Segregation: prefer small, focused interfaces.
  - Dependency Inversion: depend on abstractions, and inject dependencies.
- Keep classes focused and cohesive.
- Avoid unnecessary abstractions.
- Avoid premature design patterns.

## Java Development Rules

- Use constructor injection.
- Do not use field injection.
- Prefer Java records for immutable DTOs when appropriate.
- Do not expose persistence entities directly through REST APIs.
- Do not introduce additional frameworks unless explicitly requested.
- Use clear English names for classes, methods, variables and packages.
- Use a logging framework (SLF4J) instead of System.out or System.err.

## Spring Boot Rules

- Follow standard Spring Boot conventions.
- Use @RestController for REST endpoints.
- Use Bean Validation for request validation.
- Keep controllers focused on HTTP concerns.
- Keep business logic outside controllers.
- Use constructor injection for Spring components.

## Persistence Rules

- Use Spring Data JPA for data access.
- The database is MySQL.
- Access the database only through Spring Data repositories used by infrastructure persistence adapters.
- Map between JPA entities and domain models inside persistence mappers; map between application results and HTTP DTOs inside API mappers. Return application DTOs through input ports, never JPA entities through REST controllers.

## Configuration and Secrets

- Never include secrets in source code or in committed files: passwords, tokens, API keys or credentials.
- Read sensitive values from environment variables or an external secret source.
- Use application.yml as the base configuration file. application.properties may be used where needed.
- Use Spring profiles with one configuration file per profile:
  - `local`
  - `dev`
  - `qa`
  - `prd`
- Profile files follow the naming `application-{profile}.yml`.

## Testing Rules

- Write unit tests with JUnit 5.
- Keep unit tests fast and isolated from the database and the network.
- Use @SpringBootTest only when an integration test is really needed.

## API Documentation (Swagger)

- Document the REST API with Swagger / OpenAPI using springdoc-openapi.
- Use a springdoc-openapi version that is compatible with Spring Boot 4.1.1. Verify the version before adding the dependency.
- This is the only additional documentation library explicitly approved, as an exception to the rule about additional frameworks.
- Annotate controllers and DTOs so every endpoint documents its summary, parameters, request body, responses and error codes.
- Keep the Swagger UI and the OpenAPI JSON available in the `local`, `dev` and `qa` profiles. Disable them in `prd` unless explicitly requested.

## Postman Collection

- When the application is finished, generate a Postman collection ready to import and test every endpoint.
- Generate the following files in `docs/postman/`:
  - `<project-name>.postman_collection.json`, using the Postman Collection v2.1 format
  - `<project-name>.postman_environment.json`, with a variable for each profile: `local`, `dev`, `qa` and `prd`
- Base the collection on the OpenAPI specification of the application, so it matches the real endpoints.
- Organize the collection in folders by feature, with one request per endpoint.
- Include example request bodies, headers and the base URL as a variable (for example `{{baseUrl}}`).
- Never include secrets or real credentials in the collection or the environment files. Use empty or placeholder values.
- Regenerate the collection whenever endpoints change.

## AI-Assisted Development Rules

Before making significant changes:

1. Inspect the existing codebase.
2. Explain the proposed changes.
3. Identify assumptions.
4. Do not invent requirements.
5. Implement only the requested scope.

When the developer explicitly delegates implementation of an increment whose scope and decisions are approved in `docs/mvp1.md`, the approved documentation and the request to continue are sufficient authorization. Do not pause to request approval of the same decisions again. Record unresolved decisions, implement the remaining approved scope when possible, and ask only when an unresolved decision blocks the core implementation or could lead to an irreversible or consequential choice. The developer may use ChatGPT for decision-making and documentation while Claude Code implements the documented scope.

After making changes:

1. Compile the project.
2. Run the relevant tests.
3. Report the files created or modified.
4. Report any unresolved issues.

Never claim that code works unless it has been compiled or tested.
