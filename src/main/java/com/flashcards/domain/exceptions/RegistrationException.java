package com.flashcards.domain.exceptions;

public class RegistrationException extends RuntimeException {

    private String code;

    public RegistrationException(String msg, String code) {
        super(msg);
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
    }
}
