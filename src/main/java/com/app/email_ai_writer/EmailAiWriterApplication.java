package com.app.email_ai_writer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.app.email_ai_writer")
public class EmailAiWriterApplication {

	public static void main(String[] args) {
		SpringApplication.run(EmailAiWriterApplication.class, args);
	}

}
