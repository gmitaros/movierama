# Accessing the Application

The production version of the application is accessible at:

[https://movierama.aboveaverage.dev/](https://movierama.aboveaverage.dev/)

## Movierama Frontend

For frontend-specific documentation and setup instructions, please refer
to [Movierama Frontend README](./movierama-frontend/README.md).

## Run Instructions

To run the Spring Boot application, follow these steps:

### Prerequisites

- Java JDK 11 or higher installed
- Maven installed

### Steps

1. **Clone the Repository**:

    ```bash
    git clone https://github.com/gmitaros/movierama.git
    ```

2. **Navigate to the Project Directory**:

    ```bash
    cd movierama
    ```

3. **Build the Project**:

    ```bash
    mvn clean install
    ```

4. **Run the Application**:

    ```bash
    mvn spring-boot:run -Dspring.profiles.active=local
    ```

Alternatively, you can run the generated JAR file:

1. **Build the Project**:

    ```bash
    mvn clean install
    ```

2. **Navigate to the Target Directory**:

    ```bash
    cd target
    ```

3. **Run the JAR file**:

    ```bash
    java -jar movierama-backend-0.0.1-SNAPSHOT.jar
    ```

The application will start, and you should see output in the console indicating that the server is running. By default,
the application will run on `http://localhost:8088`.

### Database

The application uses JPA with Hibernate and is designed to be database-agnostic, making it easy to switch between
different database systems.

#### Development and Production Deployment

For development and production deployment, the application uses MariaDB. Ensure you have MariaDB running and the
appropriate database configurations set in `application-loacl.properties`.

#### Testing

Tests are executed using an H2 in-memory database to ensure isolation and speed. The H2 database configurations for
tests are set in `application-test.properties`.

**Note**: Before accessing the H2 console, make sure the application is running.

## Technologies Used

### Backend

- **Spring Boot**: Framework for building robust Java applications.
- **Spring Data JPA**: Simplifies data access for relational databases.
- **Spring Security**: Provides authentication and authorization functionalities.
- **JWT (JSON Web Tokens)**: For secure token-based authentication.
- **H2 Database**: In-memory database used for development and testing.
- **Maven**: Dependency management and build tool.
- **JUnit & Mockito**: For unit testing and mocking.

### Database

- **MariaDB**: Used for development and production deployments.
- **H2 Database**: Used for testing purposes with Spring Boot.

### Testing

- **JUnit**: Framework for writing and running tests.
- **Mockito**: Mocking framework for unit tests.

### Other Tools and Libraries

- **Flyway**: Database migration tool for managing changes to the database schema.
- **JaCoCo**: Java Code Coverage tool for generating test coverage reports.
- **Lombok**: For reducing boilerplate code and generating getters, setters, etc.
- **Swagger**: API documentation and testing tool.
- **Logback**: Logging framework for logging and monitoring application events.
- **Jackson**: JSON processing library for converting Java objects to JSON and vice versa.
- **Postman Collections**: Included Postman collections for API testing and documentation.

## Spring Boot Endpoints and Features

### API Documentation

- **Swagger UI**: [Swagger API Documentation](https://movierama.aboveaverage.dev/api/v1/swagger-ui/index.html) -
  Documentation and testing of API endpoints.

### MovieController

The `MovieController` manages movie-related HTTP requests, providing a variety of API endpoints to interact with movies,
including saving, fetching, and voting.

#### Endpoints:

- **GET /public/movies**
    - Fetches a paginated list of movies.
    - Optional query parameters for pagination, sorting, and filtering by user.
- **POST /movies**
    - Saves a new movie.
    - Requires a MovieRequest object in the request body.
    - Returns the saved MovieResponse object.
- **GET /movies/{movieId}**
    - Fetches a movie by its ID.
    - Path variable movieId specifies the movie ID.
    - Returns the MovieResponse object for the specified movie ID.
- **PUT /movies/{movieId}**
  - Updates a movie by its ID.
  - Path variable movieId specifies the movie ID.
  - Returns the MovieResponse object for the updated movie ID.
- **GET /public/movies**
    - Fetches a paginated list of movies.
    - Optional query parameters for pagination, sorting, and filtering.
    - Returns a Page<MovieResponse> containing the list of movies.
- **GET /public/movies/owner/{ownerId}**
    - Fetches a paginated list of movies by owner.
    - Path variable ownerId specifies the owner ID.
    - Optional query parameters for pagination and sorting.
    - Returns a Page<MovieResponse> containing the list of movies.
- **PUT /movies/{movieId}/vote**
    - Votes (create or update vote) for a movie by its ID.
    - Path variable movieId specifies the movie ID.
    - Requires a VoteRequest object in the request body.
    - Returns the updated MovieResponse object with the new vote.

#### Query Parameters:

- **page**: Specifies the page number for pagination.
- **size**: Specifies the number of items per page.
- **user**: Filters movies by user ID.
- **sortField**: Specifies the field to sort movies by (e.g., CREATED, LIKES).
- **sortType**: Specifies the sort direction (ASC or DESC).
- **title**: Filters movies by matching movies title with provided text.

### AuthenticationController

The `AuthenticationController` handles user authentication and registration.

#### Endpoints:

- **POST /auth/register**
    - Description: Registers a new user.
    - Request Body: RegistrationRequest with user details.
    - Response: RegistrationResponse indicating registration status.

- **POST /auth/authenticate**
    - Description: Authenticates a user and returns a JWT token.
    - Request Body: LoginRequest with email and password.
    - Response: LoginResponse with authentication token or error message.

- **GET /auth/activate-account**
    - Description: Activates a user account using an activation code.
    - Query Parameter: activation-code - Activation code sent to the user.
    - Response: HTTP 200 OK upon successful activation.

### UserController

The `UserController` manages user-related HTTP requests.

#### Endpoints:

- **GET /user/votes**
    - Description: Fetches votes by the authenticated user.
    - Response: Array of VoteResponse representing the user's votes.
- **GET /user/info**
    - Description: Fetches authenticated user information.
    - Response: UserResponse containing user details.

### VoteController

The `VoteController` manages vote-related HTTP requests.

#### Endpoints:

- **POST /votes/movie/{movieId}**
    - Description: Votes for a movie.
    - Path Variable: movieId - ID of the movie to vote for.
    - Request Body: VoteRequest with the vote type (LIKE or HATE).
    - Response: VoteResponse with the vote details.

#### Specifications

The `MovieSpecification` class provides specifications for filtering movies based on different criteria.

#### Features:

- **withOwnerId(Long ownerId)**
    - Filters movies by owner ID when provided.
    - If ownerId is null, the specification is ignored.
- **withTitle(String title)**
    - Filters movies by matching movies title with provided text.
    - If title is null, the specification is ignored.

### GlobalExceptionHandler

The `GlobalExceptionHandler` class handles global exceptions and maps them to appropriate HTTP responses.

#### Features:

- **Exception Handling**
    - Handles various exceptions like InvalidTokenException, MovieramaGenericException, TokenExpiredException, etc.
    - Returns custom error responses with error codes and descriptions.
