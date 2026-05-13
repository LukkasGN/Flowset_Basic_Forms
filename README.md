# BPM Engine + Spring Boot Application

A simple demo application demonstrating BPM Engine integration with Spring Boot.
Includes a minimal BPMN process with a Java Delegate that prints "Hello, World!" to the console.

## ⚙️ Technologies

- Java 17 (or other version)
- Spring Boot 3.4.x (or other version)
- Camunda/Operaton
- Database: H2
- Maven/Gradle

## 🚀 Quick Start

### Running the Application

For Maven:
   ```bash
     mvn spring-boot:run
   ```

or for Gradle:
   ```bash
      gradle bootRun
   ```

### Access:

🔗 http://localhost:8080

### Credentials

Login: `user`

Password: `pass`

## 📌 Process Overview

### BPMN Process (simple-process.bpmn)

**Simple flow:**

Start → Service Task (Java Delegate) → End

```xml
<?xml version="1.0" encoding="UTF-8"?>
<definitions xmlns="http://www.omg.org/spec/BPMN/20100524/MODEL" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:camunda="http://camunda.org/schema/1.0/bpmn" xmlns:bpmndi="http://www.omg.org/spec/BPMN/20100524/DI" xmlns:omgdc="http://www.omg.org/spec/DD/20100524/DC" xmlns:omgdi="http://www.omg.org/spec/DD/20100524/DI" xmlns:xsd="http://www.w3.org/2001/XMLSchema" targetNamespace="http://bpmn.io/schema/bpmn" xsi:schemaLocation="http://www.omg.org/spec/BPMN/20100524/MODEL http://www.omg.org/spec/BPMN/2.0/20100501/BPMN20.xsd">
    <process id="simple-process" name="Simple process" isExecutable="true" camunda:historyTimeToLive="P30D">
        <startEvent id="startEvent1">
            <outgoing>Flow_0sayidq</outgoing>
        </startEvent>
        <sequenceFlow id="Flow_0sayidq" sourceRef="startEvent1" targetRef="Activity_16ohsxx" />
        <endEvent id="Event_084x5ej">
            <incoming>Flow_1irf8s2</incoming>
        </endEvent>
        <sequenceFlow id="Flow_1irf8s2" sourceRef="Activity_16ohsxx" targetRef="Event_084x5ej" />
        <serviceTask id="Activity_16ohsxx" name="Say Hello" camunda:class="com.example.workflow.delegate.SayHelloDelegate">
            <incoming>Flow_0sayidq</incoming>
            <outgoing>Flow_1irf8s2</outgoing>
        </serviceTask>
    </process>
</definitions>

```

### Java Delegate (SayHelloDelegate.java)

Prints a message to the console:

```java
public class SayHelloDelegate implements JavaDelegate {

    private static final Logger log = LoggerFactory.getLogger(SayHelloDelegate.class);

    @Override
    public void execute(DelegateExecution execution) {
        log.info("JavaDelegate: Hello, World!");
    }
}
```