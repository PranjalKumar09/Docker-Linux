
#### **Objective**
Develop a Docker Compose application with the following components:
1. **Thymeleaf Web Application**: Using Spring Boot.
2. **Database**: MySQL container with persistent data storage.

---

### **Setup Outline**
1. **Base Image**: Use `apache/maven` as the base image for building the Spring Boot application.
2. **Key Files**:
   - `pom.xml`: Defines project dependencies for Spring Boot and MySQL.
   - `application.properties`: Configures the environment variables.
   - `Expenses-Tracker-WebApp`: The main web folder of the project containing source code and templates.

---

### **Environment Variables Transformation**
- Update `application.properties` to use uppercase variable names and replace dots (`.`) with underscores (`_`).
- Example transformation:
  ```properties
  spring.datasource.url=jdbc:mysql://mysql:3306/expenses_tracker?useSSL=false&serverTimezone=UTC
  spring.datasource.username=root
  spring.datasource.password=Test@123
  spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
  ```
  **Becomes:**
  ```yaml
  SPRING_DATASOURCE_URL: "jdbc:mysql://mysql:3306/expenses_tracker?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC"
  SPRING_DATASOURCE_USERNAME: "root"
  SPRING_DATASOURCE_PASSWORD: "Test@123"
  ```
  > **Note**: The addition of `?allowPublicKeyRetrieval=true` in the connection string ensures proper key retrieval when connecting to the MySQL server.

---

### **Docker Compose Configuration**

#### **Java Application Service** (`java_app`)
- **Build Context**: Specifies the location of the project code.
- **Environment Variables**: Maps key Spring Boot configurations to environment variables.
- **Health Check**: Ensures the service is up and running.
- **Dependencies**: Relies on the MySQL database container (`mysql_db`).

```yaml
services:
  java_app:
    build:
      context: .
    container_name: "expensesapp"
    networks:
      - expenses-app-nw
    environment:
      SPRING_DATASOURCE_URL: "jdbc:mysql://mysql:3306/expenses_tracker?allowPublicKeyRetrieval=true&useSSL=false"
      SPRING_DATASOURCE_USERNAME: "root"
      SPRING_DATASOURCE_PASSWORD: "Test@123"
    depends_on:
      - mysql_db
    ports:
      - "8080:8080"
    restart: always
    healthcheck:
      test: ["CMD-SHELL", "curl -f http://localhost:8080 || exit 1"]
      interval: 10s
      timeout: 5s
      retries: 5
      start_period: 60s
```

---

#### **MySQL Database Service** (`mysql_db`)
- **Image**: Official MySQL image.
- **Environment Variables**: Configures root credentials and database name.
- **Persistent Storage**: Maps a Docker volume to `/var/lib/mysql` for data persistence.
- **Health Check**: Ensures MySQL server is ready before allowing other services to depend on it.

```yaml
  mysql_db:
    image: mysql
    container_name: "mysql"
    networks:
      - expenses-app-nw
    environment:
      MYSQL_ROOT_PASSWORD: "Test@123"
      MYSQL_DATABASE: "expenses_tracker"
      MYSQL_SSL: "0"  # Disable SSL
    restart: always
    ports:
      - "3306:3306"
    volumes:
      - mysql_data:/var/lib/mysql  # Persistent data volume
    healthcheck:
      test: ["CMD", "mysqladmin", "ping", "-h", "localhost", "-uroot", "-pTest@123"]
      interval: 10s
      timeout: 5s
      retries: 5
      start_period: 60s
```

---

#### **Networks and Volumes**
- **Network**: Creates an isolated network for the application components.
- **Volume**: Declares a persistent volume for the MySQL database.

```yaml
networks:
  expenses-app-nw:

volumes:
  mysql_data:
```

---

### **Debugging and Common Issues**
1. **Volume-Related Errors**:
   - Temporarily remove the volume to test configuration (`mysql_db` in `mysql_db`).
   - Verify volume permissions to ensure Docker can write to the directory.
   
2. **MySQL Connection String**:
   - Ensure `?allowPublicKeyRetrieval=true` is added if you face connection issues.
   - Use the correct timezone parameter (`serverTimezone=UTC`).

3. **Container Health**:
   - Use `docker logs <container_name>` to debug container startup issues.
   - Adjust health check timings if the service takes longer to initialize.

4. **Environment Variable Debugging**:
   - Use `docker exec -it <container_name> env` to inspect environment variables.

---
