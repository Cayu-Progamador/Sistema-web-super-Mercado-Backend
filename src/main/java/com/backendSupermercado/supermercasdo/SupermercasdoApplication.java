package com.backendSupermercado.supermercasdo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SupermercasdoApplication {
	public static void main(String[] args) {
		SpringApplication.run(SupermercasdoApplication.class, args);
	}
}