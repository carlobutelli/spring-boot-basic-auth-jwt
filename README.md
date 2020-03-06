Setup
-----

### Running the application

A basic spring boot application with basic authentication and jwt (mainclass: `com.tech.travel.Application`).

To run the application execute `./gradlew bootRun`

### Embedded H2 database

During application startup an embedded h2 database is started and loaded  

Url: [http://localhost:8080/h2-console]   
Database url: `jdbc:h2:mem:travel-api`   
Username: `sa` (no password required)   

---

Security
--------

### Basic authentication

Api is secured with basic authentication, so it first requires a user to login:
- username: someuser
- password: psw

### JWT

The login generates a jwt that can be used to access the protected routes

#### Diagram
[[charts/basic_auth_jwt.png | width=100px | height=200px]]

---

Documentation
-------------



Statistics
----------

Required `OPS` role to be accessed.

Information are exposed in an `/actuator/metrics/**` API endpoint as JSON.

---

API data
--------

### Database schema

The data exposed by the API is stored in an embedded H2 database.   
Us JPA mappings to map this data to a domain model.   

The structure of the database schema is shown below:

      =================================
      | Location                      |
      |===============================|
      | id        integer (generated) |
      | code      varchar             |
      | type      varchar             |<-------|
      | longitude double              |        |
      | latitude  double              |        | parent:                           
      | parent    integer             |        | relation between locations 
      =================================        | airport -> city, city -> country   
                  |   |                        |
                  |   |                        |
                  |   --------------------------
                  |
                  |
      ==================================
      | Translation                    |
      |================================|
      | id          integer (generated)|
      | location    integer            |
      | language    varchar            |
      | name        varchar            |
      | description varchar            |
      ==================================
      
### Example data

Locations sample data:

| id  | code | type    | longitude | latitude | parent |
|-----|------|---------|-----------|----------|--------|
| 4614| NL   | COUNTRY | 5.45      | 52.3     | null   |
| 5366| AMS  | CITY    | 4.78417   | 52.31667 | 4614   |
| 6412| AMS  | AIRPORT | 4.76806   | 52.30833 | 5366   |

Translations sample data:

| id   | location | language | name        | description                           |
|------|----------|----------|-------------|---------------------------------------|
| 7131 | 4614     | EN       | Netherlands | Netherlands (NL)                      |
| 7132 | 4614     | NL       | Nederland   | Nederland (NL)                        |
| 8635 | 5366     | EN       | Amsterdam   | Amsterdam (AMS)                       |
| 8636 | 5366     | NL       | Amsterdam   | Amsterdam (AMS)                       |
| 9970 | 6412     | NL       | Schiphol    | Amsterdam - Schiphol (AMS), Nederland |

---

Exposed routes and Roles
-----

| routes                     | role/s           | description                      |
|----------------------------|------------------|----------------------------------|
| ```/h2-console```          | All              | Database                         |
| ```/actuator/metrics/**``` | ADMIN, OPS       | Metrics                          |
| ```/api/auth/sign-up```    | All              | Register user when unknown       |
| ```/api/auth/login```      | All              | Login with basic authentication  |
| ```/travel/**```           | ADMIN, OPS, USER | Protected endpoints              |
| ```/users/all```           | All              | Fetch all the users              |
| ```/users/{username}```    | ADMIN, OPS, USER | Fetch a single user              |
| ```/users/test/ops```      | OPS              | Test to access ops board         |
| ```/users/test/admin```    | ADMIN            | Test to access admin board       |

---

Run API with Docker
---------------
```bash
docker build -t travel-api:latest .

docker run -p 8080:8080 travel-api
```

