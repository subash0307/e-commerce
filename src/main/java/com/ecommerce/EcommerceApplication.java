package com.ecommerce;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main Entry Point for the E-Commerce Application.
 *
 * Architectural Note (Interview Prep):
 * @SpringBootApplication is a meta-annotation that encapsulates three critical annotations:
 * 1. @Configuration: Tags the class as a source of bean definitions for the application context.
 * 2. @EnableAutoConfiguration: Tells Spring Boot to start adding beans based on classpath settings,
 *    other beans, and various property settings (e.g., auto-configuring HikariDataSource when MySQL driver is present).
 * 3. @ComponentScan: Directs Spring to scan the current package ('com.ecommerce') and all its sub-packages
 *    for Spring components, services, repositories, and controllers.
 */
@SpringBootApplication
public class EcommerceApplication {

    public static void main(String[] args) {
        SpringApplication.run(EcommerceApplication.class, args);
    }

}
