package com.flashcards.domain.exceptions;

public class UnprocessableEntityException extends RuntimeException {

    private String code;

    public UnprocessableEntityException(String msg, String code) {
        super(msg);
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
    }
}
