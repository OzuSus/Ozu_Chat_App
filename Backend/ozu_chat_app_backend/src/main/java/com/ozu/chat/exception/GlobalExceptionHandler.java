package com.ozu.chat.exception;

import java.util.stream.Collectors;

import com.ozu.chat.common.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(BadRequestException.class)
	ResponseEntity<ApiResponse<Void>> handleBadRequest(BadRequestException exception) {
		return ResponseEntity.badRequest().body(ApiResponse.fail(exception.getMessage()));
	}

	@ExceptionHandler(NotFoundException.class)
	ResponseEntity<ApiResponse<Void>> handleNotFound(NotFoundException exception) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.fail(exception.getMessage()));
	}

	@ExceptionHandler(ForbiddenException.class)
	ResponseEntity<ApiResponse<Void>> handleForbidden(ForbiddenException exception) {
		return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ApiResponse.fail(exception.getMessage()));
	}

	@ExceptionHandler({ AuthenticationException.class, BadCredentialsException.class })
	ResponseEntity<ApiResponse<Void>> handleAuthentication(RuntimeException exception) {
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.fail(exception.getMessage()));
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	ResponseEntity<ApiResponse<Void>> handleValidation(MethodArgumentNotValidException exception) {
		String message = exception.getBindingResult()
				.getFieldErrors()
				.stream()
				.collect(Collectors.groupingBy(FieldError::getField))
				.entrySet()
				.stream()
				.map(entry -> entry.getKey() + ": " + entry.getValue().getFirst().getDefaultMessage())
				.collect(Collectors.joining("; "));
		return ResponseEntity.badRequest().body(ApiResponse.fail(message));
	}

	@ExceptionHandler(Exception.class)
	ResponseEntity<ApiResponse<Void>> handleUnexpected(Exception exception) {
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
				.body(ApiResponse.fail("Unexpected server error"));
	}
}
