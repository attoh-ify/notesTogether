package com.example.notesTogether;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class NotesTogetherApplication {

	public static void main(String[] args) {
		SpringApplication.run(NotesTogetherApplication.class, args);
	}

}
