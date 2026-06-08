package com.pi1.Edook.exception;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ErrorResponse {

    private String message;
    private int status;
    private String error;
    private List<String> details;

    public ErrorResponse(String message, int status, String error, List<String> details) {
        this.message = message;
        this.status = status;
        this.error = error;
        this.details = details;
    }
}
