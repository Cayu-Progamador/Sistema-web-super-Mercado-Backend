package com.backendSupermercado.supermercasdo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class SupermercasdoApplication {

	@GetMapping("/saludar")
	public String saludar () {
		return "Hola Mundo";
	}
	public static void main(String[] args) {
		SpringApplication.run(SupermercasdoApplication.class, args);
	}

}
