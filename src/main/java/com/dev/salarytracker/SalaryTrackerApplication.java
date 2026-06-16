package com.dev.salarytracker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
public class SalaryTrackerApplication {

	public static void main(String[] args) {
		SpringApplication.run(SalaryTrackerApplication.class, args);
	}

}
