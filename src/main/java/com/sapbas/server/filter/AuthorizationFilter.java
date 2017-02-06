package com.sapbas.server.filter;

import java.io.IOException;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import com.sapbas.server.privileges.Privilege;
import com.sapbas.server.security.Secured;

@Secured
@Provider
@Priority(Priorities.AUTHORIZATION)
public class AuthorizationFilter implements ContainerRequestFilter {

    private final Logger logger = LoggerFactory.getLogger(AuthorizationFilter.class);
    
    @Context
    private ResourceInfo resourceInfo;
    
    @Autowired
    JdbcTemplate jdbcTemplate;

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        
        // Get the resource class which matches with the requested URL
        // Extract the roles declared by it
        Class<?> resourceClass = resourceInfo.getResourceClass();
        List<Privilege> classPrivs = extractPrivs(resourceClass);

        // Get the resource method which matches with the requested URL
        // Extract the roles declared by it
        Method resourceMethod = resourceInfo.getResourceMethod();
        List<Privilege> methodPrivs = extractPrivs(resourceMethod);

        try {

            // Check if the user is allowed to execute the method
            // The method annotations override the class annotations

            String username = requestContext.getSecurityContext().getUserPrincipal().getName();

            if (methodPrivs.isEmpty()) {
                checkPermissions(classPrivs, username);
            } else {
                checkPermissions(methodPrivs, username);
            }

        } catch (Exception e) {
            logger.error("Exception", e);
            requestContext.abortWith(Response.status(Response.Status.FORBIDDEN).build());
        }
    }

    // Extract the roles from the annotated element
    private List<Privilege> extractPrivs(AnnotatedElement annotatedElement) {
        if (annotatedElement == null) {
            return new ArrayList<Privilege>();
        } else {
            Secured secured = annotatedElement.getAnnotation(Secured.class);
            if (secured == null) {
                return new ArrayList<Privilege>();
            } else {
                Privilege[] allowedPrivs = secured.value();
                return Arrays.asList(allowedPrivs);
            }
        }
    }

    private void checkPermissions(List<Privilege> requiredPrivs, String username) {
        
        String role = "admin".equals(username)?"ROLE_WRITE":"ROLE_READ";
        
        List<String> roleList = new ArrayList<String>();
        roleList.add(role);
        
        /*List<Privilege> privList = new ArrayList<Privilege>();
        
        for(String userRole : roleList) {
            privList.addAll(roleMap.get(userRole));
        }*/
        
        List<String> privList=jdbcTemplate.queryForList(getQuery(roleList),String.class);
        System.out.println(getQuery(roleList));
        System.out.println(privList);
        boolean forbidden = false;
        
        for(Privilege priv : requiredPrivs) {
            if(!privList.contains(priv.toString())) {
                forbidden = true;
                break;
            }
        }
        
        if(forbidden) {
            throw new javax.ws.rs.ForbiddenException();
        }
    }
    
    public String getQuery(List<String> roles) {

        String roleNameList = null;
        for(String roleName : roles) {
            if(roleNameList!=null) {
                roleNameList = roleNameList + "'" + roleName + "',";
            }
            roleNameList = "'" + roleName + "'";
        }
        
        return "SELECT ACCESS from ROLEACCESS WHERE ROLE IN (" + roleNameList + ")"; 
    }
}