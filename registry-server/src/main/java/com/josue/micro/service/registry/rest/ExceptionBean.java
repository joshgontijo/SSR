package com.josue.micro.service.registry.rest;


public class ExceptionBean {

    private final String code;
    private final String message;

    public ExceptionBean(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
