package com.jk.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.annotations.OpenAPI31;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class SwaggerConfig {
	
	 @Bean
	    public OpenAPI customOpenAPI() {
	        return (OpenAPI) new OpenAPI()
	                .info(new Info()
	                        .title("Document Management API")
	                        .version("1.0")
	                        .description("API for document ingestion and Q&A"));
	    }

}
