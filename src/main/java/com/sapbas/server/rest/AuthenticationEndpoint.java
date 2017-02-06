package com.sapbas.server.rest;

import java.util.Calendar;
import java.util.Date;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.sapbas.server.exception.AuthenticationFailure;
import com.sapbas.server.model.LoginDetails;
import com.sapbas.server.model.Token;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Component
@Path("/authentication")
public class AuthenticationEndpoint {

    static final long ONE_MINUTE_IN_MILLIS = 60000;

    @Value("${authentication.tokenValidityPeriod}")
    private String tokenValidityPeriod;

    @Value("${authentication.signingKey}")
    private String signingKey;

    @POST
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Token authenticateUser(LoginDetails loginDetails) {
        try {
            authenticate(loginDetails.getUsername(), loginDetails.getPassword());

            String token = issueToken(loginDetails.getUsername());

            return new Token(token);

        } catch (Exception e) {
            throw new AuthenticationFailure("Authentication failed");
        }
    }

    private void authenticate(String username, String password) throws Exception {

        if (("admin".equals(username) && "admin".equals(password))
                || ("user".equals(username) && "user".equals(password))) {
            
        }
        else {
            throw new Exception();
        }

    }

    private String issueToken(String username) {

        Calendar now = Calendar.getInstance();
        long nowInMillis = now.getTimeInMillis();
        Date expiryDate = new Date(nowInMillis + (Integer.parseInt(this.tokenValidityPeriod) * ONE_MINUTE_IN_MILLIS));

        String jwtToken = Jwts.builder().setSubject(username).setExpiration(expiryDate).signWith(SignatureAlgorithm.HS256, signingKey).compact();
        return jwtToken;
    }

    public String getTokenValidityPeriod() {
        return tokenValidityPeriod;
    }

    public void setTokenValidityPeriod(String tokenValidityPeriod) {
        this.tokenValidityPeriod = tokenValidityPeriod;
    }

    public String getSigningKey() {
        return signingKey;
    }

    public void setSigningKey(String signingKey) {
        this.signingKey = signingKey;
    }
}