package com.sapbas.server.javaconfig;

import javax.ws.rs.ApplicationPath;

import org.glassfish.jersey.server.ResourceConfig;

import com.sapbas.server.exception.AuthenticationExceptionHandler;
import com.sapbas.server.exception.NotFoundExceptionHandler;
import com.sapbas.server.filter.AuthenticationFilter;
import com.sapbas.server.filter.AuthorizationFilter;
import com.sapbas.server.rest.AuthenticationEndpoint;
import com.sapbas.server.rest.EmployeeResource;

@ApplicationPath("/resources")
public class RestConfig extends ResourceConfig {
    public RestConfig() {
        registerClasses(EmployeeResource.class, AuthenticationEndpoint.class, NotFoundExceptionHandler.class, AuthenticationExceptionHandler.class, AuthenticationFilter.class, AuthorizationFilter.class);
    }
}