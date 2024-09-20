## Clinic Visit Accounting System

### Project Description

This is a Spring Boot application designed to manage patient visit records for a clinic. It allows for simplified tracking and management of visits using a connected MySQL database.

### Prerequisites

To run this application, you will need:

- **JDK 17** installed.
- **Maven** installed.
- A **MySQL** database with an existing blank schema (`clinic` by default); the application will create the necessary tables.

### Database Configuration

The application uses a MySQL database to store patient visit records. By default, the application connects to the database using pre-configured credentials (`root`/`root`), but they can be overriden these by passing the database URL, username, and password as command-line arguments.

**Default connection details**:
- URL: `jdbc:mysql://localhost:3306/clinic?useSSL=false&allowPublicKeyRetrieval=true`
- Username: `root`
- Password: `root`

You can override these with command-line arguments as follows:

```bash
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.datasource.url=jdbc:mysql://<your-db-url>:<port>/<your-database> --spring.datasource.username=<your-username> --spring.datasource.password=<your-password>"
```
### Implementation notes

- Swagger is not added/enabled by default as long as not mentioned in the requirements. 
- Domain object identification are defined as integers for simplicity; they might be as well UUIDs (probably, preferred way for a production application)
- Doctors and patients are not mixed up in terms that a doctor can be a patient as well at the same time with the same username and password.  
- No caching is implemented for simplicity (could make sense for a production application)
- **The last login date column is made optional** in contrast to the requirements because when users are created filling out this column does not make sense and is misleading
- It is supposed that there can be at most only one clinic in the system; by default it is created with some dummy data during application startup (if the clinic does not exist yet) 
- There is no validation for the logo file format when it is updated
- CORS and CSRF are disabled for simplicity
- Dictionaries like test types are defined as enums for simplicity; they might be stored in the database as well
- Validation added for appointment statuses flow: an appointment status can only transition like NEW -> IN_PROGRESS -> CLOSED
- The application is not secured with HTTPS for simplicity
