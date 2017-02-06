package com.sapbas.server.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import com.sapbas.server.exception.EmployeeNotFound;
import com.sapbas.server.model.Employee;
import com.sapbas.server.rowmapper.EmployeeRowMapper;

@Component
public class EmployeeRepositoryImpl implements EmployeeRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<Employee> searchEmployees(String firstName, String lastName) {
        String sql = "SELECT * FROM EMPLOYEE WHERE FIRST_NAME = ? AND LAST_NAME = ?";

        List<Employee> employeeList = new ArrayList<Employee>();

        List<Map<String, Object>> rows = getJdbcTemplate().queryForList(sql, new Object[] { firstName, lastName });
        for (Map<String, Object> row : rows) {
            Employee employee = new Employee();
            employee.setId((Integer) (row.get("ID")));
            employee.setFirstName((String) row.get("FIRST_NAME"));
            employee.setLastName((String) row.get("LAST_NAME"));
            employee.setAge((Integer) row.get("AGE"));
            employeeList.add(employee);
        }

        return employeeList;
    }

    public Employee getEmployee(int id) {
        String sql = "SELECT * FROM EMPLOYEE WHERE ID = ?";

        Employee employee = (Employee) getJdbcTemplate().queryForObject(sql, new Object[] { id }, new EmployeeRowMapper());

        if (employee == null) {
            throw new EmployeeNotFound("No Employee with id: " + id + " not found");
        }
        return employee;
    }

    public void updateEmployee(Employee employee, int id) {
        String sql = "UPDATE EMPLOYEE SET FIRST_NAME = ?, LAST_NAME = ?, AGE = ? WHERE ID = ?";

        int updated_rows = this.jdbcTemplate.update(sql, employee.getFirstName(), employee.getLastName(), employee.getAge(), id);
        if (updated_rows == 0) {
            throw new EmployeeNotFound("No Employee with id: " + id + " not found");
        }
    }

    public void deleteEmployee(int id) {
        String sql = "DELETE FROM EMPLOYEE WHERE ID = ?";

        int deleted_rows = this.jdbcTemplate.update(sql, id);
        if (deleted_rows == 0) {
            throw new EmployeeNotFound("No Employee with id: " + id + " not found");
        }
    }

    public void addEmployee(Employee employee) {
        String sql = "INSERT INTO EMPLOYEE VALUES ( EMP_ID.NEXTVAL, ?, ?, ? )";

        this.jdbcTemplate.update(sql, employee.getFirstName(), employee.getLastName(), employee.getAge());
    }

    public JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }

    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

}
