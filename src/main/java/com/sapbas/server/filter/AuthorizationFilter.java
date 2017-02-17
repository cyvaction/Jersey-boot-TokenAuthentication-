package com.sapbas.server.filter;

import java.io.IOException;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Priority;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
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
import org.springframework.ldap.core.AttributesMapper;
import org.springframework.ldap.core.LdapTemplate;

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

    @Autowired
    private LdapTemplate ldapTemplate;

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {

        // Get the resource class which matches with the requested URL
        // Extract the roles declared by it
        Class<?> resourceClass = resourceInfo.getResourceClass();
        Privilege classPriv = extractPriv(resourceClass);

        // Get the resource method which matches with the requested URL
        // Extract the roles declared by it
        Method resourceMethod = resourceInfo.getResourceMethod();
        Privilege methodPriv = extractPriv(resourceMethod);

        try {

            // Check if the user is allowed to execute the method
            // The method annotations override the class annotations

            String username = requestContext.getSecurityContext().getUserPrincipal().getName();

            if (methodPriv.equals(methodPriv.DEFAULT)) {
                checkPermissions(classPriv, username);
            } else {
                checkPermissions(methodPriv, username);
            }

        } catch (Exception e) {
            logger.error("Exception", e);
            requestContext.abortWith(Response.status(Response.Status.FORBIDDEN).build());
        }
    }

    // Extract the roles from the annotated element
    private Privilege extractPriv(AnnotatedElement annotatedElement) {
        if (annotatedElement == null) {
            return Privilege.DEFAULT;
        } else {
            Secured secured = annotatedElement.getAnnotation(Secured.class);
            if (secured == null) {
                return Privilege.DEFAULT;
            } else {
                Privilege allowedPriv = secured.value();
                return allowedPriv;
            }
        }
    }

    private void checkPermissions(Privilege requiredPriv, String username) {

        List<String> userRoles = ldapTemplate.search("ou=roleusers", "uniqueMember=cn=" + username + ",ou=users,dc=example,dc=com", new AttributesMapper() {
            public Object mapFromAttributes(Attributes attrs) throws NamingException {
                return attrs.get("cn").get();
            }
        });


        List<String> permRoles = ldapTemplate.search("ou=rolepermissions", "uniqueMember=cn=" + requiredPriv.name() + ",ou=permissions,dc=example,dc=com", new AttributesMapper() {
            public Object mapFromAttributes(Attributes attrs) throws NamingException {
                return attrs.get("cn").get();
            }
        });

        boolean forbidden = true;
        
        for(String permRole : permRoles) {
            if(userRoles.contains(permRole)) {
                forbidden = false;
                break;
            }
        }

        if (forbidden) {
            throw new javax.ws.rs.ForbiddenException();
        }
    }

    public String getQuery(List<String> roles) {

        String roleNameList = null;
        for (String roleName : roles) {
            if (roleNameList != null) {
                roleNameList = roleNameList + "'" + roleName + "',";
            }
            roleNameList = "'" + roleName + "'";
        }

        return "SELECT ACCESS from ROLEACCESS WHERE ROLE IN (" + roleNameList + ")";
    }

    public JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }

    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public LdapTemplate getLdapTemplate() {
        return ldapTemplate;
    }

    public void setLdapTemplate(LdapTemplate ldapTemplate) {
        this.ldapTemplate = ldapTemplate;
    }
}