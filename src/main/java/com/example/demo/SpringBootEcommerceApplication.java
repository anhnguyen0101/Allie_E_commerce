package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootApplication
@ComponentScan(basePackages = "com.example.demo")
public class SpringBootEcommerceApplication {

	public static void main(String[] args) {
		log.info("🚀 ========================================");
		log.info("🚀 Starting E-Commerce Application...");
		log.info("🚀 ========================================");
		
		SpringApplication.run(SpringBootEcommerceApplication.class, args);
		
		log.info("✅ ========================================");
		log.info("✅ E-Commerce Application Started!");
		log.info("✅ Check for CORS Filter initialization above");
		log.info("✅ ========================================");
	}

}
