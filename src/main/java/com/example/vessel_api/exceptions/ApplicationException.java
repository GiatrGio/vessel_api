package com.example.vessel_api.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class ApplicationException extends ResponseStatusException {

    public ApplicationException(HttpStatus status, String message) {
        super(status, message);
    }
}
