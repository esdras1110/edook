package com.pi1.Edook.exception;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

// o globalexception serve para gerenciar todas as excessões lançadas
// retornando mensagens padronizadas 
@RestControllerAdvice
public class GlobalExceptionHandler{
    // esse metodo captura todas as excessões lançadas pelo BusinessException e retorna o primeiro erro
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException ex) {

        ErrorResponse error = new ErrorResponse(
                ex.getMessage(),
                ex.getStatus().value(),
                ex.getStatus().getReasonPhrase(),
                List.of(ex.getMessage())
        );

        return ResponseEntity
                .status(ex.getStatus())
                .body(error);
    }

    // esse metodo captura todas as excessões lançadas pela validação do jakarta
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {

        List<String> erros = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .toList();

        ErrorResponse response = new ErrorResponse(
                "Erro de validação",
                400,
                "Bad Request",
                erros
        );

        return ResponseEntity.badRequest().body(response);
    }
}