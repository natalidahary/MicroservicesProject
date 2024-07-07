package org.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Main {
    public static void main(String[] args) {
        System.setProperty("java.net.preferIPv4Stack", "true");
        SpringApplication.run(Main.class, args);
    }
}