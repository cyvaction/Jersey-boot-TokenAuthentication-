package com.sapbas.server.exception;

public class AuthenticationFailure extends RuntimeException{

    static final long serialVersionUID = 1L;
    
    public AuthenticationFailure(String message) {
        super(message);
    }
}
