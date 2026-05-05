package com.ritvik.zodiacverseBackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class ZodiacverseBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(ZodiacverseBackendApplication.class, args);
	}

}
