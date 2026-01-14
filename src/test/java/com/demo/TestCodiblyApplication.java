package com.demo;

import org.springframework.boot.SpringApplication;

public class TestCodiblyApplication {

	public static void main(String[] args) {
		SpringApplication.from(CodiblyApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
