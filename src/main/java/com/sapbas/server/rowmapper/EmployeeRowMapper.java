package com.sapbas.server.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.sapbas.server.model.Employee;

public class EmployeeRowMapper implements RowMapper<Employee> {
    public Employee mapRow(ResultSet rs, int rowNum) throws SQLException {
        Employee employee = new Employee();
        employee.setId(rs.getInt("ID"));
        employee.setFirstName(rs.getString("FIRST_NAME"));
        employee.setLastName(rs.getString("LAST_NAME"));
        employee.setAge(rs.getInt("AGE"));
        return employee;
    }

}
