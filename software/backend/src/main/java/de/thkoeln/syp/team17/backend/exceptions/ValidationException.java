package de.thkoeln.syp.team17.backend.exceptions;

import lombok.Getter;
import org.springframework.validation.ObjectError;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

@Getter
public class ValidationException extends RuntimeException {

    private final List<ObjectError> errors;
    private final String message;

    public ValidationException(String message) {
        errors = new LinkedList<>();
        this.message = message;
    }

    public ValidationException(ObjectError... errors) {
        this.errors = Arrays.asList(errors);
        this.message = "ValidationException";
    }

    public ValidationException(List<ObjectError> errors) {
        this.errors = new LinkedList<>(errors);
        this.message = "ValidationException";
    }

}
