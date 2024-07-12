package org.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Main {
    public static void main(String[] args) {
        // Set Dapr client to use HTTP
        System.setProperty("dapr.client.use.http", "true");
        SpringApplication.run(Main.class, args);
    }
}