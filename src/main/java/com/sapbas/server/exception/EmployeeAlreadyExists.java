package com.sapbas.server.exception;

public class EmployeeAlreadyExists extends RuntimeException {

    static final long serialVersionUID = 1L;
    
    public EmployeeAlreadyExists(String message) {
        super(message);
    }
}
