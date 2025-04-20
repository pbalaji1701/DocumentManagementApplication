package com.jk;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication
public class DocumentManagementApplication {

	public static void main(String[] args) {
		SpringApplication.run(DocumentManagementApplication.class, args);
	}

}
