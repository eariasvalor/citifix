package com.cityfix.citifix;

import org.springframework.boot.SpringApplication;

public class TestCitifixApplication {

	public static void main(String[] args) {
		SpringApplication.from(CitifixApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
