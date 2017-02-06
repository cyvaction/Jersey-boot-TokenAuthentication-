package com.sapbas.server.repository;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.sapbas.server.exception.EmployeeAlreadyExists;
import com.sapbas.server.exception.EmployeeNotFound;
import com.sapbas.server.model.Employee;

@Component
public class EmployeeRepositoryImpl implements EmployeeRepository {
    private List<Employee> employeeList;

    public EmployeeRepositoryImpl() {
        employeeList = new ArrayList<Employee>();
        employeeList.add(new Employee(1, "Jane", "Doe", 23));
        employeeList.add(new Employee(2, "Jack", "Doe", 25));
        employeeList.add(new Employee(3, "George", "Doe", 30));
    }

    public List<Employee> getAllEmployees() {
        return employeeList;
    }

    public Employee getEmployee(int id) {
        for (Employee emp : employeeList) {
            if (emp.getId() == id) {
                return emp;
            }
        }
        throw new EmployeeNotFound("No Employee with id: "+id+" not found");
    }

    public void updateEmployee(Employee employee, int id) {
        for (Employee emp : employeeList) {
            if (emp.getId() == id) {
                emp.setId(employee.getId());
                emp.setFirstName(employee.getFirstName());
                emp.setLastName(employee.getLastName());
                emp.setAge(employee.getAge());
                return;
            }
        }
        throw new EmployeeNotFound("No Employee with id: "+id+" not found");
    }

    public void deleteEmployee(int id) {
        for (Employee emp : employeeList) {
            if (emp.getId() == id) {
                employeeList.remove(emp);
                return;
            }
        }
        throw new EmployeeNotFound("No Employee with id: "+id+" not found");
    }

    public void addEmployee(Employee employee) {
        for (Employee emp : employeeList) {
            if (emp.getId() == employee.getId()) {
                throw new EmployeeAlreadyExists("Employee with id: "+employee.getId()+" already exists");
            }
        }
        employeeList.add(employee);
    }
}
