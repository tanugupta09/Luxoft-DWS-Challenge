package com.dws.challenge.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;

@Configuration
public class AccountConfiguration {
	
	@Bean
	public MethodValidationPostProcessor methodValidationPostProcessor() {
	     return new MethodValidationPostProcessor();
	}

}
