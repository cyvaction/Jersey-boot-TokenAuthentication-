package com.sapbas.server.model;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Token {
    private String accessToken;

    public Token() {
        
    }
    
    public Token(String token) {
        this.accessToken = token;
    }
    
    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
}
