package org.example.pandaexpresspos.database;

import org.example.pandaexpresspos.models.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

class SQLToJavaMapper {
    public static Employee employeeMapper(ResultSet rs) {
        try {
            return new Employee(
                    UUID.fromString(rs.getString("employeeId")),
                    rs.getBoolean("isManager"),
                    rs.getString("name")
            );
        } catch (SQLException e) {
            throw new RuntimeException("Error mapping ResultSet to Employee", e);
        }
    }

//    public static Order orderMapper(ResultSet rs) {
//
//    }
}
