package com.Santander.CQRS;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = "com.Satander.CQRS")
@EnableJpaRepositories(basePackages = "com.Satander.CQRS")
@EntityScan(basePackages = "com.Satander.CQRS")
public class CqrsBankApplication {
	public static void main(String[] args) {
		SpringApplication.run(CqrsBankApplication.class, args);
	}
}
