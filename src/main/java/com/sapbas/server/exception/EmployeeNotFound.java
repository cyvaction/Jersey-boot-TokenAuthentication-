package com.sapbas.server.exception;

public class EmployeeNotFound extends RuntimeException {

    static final long serialVersionUID = 3L;
    
    public EmployeeNotFound(String message) {
        super(message);
    }
}
