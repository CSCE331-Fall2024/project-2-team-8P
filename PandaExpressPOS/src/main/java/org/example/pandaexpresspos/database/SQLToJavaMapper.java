package org.example.pandaexpresspos.database;

import org.example.pandaexpresspos.models.*;
import org.example.pandaexpresspos.models.MenuItem;
import org.example.pandaexpresspos.models.wrappers.InventoryItemWithQty;
import org.example.pandaexpresspos.models.wrappers.MenuItemToInventoryItem;
import org.example.pandaexpresspos.models.wrappers.MenuItemWithQty;
import org.example.pandaexpresspos.models.wrappers.InventoryItemWithQty;

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

    public static MenuItemWithQty menuItemWithQtyMapper(ResultSet rs) {
        try {
            return new MenuItemWithQty(
                    new MenuItem(
                            UUID.fromString(rs.getString("menuItemId")),
                            rs.getDouble("price"),
                            rs.getInt("availableStock"),
                            rs.getString("itemName")
                    ),
                    rs.getInt("count")
            );
        } catch (SQLException e) {
            throw new RuntimeException("Error mapping ResultSet to MenuItemWithQty", e);
        }
    }

    public static InventoryItemWithQty inventoryItemWithQtyMapper(ResultSet rs) {
        try {
            return new InventoryItemWithQty(
                    new InventoryItem(
                            UUID.fromString(rs.getString("inventoryItemId")),
                            rs.getDouble("cost"),
                            rs.getInt("availableStock"),
                            rs.getString("itemName")
                    ),
                    rs.getInt("itemsUsed")
            );
        } catch (SQLException e) {
            throw new RuntimeException("Error mapping ResultSet to InventoryItemWithQty", e);
        }
    }

    public static MenuItemToInventoryItem menuItemToInventoryItemMapper(ResultSet rs) {
        try {
            return new MenuItemToInventoryItem(
                    new MenuItem(
                            UUID.fromString(rs.getString("menuItemId")),
                            rs.getDouble("menuItemPrice"),
                            rs.getInt("menuItemStock"),
                            rs.getString("menuItemName")
                    ),
                    new InventoryItem(
                            UUID.fromString(rs.getString("inventoryItemId")),
                            rs.getDouble("inventoryItemCost"),
                            rs.getInt("inventoryItemStock"),
                            rs.getString("inventoryItemName")
                    )
            );
        } catch (SQLException e) {
            throw new RuntimeException("Error mapping ResultSet to MenuItemToInventoryItem", e);
        }
    }

    public static Double orderSumMapper(ResultSet rs) {
        try {
            double orderSum = rs.getDouble("sum");
            return Math.round(orderSum * 100.0) / 100.0;
        } catch (SQLException e) {
            throw new RuntimeException("Error mapping ResultSet to Double", e);
        }
    }
}