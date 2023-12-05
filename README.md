# Demo-App
![](https://img.shields.io/badge/build-success-brightgreen.svg)

# Stack
![](https://img.shields.io/badge/java_17-✓-blue.svg)
![](https://img.shields.io/badge/spring_boot_2.7.12-✓-blue.svg)
![](https://img.shields.io/badge/mysql_8.0.16-✓-blue.svg)
![](https://img.shields.io/badge/liquibase_4.9.1-✓-blue.svg)
![](https://img.shields.io/badge/jwt-✓-blue.svg)
![](https://img.shields.io/badge/swagger_2.9.2-✓-blue.svg)
![](https://img.shields.io/badge/maven-✓-blue.svg)
![](https://img.shields.io/badge/docker-✓-blue.svg)

# Introduction
Just to throw some background in, the presented project serves only as a simple demonstration of my adeptness with the showcased technical stack.
While this work is intended as a demonstrative exhibit rather than as a fully developed project, it's worth noting that it functions smoothly in every aspect,
and you're more than welcome to try it out.


# How to use this code?
1. Make sure you have [MySql 8.0.16](https://downloads.mysql.com/archives/installer/) installed
2. Create a new database with the following credentials:
   - `name: db`
   - `username: root`
   - `password: 12345`
   - `host: localhost:3306`
3. Open a cdm and navigate into a folder of your choice 
4. Clone this repository into this folder using the following command:
```
PS > git clone https://github.com/AngeloCrl/Demo.git
```
5. Open the project with the Intellij IDEA
6. Add a new Run Configuration and set: `Java 17`, `Maven wrapper` and the `spring-boot:run` command
7. Run the program by clicking the run-button or by pressing Shift+F10
8. Once the program starts, navigate to `http://localhost:8080/swagger-ui.html#/` in your browser to check everything is working correctly
9. You are ready to start sending requests.

# How to run with Docker
**No Database needs to be pre-installed.**
**Instead, you need to have [Docker](https://docs.docker.com/get-docker/) installed locally!**

1. Open a cdm and navigate into a folder of your choice
2. Clone this repository into this folder using the following command:
```
PS > git clone https://github.com/AngeloCrl/Demo.git
```
3. Open the project with the Intellij IDEA
4. Within the `application.properties` file:
   - un-comment the line 2: `spring.datasource.url=jdbc:mysql://db:3306/demo` 
   - comment-out the line 3: `spring.datasource.url=jdbc:mysql://localhost:3306/db?serverTimezone=UTC`
5. In the top-right corner of IntelliJ's window, use the `install` command at maven's toolbar, 
   in order for the jar-file to be created: `Maven > Lifecycle > install`
6. Open the Intellij's terminal and run the following command:
```
PS > docker-compose up
```
7. Once docker is done creating the images and their respective containers, you may open your browser and navigate to: `http://localhost:8080/swagger-ui.html#/`
8. You are ready to start sending requests