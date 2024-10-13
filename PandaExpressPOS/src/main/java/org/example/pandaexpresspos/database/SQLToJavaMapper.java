package org.example.pandaexpresspos.database;

import org.example.pandaexpresspos.models.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

class SQLToJavaMapper {
    public static Order orderMapper(ResultSet rs) {
        try {
            return new Order(
                    UUID.fromString(rs.getString("orderId")),
                    UUID.fromString(rs.getString("cashierId")),
                    rs.getInt("month"),
                    rs.getInt("week"),
                    rs.getInt("day"),
                    rs.getInt("hour"),
                    rs.getDouble("price")
            );
        } catch (SQLException e) {
            throw new RuntimeException("Error mapping ResultSet to Order", e);
        }
    }

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

    public static InventoryItem inventoryItemMapper(ResultSet rs) {
        try {
            return new InventoryItem(
                    UUID.fromString(rs.getString("inventoryItemId")),
                    rs.getDouble("cost"),
                    rs.getInt("availableStock"),
                    rs.getString("itemName")
            );
        } catch (SQLException e) {
            throw new RuntimeException("Error mapping ResultSet to InventoryItem", e);
        }
    }

    public static MenuItem menuItemMapper(ResultSet rs) {
        try {
            return new MenuItem(
                    UUID.fromString(rs.getString("menuItemId")),
                    rs.getDouble("price"),
                    rs.getInt("availableStock"),
                    rs.getString("itemName")
            );
        } catch (SQLException e) {
            throw new RuntimeException("Error mapping ResultSet to MenuItem", e);
        }
    }

}
