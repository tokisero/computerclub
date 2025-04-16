package com.tokiserskyy.computerclub;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.tokiserskyy.computerclub")
public class ComputerClubApplication {
	public static void main(String[] args) {
		SpringApplication.run(ComputerClubApplication.class, args);
	}
}