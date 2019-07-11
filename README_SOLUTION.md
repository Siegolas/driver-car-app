# Driver car application solution

## Table of contents
* [General info](#general-info)
* [Considerations](#considerations)
* [Modifications on original code](#modifications-on-original-code)
* [Development considerations](#development-considerations)
* [Task development](#task-development)
* [Task 1](#task-1)
* [Task 2](#task-2)
* [Task 3](#task-3)
* [Task 4 (optional)](#task-4-optional)
* [Swagger interface](#swagger-interface)
* [Postman tool](#postman-tool)
* [Getting Started](#getting-started)
* [Prerequisites](#prerequisites)
* [Build the project](#build-the-project)
* [Application execution](#application-execution)
* [Testing](#testing)
* [Data Access Object tests](#data-access-object-tests)
* [Service tests](#service-tests)
* [Controller tests](#controller-tests)
* [Integration testing](#integration-testing)
* [Built With](#built-with)
* [Authors](#authors)
* [Acknowledgments](#acknowledgments)

## General info

The solution provides the required development for all the Tasks proposed in the original README file.

### Considerations

#### Modifications on original code

I have tried not to modify the original code as far as possible. However, I have made the following modifications / corrections:

```
- ConstraintsViolationException: Suppress reason from @ResponseStatus to show the value of the message attribute
- data.sql: To add insert statements for car and manufacturer table, and to assign cars to drivers
- DefaultDriverService: Add methods required by the tasks functionalities
- DriverController: Changes to add tasks functionalities
- DriverDO: Specify 'strategy = GenerationType.IDENTITY' on id property. Insert new cars threw error without 
  applying the strategy. The error trace was: 'could not execute statement; SQL [n/a]; constraint ["PRIMARY KEY ON 
  PUBLIC.DRIVER(ID)";' 
- DriverDO: Add the relationship with car entity
- DriverDTO: Add assignedCar attribute
- DriverMapper: Map DriverDO car attribute to DriverDTO assignedCar attribute
- DriverRepository: Add findByCar method
- DriverService: Add methods required by the tasks functionalities
- EntityNotFoundException: Suppress reason from @ResponseStatus to show the value of the message attribute
- pom.xml: Add dependencies related to Spring security and Querydsl
```

Proposed corrections:

```
- DriverController: deleteDriver can be applied on a driver that is already deleted
- Returned HttpStatus by DriverController endpoints: does not always comply with the conventions 
```

#### Development considerations

I have applied the default code formatter provided by IntelliJ

## Task development

### Task 1

I have added the classes required to add the Car controller functionality following the applied
conventions observed in the original project. This means adding the controller, the service, 
the repository, the mapper, the DTO, the DO and the exception classes. The manufacturer entity
uses a new table.

The Driver controller has been extended (as well as other classes) in order to allow a driver
to assign a car and unassign it.

I have included new insert statements to the data.sql file to add cars and manufacturers, as well
as assigning cars to drivers. 
 
### Task 2

Implement the functionality that checks that a car can be selected by only one online driver. If
another driver tries to select an already used car the operation throws a CarAlreadyInUseException.

### Task 3

For this task I have upgraded the Driver repository, extending the QuerydslPredicateExecutor interface.
A new endpoint has been added to the Driver controller that allows to perform searches on the drivers specifying
a series of both, driver and car attributes.

Searchable driver attributes:
```
- username
- onlinestatus
```

Searchable car attributes:
```
- licenseplate
- seatcount
- convertible
- rating
- enginetype
- manufacturer
```

The Driver controller findDriversByFilter method receives a request body that expects a map of Strings and Objects.
Each of the allowed attributes will be type checked. If the Object type is wrong, an exception will be thrown.  
If the attributes are correct, it will return a list with the drivers that fulfill all of the applied attributes.

### Task 4 (optional)

Basic authentication has been applied as the security implementation. To be able to access the controller endpoints
authentication is required. I have considered two kind of users, a user and an admin, each one having USER or ADMIN 
role.

The access to the endpoints is restricted by role, so that the query endpoints can be executed by the USER and ADMIN
roles, but the endpoints that modify the state of the entities (creation, update, deletion, assign / unassign car, etc.)
are only accessible for the ADMIN role.

User properties:
```
- name: user
- password: password
```

Admin properties:
```
- name: admin
- password: admin
```

### Swagger interface

When an endpoint is executed through the Swagger interface, a login prompt will appear if user has not logged before.
Apply valid credentials in the login prompt fields to execute the endpoints. Once the login is made, it will apply these
credentials to the following endpoint executions.

### Postman tool

When an endpoint is executed through the Postman tool, an unauthorized exception (httpStatus 401) will be shown if
Basic Auth type has not been applied. Apply Basic Auth authorization with valid credentials to execute the endpoints.
If the cookie JSESSIONID is kept, it will be applied to the following endpoint executions.

## Getting Started

These instructions will show you how to compile and start the application as well as running the included tests.

### Prerequisites

This project requires the following prerequisites:

- Java: Install [Java JDK](https://openjdk.java.net/install/) version 8 or higher.
- Maven: Install [Maven](https://maven.apache.org/download.cgi) version 3 or higher
- Git: Install [Git](https://git-scm.com/downloads)

### Build the project

In order to test, compile and package the project execute the following command:

```
- mvn package
```

## Application execution

In order to execute the application execute the following command:

```
- mvn spring-boot:run
```

It can also be executed executing:

```
java -jar target/app-1.0.0-SNAPSHOT.jar
```

## Testing

The solution includes Testing for the Data Access Objects, the Services and the Controllers,
covering with them all the performed implementation for this solution.

In order to execute the tests execute the following command:

```
- mvn test
```

### Data Access Object tests

These tests perform a series of checks on the data contained in the data.sql file found in the 
resources folder. When the tests are executed they run the application so that the database h2
loads the contents of the records that are in the file data.sql, in the same way that happens 
when the application is started normally.

The tests check all the methods found in the Repository interfaces: DriverRepository, CarRepository 
and ManufacturerRepository.

### Service tests

These tests perform a series of checks on the methods contained by the services. They are executed
with Mockito runner. It means that they only check the behavior of these services and the rest of the
components that these services use are mocked.

The tests check all the methods found in the services: DefaultDriverServiceTest, DefaultDriverServiceTest 
and DefaultManufacturerServiceTest.

### Controller tests

These tests perform a series of checks on the methods contained by the controllers. They are executed
with Spring runner, performing the calls with MockMvc and mocking the service that the controller
makes use of.

The tests check all the methods found in the controllers: DriverController and CarController.

### Integration testing

There are no integration tests included, it is only a matter of time, but all the functionalities 
has been tested through the Swagger interface provided, as well as through Postman tool. I have
added the Postman collection of requests for the application in the doc folder.

## Built With

* [Maven](https://maven.apache.org/) - Dependency Management

## Authors

* **Siegolas**

## Acknowledgments

* Spring
* Mockito & JUnit
* Inspiration