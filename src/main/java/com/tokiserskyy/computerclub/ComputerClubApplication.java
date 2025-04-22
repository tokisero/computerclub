package com.tokiserskyy.computerclub;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication(scanBasePackages = "com.tokiserskyy.computerclub")
@EnableCaching
public class ComputerClubApplication {
	public static void main(String[] args) {
		SpringApplication.run(ComputerClubApplication.class, args);
	}
}