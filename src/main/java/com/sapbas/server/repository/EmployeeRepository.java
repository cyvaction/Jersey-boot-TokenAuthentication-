package com.sapbas.server.repository;

import java.util.List;

import com.sapbas.server.model.Employee;

public interface EmployeeRepository {

    public List<Employee> searchEmployees(String firstName, String lastName);

    public Employee getEmployee(int id);

    public void updateEmployee(Employee employee, int id);

    public void deleteEmployee(int id);

    public void addEmployee(Employee employee);
}
