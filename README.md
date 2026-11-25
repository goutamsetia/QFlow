QFlow – Smart Queue Management System
QFlow is a Java-based, MySQL-powered Smart Queue Management System that helps an organization manage customers, services, service counters, tokens, and waiting queues through a menu-driven console application.

The application provides separate modules for customer management, service management, token management, queue operations, administration, and reports.
Features
1. Admin access
- Register an administrator when no admin account exists.
- Log in before accessing the main application.
- Admin records include a username, password hash, full name, and role.
2. Customer management
- Add and view customer records.
- Search for a customer by ID.
- Update customer details.
- Delete customer records when database relationships allow it.
- Import customer records from a CSV file.
3. Service management
- Add, view, search, update, and delete services.
- Store a service description and average service time.
- Enable or disable services.
4. Token management
- Generate a token for a customer and selected service.
- Assign a priority: NORMAL, SENIOR, or EMERGENCY.
- Calculate an estimated waiting time using the service's average time and the number of waiting tokens.
- View all tokens, search by token ID, and view tokens belonging to a customer.
- Update token status and cancel tokens.
- Export token records to CSV.
Token numbers are generated using the next available number for the issue date. The database enforces uniqueness for the combination of token_number and issue_date.
5. Queue management
- View the current queue.
- Call the next eligible token for a counter.
- View tokens currently being served.
- Mark a service as completed.
6. Reports and analytics
The reports module includes:
- Overall token summary.
- Service-wise report.
- Daily token report.
- Average service time.
- Priority-wise report.
- Counter performance report.
- Option to generate reports in parallel.
Technology stack
Technology	Usage
Java 17	Application logic
MySQL	Persistent data storage
JDBC	Java-to-MySQL connectivity
Maven	Dependency management and build
MySQL Connector/J 9.5.0	MySQL JDBC driver
Java Enums	Priority levels, token statuses, service categories, and user roles
Java Streams	Stream demonstration

```text
QFlow/
├── pom.xml
├── sql/
│   ├── schema.sql
│   ├── sample_data.sql
│   └── queries.sql
├── exports/
│   └── .gitkeep
└── src/
    └── main/
        └── java/
            └── com/qflow/
                ├── admin/
                │   ├── AdminDAO.java
                │   └── AdminMenu.java
                ├── app/
                │   ├── Main.java
                │   └── CustomerTest.java
                ├── customer/
                │   ├── CustomerDAO.java
                │   └── CustomerMenu.java
                ├── database/
                │   ├── DatabaseConfig.java
                │   └── DatabaseConnectionTest.java
                ├── enums/
                │   ├── PriorityLevel.java
                │   ├── ServiceCategory.java
                │   ├── TokenStatus.java
                │   └── UserRole.java
                ├── queue/
                │   ├── QueueDAO.java
                │   └── QueueMenu.java
                ├── reports/
                │   ├── ReportsDAO.java
                │   └── ReportsMenu.java
                ├── service/
                │   ├── ServiceDAO.java
                │   └── ServiceMenu.java
                ├── streams/
                │   └── StreamDemo.java
                └── token/
                    ├── TokenDAO.java
                    └── TokenMenu.java
```
Database design
The SQL schema creates six main tables:
Table	Purpose
Customer	Stores customer details
Service	Stores the services offered
Counter	Stores service counters and their associated services
Token	Stores issued tokens, priority, status, and estimated waiting time
ServiceHistory	Stores service start/completion information and remarks
Admin	Stores administrator/staff login records and roles


The schema uses primary keys, foreign keys, unique constraints, and MySQL ENUM columns to maintain relationships and valid values.
Token priorities
- NORMAL
- SENIOR
- EMERGENCY
Token statuses
The database currently defines these token statuses:
- WAITING – token is waiting in the queue.
- SERVING – customer is being served.
- COMPLETED – service has been completed.
- CANCELLED – token has been cancelled.
Service categories
The Java enum defines:
- GENERAL
- BILLING
- ENQUIRY
- DOCUMENTATION
Setup and installation
Prerequisites
Install the following:
- JDK 17 or later.
- MySQL Server.
- Apache Maven.
- An IDE such as IntelliJ IDEA, Eclipse, or Visual Studio Code (optional).
1. Clone the repository
git clone <YOUR_GITHUB_REPOSITORY_URL>
cd QFlow
2. Create the database
Open MySQL and run:
CREATE DATABASE qflow;
USE qflow;
3. Create the tables
Run the schema file:
mysql -u root -p qflow < sql/schema.sql
Alternatively, open sql/schema.sql in MySQL Workbench and execute it after selecting the qflow database.
4. Configure the database connection
Open:
src/main/java/com/qflow/database/DatabaseConfig.java
Update the connection details to match your local MySQL setup:
public static final String URL = "jdbc:mysql://127.0.0.1:3306/qflow";
public static final String USERNAME = "root";
public static final String PASSWORD = "your_mysql_password";
Do not commit real database passwords to a public repository. Use environment variables or a local configuration file for credentials before deploying the application.
5. (Optional) Insert sample data
Run the sample data script if you want pre-filled customers, services, counters, and tokens:
mysql -u root -p qflow < sql/sample_data.sql
6. Build the project
From the project root, run:
mvn clean package
7. Run the application
Run the main class:
com.qflow.app.Main
You can also run it from an IDE after Maven has downloaded the MySQL Connector/J dependency.

The application first displays the admin access menu. After successful login, the QFlow main menu provides:
1. Customer Management
2. Service Management
3. Token Management
4. Queue Management
5. Reports & Analytics
6. Exit Application
SQL resources
- sql/schema.sql – creates the database tables and relationships.
- sql/sample_data.sql – inserts sample services, customers, counters, and tokens.
- sql/queries.sql – contains additional SQL queries for the project.
Notes
- The project is a console-based Java application; it does not currently include a web or mobile frontend.
- The application expects a MySQL database named qflow at the connection URL configured in DatabaseConfig.java.
- CSV import/export features depend on the file paths supplied or used by the application.
- The sample data script is optional. Do not run it repeatedly on a database that already contains the same explicit IDs unless you intend to reset or replace that data.
- Configure database credentials locally before running the application.
Future improvements
Possible extensions include:
- A graphical web interface for customers and staff.
- Live queue updates and display screens.
- SMS or email notifications when a token is approaching.
- Configurable queue and priority policies.
- Secure externalized configuration and stronger authentication controls.
QFlow – Manage queues smarter.
