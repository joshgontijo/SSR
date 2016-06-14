package com.josue.micro.service.registry;

/**
 * Created by Josue on 15/06/2016.
 */
public class ServiceException extends Exception {

    private final int code;

    public ServiceException(int code, String message) {
        super(message);
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
