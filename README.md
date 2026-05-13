# 🏦 Flowset Basic Forms — Bank Expense Approval Demo

A Spring Boot + Camunda 7 demo application implementing a **bank expense approval workflow** using Flowset BPM Studio. Employees submit expense requests through a form, which are then reviewed and approved or rejected by a manager.

---

## ⚙️ Technologies

- **Java 21** (JDK 21 LTS)
- **Spring Boot 3.4.4**
- **Camunda Platform 7.24.0** (embedded)
- **Flowset BPM Studio** plugin for IntelliJ IDEA
- **PostgreSQL 18** (production database)
- **Maven**

---

## 🗂️ Project Structure

```
src/
└── main/
    ├── java/com/example/workflow/
    │   └── FormProject003Application.java
    └── resources/
        ├── forms/
        │   ├── submit-form.form       # Employee expense submission form
        │   └── review-form.form       # Manager review/approval form
        ├── processes/
        │   └── form_process.bpmn      # Expense approval BPMN process
        └── application.properties
```

---

## 🔄 Process Overview

The expense approval process uses **swim lanes** to organize responsibilities:

```
[Employee Lane]        [Finance Manager Lane]     [System Lane]
Start → Submit Form → Review/Confirm Form → Gateway → Approved
                                                    ↓
                                                 Rejected
```

### BPMN Flow
- **Start Event** → triggered when an employee starts the process
- **Submit Task** (User Task, Employee lane) → employee fills in expense details
- **Confirm Task** (User Task, Finance Manager lane) → manager reviews and decides
- **Exclusive Gateway** → routes to Approved or Rejected based on manager decision
- **End Events** → Approved or Rejected

---

## 📋 Forms

### Submit Form
| Field | Key | Type |
|---|---|---|
| Employee Name | `employeeName` | Text Input |
| Department | `department` | Text Input |
| Expense Amount | `expenseAmount` | Number |
| Expense Category | `expenseCategory` | Select (Travel/Supplies/Entertainment/Other) |
| Justification | `description` | Text Input |

### Review Form
Same fields as Submit Form (read-only) plus:

| Field | Key | Type |
|---|---|---|
| Decision | `approved` | Select (Approve=true / Reject=false) |
| Reviewer Comments | `reviewerComments` | Text Input |

---

## 🚀 Quick Start

### Prerequisites
- JDK 21 installed
- PostgreSQL running on port 5432 with a database named `camunda`
- IntelliJ IDEA 2025.x with Flowset BPM Studio plugin

### 1. Clone the repository
```bash
git clone https://github.com/LukkasGN/Flowset_Basic_Forms.git
cd Flowset_Basic_Forms
```

### 2. Configure the database
Edit `src/main/resources/application.properties` and update:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/camunda
spring.datasource.username=postgres
spring.datasource.password=YOUR_POSTGRES_PASSWORD
```

### 3. Run the application
In IntelliJ, open the project and click the ▶ Run button on `FormProject003Application.java`.

Or via Maven:
```bash
mvn spring-boot:run
```

### 4. Access the application
| Interface | URL | Credentials |
|---|---|---|
| Camunda Tasklist | http://localhost:8080/camunda/app/tasklist | user / pass |
| Camunda Cockpit | http://localhost:8080/camunda/app/cockpit | user / pass |
| Camunda Admin | http://localhost:8080/camunda/app/admin | user / pass |

---

## 👥 Users & Groups

After first startup, create the following in Camunda Admin:

### Groups
| Group ID | Name | Type |
|---|---|---|
| `employees` | Employees | WORKFLOW |
| `managers` | Managers | WORKFLOW |

### Users
| User ID | Password | Group |
|---|---|---|
| `employee1` | employee1 | employees |
| `manager1` | manager1 | managers |

---

## 🔒 Authorizations

Authorization enforcement is enabled. Configure in Camunda Admin → Authorizations:

| Resource | Group | Permissions | Resource ID |
|---|---|---|---|
| Application | employees | ALL | tasklist |
| Application | managers | ALL | tasklist |
| Process Definition | employees | READ, CREATE_INSTANCE | form_process |
| Process Definition | managers | READ, READ_TASK, UPDATE_TASK | form_process |
| Process Instance | employees | CREATE | * |
| Process Instance | managers | READ, UPDATE | * |
| Task | employees | READ, UPDATE | * |
| Task | managers | READ, UPDATE | * |
| Historic Task Instance | employees | READ | * |
| Historic Task Instance | managers | READ | * |

---

## 🧪 Testing the Workflow

1. **Log in as `employee1`** in the Tasklist
2. Click **Start process** → select `form_process`
3. **Claim** the Submit task and fill in the expense form → Complete
4. **Log out** → **Log in as `manager1`**
5. **Claim** the Confirm task and make a decision → Complete
6. The process routes to **Approved** or **Rejected** based on the decision

---

## 🛠️ Optional Tools

### Flowset Control (monitoring)
```bash
git clone https://github.com/flowset/flowset-control-community.git
cd flowset-control-community
./gradlew bootRun
```
Access at http://localhost:8081 — credentials: `admin` / `admin`

Connect to engine at: `http://localhost:8080/engine-rest`

> **Note:** Requires JDK 17 alongside JDK 21.

### Flowset Tasklist (modern UI)
```bash
git clone https://github.com/flowset/flowset-tasklist-react-community
cd flowset-tasklist-react-community
cp .env.example .env.local
npm install
npm run dev
```
Access at http://localhost:3000

Configure `.env.local`:
```
VITE_BPM_ENGINE_API_URL=http://localhost:8080/engine-rest
VITE_BPM_ENGINE_TYPE=CAMUNDA_7
```

> **Note:** Flowset Tasklist only shows directly assigned tasks. Claim tasks in Camunda Tasklist first, then work on them in Flowset Tasklist.

---

## ⚠️ Known Issues

- **IntelliJ 2026.x** has a bug where the Flowset Explorer disappears after restart. Use **IntelliJ 2025.x** instead.
- The `start-flowset-control.bat` file in the root is a helper script to launch Flowset Control.

---

## 📄 License

This project is for educational/demo purposes.
