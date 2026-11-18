# ⚡ QFlow — The Queue Has Left the Chat.

### 🎟️ Smart Queue & Token Management System

**Less waiting. More doing. Zero queue chaos.**

Ever walked into a college office, seen 47 people waiting, and immediately reconsidered your life choices?

Yeah. We built QFlow for that.

QFlow is a Java-based console application designed to manage customer registration, token generation, priority handling and queue operations in college administration offices.

Instead of the classic *"Who's last?"* situation, QFlow brings structure, smart token management and a little peace to the chaos.

---

## 🌟 The Main Character Energy

- 🎟️ **Token Generation** — Get your token without fighting for your place in line.
- 👤 **Customer Management** — Register customers and recognize returning visitors.
- ⚡ **Priority Management** — Emergency and senior-citizen priority rules.
- 📍 **Token Tracking** — Check token status and queue position.
- ⏳ **Wait-Time Estimates** — Know approximately how long the wait might be.
- 🏢 **Multiple Services** — Separate service categories for different college departments.

---

## 🏢 Where the Magic Happens

QFlow supports four college administration services:

| Service | Code | What it's for |
|---|---|---|
| 📝 Registrar | REG | Student records and registration |
| 💰 Accounts | ACC | Fees and financial services |
| 🎓 Admissions | ADM | Admission-related services |
| 📚 Examinations | EXM | Examination-related services |

Four departments. One system. Much less confusion.

---

## 🎭 Who's Using QFlow?

| Role | Responsibilities |
|---|---|
| 👤 Customer | Register, generate tokens and check token status |
| 🧑‍💼 Counter Operator | Manage service calls and token processing |
| 👑 Administrator | Manage services, priority authorization and reports |
| ⚙️ Internal System | Coordinate queue operations and automated processing |

---

## 🧠 Priority System

Not all situations are created equal.

QFlow uses three priority levels:

| Priority | Level | Description |
|---|---:|---|
| 🚨 EMERGENCY | 1 | Authorized emergency requests |
| 👴 SENIOR_CITIZEN | 2 | Eligible senior citizens |
| 🟢 NORMAL | 3 | Regular customers |

**Important:** Emergency priority requires staff authorization. Customers cannot simply select emergency because they're running late. Nice try though. 💀

---

## 🎫 Token Format

Every token gets a unique, readable identifier.

```text
TKN-<SERVICE>-<DATE>-<SEQUENCE>
```

Example:

```text
TKN-ACC-20261118-0001
TKN-REG-20261118-0002
TKN-EXM-20261118-0003
```

Your token. Your place. Your moment.

---

## 🛠️ Tech Stack

| Technology | Purpose |
|---|---|
| ☕ Java 17 | Core programming language |
| 📦 Apache Maven | Build and dependency management |
| 🗄️ MySQL 8 | Database for the complete system |
| 🧪 JUnit 5 | Unit testing |
| 🔧 Git & GitHub | Version control and collaboration |
| 💻 Console CLI | User interaction |

---

## 🏗️ Project Architecture

QFlow follows a layered architecture to keep responsibilities organized.

```text
QFlow
│
├── Presentation Layer
│   └── Console interaction
│
├── Application Layer
│   └── CustomerService
│   └── TokenService
│
├── Domain Layer
│   └── Customer
│   └── Token
│   └── Enums
│
├── Repository Contracts
│   └── Data access interfaces
│
└── Infrastructure Layer
    └── Database
    └── Queue coordination
    └── Concurrency
    └── Analytics
```

Each layer has a responsibility. No unnecessary spaghetti code. 🍝

---

## 👩‍💻 Team Contributions

QFlow is developed collaboratively by four team members.

| Member | Responsibility |
|---|---|
| Member 1 | 👤 Customer & Token Management |
| Member 2 | 🔄 Queue Management |
| Member 3 | 🗄️ JDBC & Database Management |
| Member 4 | ⚙️ Concurrency, Analytics & File Handling |

### Member 1 — Customer & Token Management

This module handles the customer and token lifecycle.

Implemented components:

- Customer and Token domain models.
- Priority, CustomerCategory and TokenStatus enums.
- CustomerRegistrationDTO.
- TokenRequestDTO.
- TokenSlipDTO.
- TokenStatusDTO.
- CustomerService and TokenService.
- InputValidator.
- TokenGenerator.
- Repository and QueueManager integration contracts.
- Unit tests and in-memory test doubles.

**Module status:** Completed and independently tested.

---

## 🚀 Getting Started

Ready to run QFlow? Let's get it.

### Prerequisites

Make sure you have:

- Java Development Kit (JDK).
- Java 17 language compatibility.
- Maven Wrapper included in the project.
- Git (optional, for version control).

### 1. Clone the repository

```bash
git clone <YOUR_GITHUB_REPOSITORY_URL>
```

### 2. Enter the project directory

```bash
cd QfLOW
```

### 3. Check Java

```bash
java -version
```

### 4. Check Maven

On Windows:

```cmd
.\mvnw.cmd -version
```

### 5. Compile the project

```cmd
.\mvnw.cmd clean compile
```

### 6. Run the tests

```cmd
.\mvnw.cmd clean test
```

### 7. Build the JAR

```cmd
.\mvnw.cmd clean package
```

If everything passes, Maven will display:

```text
BUILD SUCCESS
```

That's the green flag. ✅

---

## 🧪 Testing — We Don't Ship Bugs on Purpose

The Member 1 module has been tested using JUnit 5.

<details>
<summary>📊 Click to view test results</summary>

| Test Class | Tests |
|---|---:|
| CustomerTest | 4 |
| TokenTest | 3 |
| PriorityTest | 6 |
| CustomerCategoryTest | 1 |
| TokenStatusTest | 2 |
| DTOTest | 5 |
| InputValidatorTest | 39 |
| TokenGeneratorTest | 10 |
| CustomerServiceTest | 7 |
| TokenServiceTest | 12 |
| QFlowSmokeTest | 1 |
| **TOTAL** | **90** |

**Reported results:**

- ✅ Passed: 90
- ❌ Failed: 0
- ⏭️ Skipped: 0

</details>

---

## 📂 Project Structure

```text
QfLOW/
│
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/qflow/
│   │   │       ├── exception/
│   │   │       ├── model/
│   │   │       │   ├── dto/
│   │   │       │   └── enums/
│   │   │       ├── queue/
│   │   │       ├── repository/
│   │   │       ├── service/
│   │   │       └── util/
│   │   │
│   │   └── resources/
│   │
│   └── test/
│       ├── java/
│       │   └── com/qflow/
│       └── resources/
│
├── target/
├── pom.xml
├── mvnw
├── mvnw.cmd
├── .gitignore
├── MEMBER1_PROGRESS.md
└── README.md
```

---

## 📌 Project Progress

Here's the current development checklist:

- [x] Project setup and Maven configuration
- [x] Customer domain model
- [x] Token domain model
- [x] DTO implementation
- [x] Input validation
- [x] Token number formatting
- [x] Customer service implementation
- [x] Token service implementation
- [x] Member 1 unit testing
- [x] Member 1 build and packaging verification
- [ ] Integration with Member 2's queue implementation
- [ ] Integration with Member 3's JDBC implementation
- [ ] Integration with Member 4's concurrency and presentation modules
- [ ] Complete system testing

**We're making progress. The final boss is system integration.** 🎮

---

## 🔮 Future Scope

Possible improvements for future versions:

- 🖥️ Graphical user interface.
- 📱 Mobile-based token tracking.
- 🔔 Real-time customer notifications.
- 📊 Advanced queue analytics.
- 🌐 Web-based administration dashboard.
- 📈 Historical queue performance reports.

---

## 🤝 Contributing

Want to improve QFlow?

1. Fork the repository.
2. Create a feature branch.
3. Make your changes.
4. Test your implementation.
5. Submit a pull request.

Keep the code clean, the commits meaningful and the queue moving.

---

## 💬 Final Words

QFlow is built around one simple idea:

**People came to get their work done, not to spend their entire day standing in a queue.**

Less chaos. Better organization. Smarter service.

Made with ☕ Java, teamwork and a questionable amount of debugging.

### ⚡ QFlow — Your Queue. Your Turn. Your Time.

---

<sub>Academic Project | Java 17 | Maven | JUnit 5</sub>
