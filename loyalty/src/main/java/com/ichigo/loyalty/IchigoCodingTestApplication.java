package com.ichigo.loyalty;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class IchigoCodingTestApplication {

	public static void main(String[] args) {
		SpringApplication.run(IchigoCodingTestApplication.class, args);
	}

}
