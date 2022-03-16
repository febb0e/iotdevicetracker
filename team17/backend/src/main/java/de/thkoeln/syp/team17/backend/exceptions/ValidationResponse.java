package de.thkoeln.syp.team17.backend.exceptions;

public class ValidationResponse {

    private String error;

    public ValidationResponse() {

    }

    public ValidationResponse(String error) {
        this.error = error;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

}
