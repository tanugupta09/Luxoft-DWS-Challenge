package com.dws.challenge.exception;

import javax.validation.ConstraintViolationException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler{
	
	@ExceptionHandler(value = {
		    ConstraintViolationException.class
		})
		
		public ResponseEntity<Object> handleResourceException(
			Exception ex ) {

		return new ResponseEntity(ex.getMessage(),HttpStatus.BAD_REQUEST);
	}
}
