package de.thkoeln.syp.team17.backend.controllers;

import de.thkoeln.syp.team17.backend.exceptions.ValidationException;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.MediaType;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;

@ControllerAdvice
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class ExceptionController {

    @ApiResponse(responseCode = "422", description = "Unprocessable Entity", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, array = @ArraySchema( schema = @Schema(implementation = ObjectError.class))))
    @ExceptionHandler(ValidationException.class)
    @ResponseBody
    @ResponseStatus(UNPROCESSABLE_ENTITY)
    public List<ObjectError> handleValidationException(ValidationException ve) {
        return ve.getErrors();
    }

}
