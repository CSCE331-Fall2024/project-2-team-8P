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

/**
 * The SQLToJavaMapper class provides methods for mapping SQL ResultSet data
 * to corresponding Java objects in the Panda Express POS system.
 *
 * <p>This class includes static methods that take a ResultSet as input and
 * convert the data from the ResultSet into Java objects such as Order,
 * Employee, InventoryItem, and MenuItem. Each mapping method handles
 * SQLException to ensure that errors during the mapping process are
 * appropriately reported.</p>
 *
 * @author Kevin Zhang
 */
class SQLToJavaMapper {

    /**
     * Maps a ResultSet row to an Order object.
     *
     * @param rs the ResultSet containing the order data
     * @return an Order object mapped from the ResultSet
     * @throws RuntimeException if an SQLException occurs during mapping
     */
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

    /**
     * Maps a ResultSet row to an Employee object.
     *
     * @param rs the ResultSet containing the employee data
     * @return an Employee object mapped from the ResultSet
     * @throws RuntimeException if an SQLException occurs during mapping
     */
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

    /**
     * Maps a ResultSet row to an InventoryItem object.
     *
     * @param rs the ResultSet containing the inventory item data
     * @return an InventoryItem object mapped from the ResultSet
     * @throws RuntimeException if an SQLException occurs during mapping
     */
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

    /**
     * Maps a ResultSet row to a MenuItem object.
     *
     * @param rs the ResultSet containing the menu item data
     * @return a MenuItem object mapped from the ResultSet
     * @throws RuntimeException if an SQLException occurs during mapping
     */
    public static MenuItem menuItemMapper(ResultSet rs) {
        try {
            return new MenuItem(
                    UUID.fromString(rs.getString("menuItemId")),
                    rs.getDouble("price"),
                    rs.getString("itemName")
            );
        } catch (SQLException e) {
            throw new RuntimeException("Error mapping ResultSet to MenuItem", e);
        }
    }

    /**
     * Maps a ResultSet row to a MenuItemWithQty object.
     *
     * @param rs the ResultSet containing the menu item and quantity data
     * @return a MenuItemWithQty object mapped from the ResultSet
     * @throws RuntimeException if an SQLException occurs during mapping
     */
    public static MenuItemWithQty menuItemWithQtyMapper(ResultSet rs) {
        try {
            return new MenuItemWithQty(
                    new MenuItem(
                            UUID.fromString(rs.getString("menuItemId")),
                            rs.getDouble("price"),
                            rs.getString("itemName")
                    ),
                    rs.getInt("count")
            );
        } catch (SQLException e) {
            throw new RuntimeException("Error mapping ResultSet to MenuItemWithQty", e);
        }
    }

    /**
     * Maps a ResultSet row to an InventoryItemWithQty object.
     *
     * @param rs the ResultSet containing the inventory item and quantity data
     * @return an InventoryItemWithQty object mapped from the ResultSet
     * @throws RuntimeException if an SQLException occurs during mapping
     */
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

    /**
     * Maps a ResultSet row to a MenuItemToInventoryItem object.
     *
     * @param rs the ResultSet containing the menu item and inventory item data
     * @return a MenuItemToInventoryItem object mapped from the ResultSet
     * @throws RuntimeException if an SQLException occurs during mapping
     */
    public static MenuItemToInventoryItem menuItemToInventoryItemMapper(ResultSet rs) {
        try {
            return new MenuItemToInventoryItem(
                    new MenuItem(
                            UUID.fromString(rs.getString("menuItemId")),
                            rs.getDouble("menuItemPrice"),
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

    /**
     * Maps a ResultSet row to a Double representing the order sum.
     *
     * @param rs the ResultSet containing the order sum data
     * @return a Double value representing the order sum
     * @throws RuntimeException if an SQLException occurs during mapping
     */
    public static Double orderSumMapper(ResultSet rs) {
        try {
            double orderSum = rs.getDouble("sum");
            return Math.round(orderSum * 100.0) / 100.0;
        } catch (SQLException e) {
            throw new RuntimeException("Error mapping ResultSet to Double", e);
        }
    }

    /**
     * Maps a ResultSet row to a Double representing the total count of orders.
     *
     * @param rs the ResultSet containing the total count data
     * @return a Double value representing the total count
     * @throws RuntimeException if an SQLException occurs during mapping
     */
    public static Double orderTotalMapper(ResultSet rs) {
        try {
            return (double) rs.getInt("count");
        } catch (SQLException e) {
            throw new RuntimeException("Error mapping ResultSet to Integer", e);
        }
    }
}
