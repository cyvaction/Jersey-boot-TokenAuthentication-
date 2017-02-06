package com.sapbas.server.main.test;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.junit.Test;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sapbas.server.model.Employee;
import com.sapbas.server.model.LoginDetails;
import com.sapbas.server.model.Token;

public class JerseyApiIT {

    public static final String AUTH_URL = "http://localhost:9090/resources/authentication";
    
    public static final String SERVICE_URL = "http://localhost:9090/resources/employees";

    @Test
    public void givenSearchAllEmployees_whenCorrectRequest_thenResponseCodeSuccess() throws IOException {
        final HttpPost request = new HttpPost(AUTH_URL);
        request.setHeader("Accept", "application/json");
        LoginDetails loginDetails = new LoginDetails("admin", "admin");
        ObjectMapper mapper = new ObjectMapper();
        String loginDetailsJson = mapper.writeValueAsString(loginDetails);
        StringEntity input = new StringEntity(loginDetailsJson);
        input.setContentType("application/json");
        request.setEntity(input);
        final HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
        
        assertEquals(httpResponse.getStatusLine().getStatusCode(), HttpStatus.SC_OK);
        
        Token accessToken = mapper.readValue(httpResponse.getEntity().getContent(), Token.class);
        
        final HttpGet searchEmployeeRequest = new HttpGet(SERVICE_URL+"?firstName=Jane&lastName=Doe");
        searchEmployeeRequest.setHeader("Accept", "application/json");
        searchEmployeeRequest.setHeader("Authorization", "Bearer " + accessToken.getAccessToken());
        final HttpResponse searchResponse = HttpClientBuilder.create().build().execute(searchEmployeeRequest);
        
        assertEquals(searchResponse.getStatusLine().getStatusCode(), HttpStatus.SC_OK);
        
        List<Employee> empList = mapper.readValue(searchResponse.getEntity().getContent(), new TypeReference<List<Employee>>(){});
        
        assertEquals(empList.get(0).getFirstName(), "Jane");
        
    }

    /*@Test
    public void givenGetEmployee_whenEmployeeExists_thenResponseCodeSuccess() throws IOException {
        final HttpUriRequest request = new HttpGet(SERVICE_URL + "/1");
    
        final HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
    
        assertEquals(httpResponse.getStatusLine().getStatusCode(), HttpStatus.SC_OK);
    }
    
    @Test
    public void givenGetEmployee_whenEmployeeDoesNotExist_thenResponseCodeNotFound() throws IOException {
        final HttpUriRequest request = new HttpGet(SERVICE_URL + "/1000");
    
        final HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
    
        assertEquals(httpResponse.getStatusLine().getStatusCode(), HttpStatus.SC_NOT_FOUND);
    }
    
    @Test
    public void givenGetEmployee_whenJsonRequested_thenCorrectDataRetrieved() throws IOException {
        final HttpUriRequest request = new HttpGet(SERVICE_URL + "/1");
    
        request.setHeader("Accept", "application/json");
        final HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
        ObjectMapper mapper = new ObjectMapper();
        Employee emp = mapper.readValue(httpResponse.getEntity().getContent(), Employee.class);
    
        assertEquals(emp.getFirstName(), "Jane");
    }
    
    @Test
    public void givenAddEmployee_whenJsonRequestSent_thenResponseCodeCreated() throws IOException {
        final HttpPost request = new HttpPost(SERVICE_URL);
    
        Employee emp = new Employee(5, "Johny", "Doe", 23);
        ObjectMapper mapper = new ObjectMapper();
        String empJson = mapper.writeValueAsString(emp);
        StringEntity input = new StringEntity(empJson);
        input.setContentType("application/json");
        request.setEntity(input);
        final HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
    
        assertEquals(httpResponse.getStatusLine().getStatusCode(), HttpStatus.SC_CREATED);
    }
    
    @Test
    public void givenAddEmployee_whenRequestForExistingObjectSent_thenResponseCodeConflict() throws IOException {
        final HttpPost request = new HttpPost(SERVICE_URL);
    
        Employee emp = new Employee(1, "Johny", "Doe", 25);
        ObjectMapper mapper = new ObjectMapper();
        String empJson = mapper.writeValueAsString(emp);
        StringEntity input = new StringEntity(empJson);
        input.setContentType("application/json");
        request.setEntity(input);
        final HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
    
        assertEquals(httpResponse.getStatusLine().getStatusCode(), HttpStatus.SC_CONFLICT);
    }*/

}