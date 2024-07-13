package org.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"org.example.config", "org.example.service", "org.example.controller"})
@EnableCaching
public class Main {
    public static void main(String[] args) {
        // Set Dapr client to use HTTP
        System.setProperty("dapr.client.use.http", "true");
        SpringApplication.run(Main.class, args);
    }
}