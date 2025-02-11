package org.example.pandaexpresspos.controllers;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import org.example.pandaexpresspos.LoginApplication;
import javafx.event.ActionEvent;
import org.example.pandaexpresspos.database.DBDriverSingleton;
import org.example.pandaexpresspos.database.DBSnapshotSingleton;
import org.example.pandaexpresspos.models.Employee;
import org.example.pandaexpresspos.models.InventoryItem;
import org.example.pandaexpresspos.models.MenuItem;
import org.example.pandaexpresspos.models.Order;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.Optional;

/**
 * ManagerController is responsible for managing the behavior and functionality of the manager
 * interface in the POS system. It handles user interactions with adding/updating menu items,
 * inventory items, and employees, as well as viewing relevant reports and charts that are
 * of interest to the manager.
 *
 * <p>The controller uses a singleton database driver and snapshot to interact with the underlying
 * database and inventory system. It also updates the database when needed, allowing the cashier
 * to work with the updated database.</p>
 *
 * @author Soham Nagawanshi
 * @author Bradley James
 */
public class ManagerController {

    // Database logic
    private final DBDriverSingleton dbDriver = DBDriverSingleton.getInstance();
    private final DBSnapshotSingleton dbSnapshot = DBSnapshotSingleton.getInstance();

    private Employee loggedInUser;

    @FXML
    private GridPane inventoryItemsGridPane;
    @FXML
    private GridPane menuItemsGridPane;
    @FXML
    private GridPane employeeItemsGridPane;
    @FXML
    private TabPane itemsTabPane;
    @FXML
    private TabPane managerReportTabPane;
    @FXML
    private TableView<Order> ordersTable;
    @FXML
    private TableColumn<Order, UUID> Order;
    @FXML
    private TableColumn<Order, UUID> Cashier;
    @FXML
    private TableColumn<Order, String> Month;
    @FXML
    private TableColumn<Order, String> Day;
    @FXML
    private TableColumn<Order, String> Week;
    @FXML
    private TableColumn<Order, String> Hour;
    @FXML
    private TableColumn<Order, String> Price;
    @FXML
    private TextFlow summary;
    @FXML
    private BarChart<String, Double> xReportBarChart;
    @FXML
    private BarChart<String, Double> zReportBarChart;
    @FXML
    private BarChart<String, Number> salesChart;
    @FXML
    private BarChart<String, Number> productUsageChart;
    @FXML
    private DatePicker startDatePickerSalesReport;
    @FXML
    private DatePicker endDatePickerSalesReport;
    @FXML
    private DatePicker startDatePickerProductUsage;
    @FXML
    private DatePicker endDatePickerProductUsage;

    private int unpopularMenuItem = 500;
    private int popularMenuItem = 10;
    private int lowInventory = 50;
    private int veryLowInventory = 10;

    // Global Constant for Images
    private final String sampleImg = Objects.requireNonNull(getClass()
                    .getResource("/org/example/pandaexpresspos/fxml/images/sample_image.png"))
            .toExternalForm();

    /**
     * Enum to check which item tab user has selected
     */
    enum ItemTab {
        INVENTORY_ITEMS(0),
        MENU_ITEMS(1),
        EMPLOYEES(2);

        private final int value;

        // Constructor
        ItemTab(int value) {
            this.value = value;
        }

        // Method to get the value
        public int getValue() {
            return value;
        }

        // Static method to convert an integer to an enum value
        public static ItemTab fromValue(int value) {
            for (ItemTab tab : ItemTab.values()) {
                if (tab.getValue() == value) {
                    return tab;
                }
            }
            throw new IllegalArgumentException("No tab found with value: " + value);
        }
    }

    /**
     * Enum to check which report tab user has selected
     */
    enum ReportTab {

        ORDER_HISTORY(0),
        SUMMARY(1),
        USAGE(2),
        X_REPORT(3),
        Z_REPORT(4),
        SALES_REPORT(5);

        private final int value;

        // Constructor
        ReportTab(int value) {
            this.value = value;
        }

        // Method to get the value
        public int getValue() {
            return value;
        }

        // Static method to convert an integer to an enum value
        public static ReportTab fromValue(int value) {
            for (ReportTab tab : ReportTab.values()) {
                if (tab.getValue() == value) {
                    return tab;
                }
            }
            throw new IllegalArgumentException("No tab found with value: " + value);
        }
    }

    /**
     * Initialize the state of the UI after FXML elements are injected
     */
    @FXML
    public void initialize() {
        dbSnapshot.refreshAllSnapshots();
        createInventoryGrid();
        createMenuItemsGrid();
        createEmployeesGrid();
        createProductUsageChart();
        initSalesReportChart();

    }

    /**
     * Determines the logged in user.
     */
    public void setLoggedInUser(Employee user) {
        loggedInUser = user;
    }

    // Handle logout button click

    /**
     * Logs out the current user and returns them to the login screen.
     *
     * @param event ActionEvent triggered by clicking the logout button.
     * @throws IOException if the login screen cannot be loaded.
     */
    public void logout(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(LoginApplication.class.getResource("fxml/login-view.fxml"));
        // Create a new scene and set it to the stage
        Scene scene = new Scene(loader.load(), 1200, 800);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

    // Handle generating reports

    /**
     * Generates the requested report based on the selected tab.
     *
     * @param event ActionEvent triggered by clicking the generate
     *              report button.
     * @throws IOException if the report cannot be loaded.
     */
    public void generateReport(ActionEvent event) throws IOException {
        // Check which report tabl is selected
        ReportTab selectedTab = ReportTab.fromValue(managerReportTabPane.getSelectionModel().getSelectedIndex());

        switch (selectedTab) {
            case ORDER_HISTORY:
                fetchOrderHistory();
                break;
            case SUMMARY:
                fetchSummary();
                break;
            case USAGE:
                updateProductUsage();
                // TODO: add usage report
                break;
            case X_REPORT:
                fetchXOrZReport(false);
                break;
            case Z_REPORT:
                fetchXOrZReport(true);
                break;
            case SALES_REPORT:
                fetchSalesReport();
                break;
            default:
                break;
        }

    }


    // Handle adding items; special case of update item

    /**
     * Adds new item to the database table determined by which tab the user is currently on, e.g., inventory items,
     * menu items, or employees.
     *
     * @throws RuntimeException if the item is invalid.
     */
    public void addItem() throws RuntimeException {
        // check if inventory items, menuitems, or employees is selected
        ItemTab selectedTab = ItemTab.fromValue(itemsTabPane.getSelectionModel().getSelectedIndex());

        switch (selectedTab) {
            case INVENTORY_ITEMS:
                addOrUpdateInventoryItem(Optional.empty());
                break;
            case MENU_ITEMS:
                addOrUpdateMenuItem(Optional.empty());
                break;
            case EMPLOYEES:
                addOrUpdateEmployee(Optional.empty());
                break;
            default:
                throw new RuntimeException();

        }
    }

    // Use this to update or add new inventory items; if null inventory item is passed in, add new item

    /**
     * Adds or updates an inventory item.
     *
     * @param inventoryItem an optional representing the inventory item to add/update. Provide an empty optional to
     *                      indicate that we're creating a new inventory item
     */
    public void addOrUpdateInventoryItem(Optional<InventoryItem> inventoryItem) {
        Dialog<ButtonType> dialog = new Dialog<>();

        // Create the layout to add to dialog
        VBox inputsContainer = new VBox();

        Label inventoryItemNameLabel = new Label("Item Name: ");
        TextField inventoryItemName = new TextField();

        Label inventoryItemCostLabel = new Label("Inventory Item Cost: ");
        TextField inventoryItemCost = new TextField();

        Label availableStockLabel = new Label("Available Stock: ");
        TextField availableStock = new TextField();

        Label imageUrlLabel = new Label("Image Url: ");
        TextField imageUrl = new TextField();

        AtomicReference<String> dialogLabelName = new AtomicReference<>("Add");

        // If name is non-empty, we are in update mode
        inventoryItem.ifPresent(safeItem -> {
            inventoryItemName.setText(safeItem.itemName);
            inventoryItemCost.setText(String.valueOf(safeItem.cost));
            availableStock.setText(String.valueOf(safeItem.availableStock));
            imageUrl.setText(sampleImg);
            dialogLabelName.set("Update");
        });

        dialog.setTitle(dialogLabelName + " Inventory Item");
        dialog.setHeaderText(dialogLabelName + " Inventory Item");


        // Add to field
        inputsContainer.getChildren().addAll(
                inventoryItemNameLabel,
                inventoryItemName,
                inventoryItemCostLabel,
                inventoryItemCost,
                availableStockLabel,
                availableStock,
                imageUrlLabel,
                imageUrl
        );
        dialog.getDialogPane().setContent(inputsContainer);

        // Add buttons to dialog pane
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        // Define the "OK" button for the popup
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                //TODO: add validation
                return new ButtonType(
                        inventoryItemName.getText() + "," +
                                inventoryItemCost.getText() + ", " +
                                availableStock.getText() + ", " +
                                imageUrl.getText()
                );
            }
            return null;
        });

        // Handle the result when the user clicks "OK"
        Optional<ButtonType> result = dialog.showAndWait();
        result.ifPresent(outputFields -> {
            String[] outputs = outputFields.getText().split(",");
            String name = outputs[0];
            String cost = outputs[1];
            String stock = outputs[2];
            String imgUrl = outputs[3];

            // If no inventory item is passed in, we need to add a new one
            if (inventoryItem.isEmpty()) {
                dbDriver.insertInventoryItem(new InventoryItem(
                        Double.parseDouble(cost.trim()),
                        Integer.parseInt(stock.trim()),
                        name)
                );
            } else {
                // If the inventoryItem is not null, we are updating an existing item
                InventoryItem item = inventoryItem.get();
                item.cost = Double.parseDouble(cost.trim());
                item.availableStock = Integer.parseInt(stock.trim());
                item.itemName = name;

                dbDriver.updateInventoryItem(item);
            }

            // Refresh our snapshot
            dbSnapshot.refreshInventorySnapshot();

            // Redraw the grid to reflect the updates
            inventoryItemsGridPane.getChildren().clear();
            createInventoryGrid();

        });
    }

    // Use this to update or add new menu items; if null menu item is passed in, add new item

    /**
     * Adds or updates a menu item
     *
     * @param menuItem an optional representing the menu item to add/update. Provide an empty optional to
     *                 indicate that we're creating a new menu item
     */
    public void addOrUpdateMenuItem(Optional<MenuItem> menuItem) {
        Dialog<ButtonType> dialog = new Dialog<>();

        // Create the layout to add to dialog
        VBox inputsContainer = new VBox(10);
        HBox menuContainer = new HBox(20);
        VBox selectInventoryItems = new VBox(10);
        ScrollPane inventoryItemsScroll = new ScrollPane(selectInventoryItems);
        inventoryItemsScroll.maxHeightProperty().bind(menuContainer.heightProperty());

        Label menuItemNameLabel = new Label("Menu Item Name: ");
        TextField menuItemName = new TextField();

        Label menuItemPriceLabel = new Label("Menu Item Cost: ");
        TextField menuItemPrice = new TextField();

        Label imageUrlLabel = new Label("Image Url: ");
        TextField imageUrl = new TextField();

        AtomicReference<String> dialogLabelName = new AtomicReference<>("Add");

        // If non-empty menu item, populate text fields with item properties
        menuItem.ifPresent(safeItem -> {
            menuItemName.setText(safeItem.itemName);
            menuItemPrice.setText(String.valueOf(safeItem.price));
            imageUrl.setText(sampleImg);
            dialogLabelName.set("Update");  // Set dialog label to update
        });

        // Set the header and dialog to either Update or Add Menu Item
        dialog.setTitle(dialogLabelName + " Menu Item");
        dialog.setHeaderText(dialogLabelName + " Menu Item");

        // Add to dialog box
        inputsContainer.getChildren().addAll(
                menuItemNameLabel,
                menuItemName,
                menuItemPriceLabel,
                menuItemPrice,
                imageUrlLabel,
                imageUrl
        );
        Map<String, String> inventoryItemnameToID = new HashMap<>();

        for (InventoryItem item : dbSnapshot.getInventorySnapshot().values()) {
            selectInventoryItems.getChildren().add(new CheckBox(item.itemName));
            inventoryItemnameToID.put(item.itemName, item.inventoryItemId.toString());
        }
        menuContainer.getChildren().addAll(inputsContainer, inventoryItemsScroll);
        dialog.getDialogPane().setContent(menuContainer);

        menuItem.ifPresent(safeItem -> {
            // Retrieve associated inventory items for the selected menu item;
            List<InventoryItem> associatedInventoryItems =
                    dbDriver.selectMenuItemInventoryItems(safeItem.menuItemId);

            // Loop through all the checkboxes and check the ones associated with the MenuItem
            for (Node node : selectInventoryItems.getChildren()) {
                if (node instanceof CheckBox checkBox) {
                    String inventoryItemId = inventoryItemnameToID.get(checkBox.getText());
                    // Check if this inventory item is associated with the menu item
                    for (InventoryItem associatedItem : associatedInventoryItems) {
                        if (associatedItem.inventoryItemId.toString().equals(inventoryItemId)) {
                            checkBox.setSelected(true);
                        }
                    }
                }
            }
        });

        // Add buttons to dialog pane
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        // Handle the result
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                //TODO: add validation
                return new ButtonType(
                        menuItemName.getText() + "," +
                                menuItemPrice.getText() + ", " +
                                imageUrl.getText()
                );
            }
            return null;
        });

        Optional<ButtonType> result = dialog.showAndWait();
        result.ifPresent(outputFields -> {
            String[] outputs = outputFields.getText().split(",");
            String name = outputs[0];
            String price = outputs[1];
            String imgUrl = outputs[2];

            // If no menu item is passed in, we need to add a new one
            if (menuItem.isEmpty()) {
                UUID menuitemid;
                dbDriver.insertMenuItem(new MenuItem(
                        menuitemid = UUID.fromString(UUID.randomUUID().toString()),
                        Double.parseDouble(price.trim()),
                        name)
                );
                for (Node node : selectInventoryItems.getChildren()) {
                    if (node instanceof CheckBox checkBox) {
                        if (checkBox.isSelected()) {
                            String inventoryItemId = inventoryItemnameToID.get(checkBox.getText());
                            dbDriver.insertMenuItemToInventoryItem(menuitemid, UUID.fromString(inventoryItemId));
                        }
                    }
                }
            } else {
                // If the menu item is not null, we are updating an existing item
                MenuItem item = menuItem.get();
                dbDriver.deleteMenuItemToInventoryItem(item.menuItemId);
                for (Node node : selectInventoryItems.getChildren()) {
                    if (node instanceof CheckBox checkBox) {
                        if (checkBox.isSelected()) {
                            String inventoryItemId = inventoryItemnameToID.get(checkBox.getText());
                            dbDriver.insertMenuItemToInventoryItem(item.menuItemId, UUID.fromString(inventoryItemId));
                        }
                    }
                }

                item.price = Double.parseDouble(price.trim());
                item.itemName = name;

                dbDriver.updateMenuItem(item);
            }

            // Refresh our menu item snapshot
            dbSnapshot.refreshMenuSnapshot();

            // redraw the grid to reflect the updates
            menuItemsGridPane.getChildren().clear();
            createMenuItemsGrid();
        });
    }

    // Use this to update or add employees; if null employee is passed in, add new employee

    /**
     * Adds or updates an employee
     *
     * @param employee an optional representing the employee to add/update. Provide an empty optional to
     *                 indicate that we're creating a new employee
     */
    public void addOrUpdateEmployee(Optional<Employee> employee) {
        Dialog<ButtonType> dialog = new Dialog<>();

        // Create the layout to add to dialog
        VBox inputsContainer = new VBox();

        Label employeeNameLabel = new Label("Employee Name: ");
        TextField employeeName = new TextField();

        Label isManagerLabel = new Label("Manager: ");
        ToggleButton isManagerButton = new ToggleButton("NO");
        isManagerButton.setSelected(false);

        // Style button appropriately
        isManagerButton.setOnAction(e -> {
            if (isManagerButton.isSelected()) {
                isManagerButton.setText("YES");
            } else {
                isManagerButton.setText("NO");
            }
        });

        Label imageUrlLabel = new Label("Image Url: ");
        TextField imageUrl = new TextField();

        AtomicReference<String> dialogLabelName = new AtomicReference<>("Add");

        // If non-empty menu item, populate text fields with employee properties
        employee.ifPresent(safeEmployee -> {
            employeeName.setText(safeEmployee.name);
            isManagerButton.setSelected(safeEmployee.isManager);
            if (safeEmployee.isManager) {
                isManagerButton.setText("YES");
            } else {
                isManagerButton.setText("NO");
            }
            imageUrl.setText(sampleImg);
            dialogLabelName.set("Update");
        });

        // Set the header and dialog to either Update or Add Employee
        dialog.setTitle(dialogLabelName + " Employee");
        dialog.setHeaderText(dialogLabelName + " Employee");

        // Add to dialog box
        inputsContainer.getChildren().addAll(
                employeeNameLabel,
                employeeName,
                isManagerLabel,
                isManagerButton,
                imageUrlLabel,
                imageUrl
        );

        dialog.getDialogPane().setContent(inputsContainer);

        // Add buttons to dialog pane
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        // Handle the result
        dialog.setResultConverter(dialogButton -> {
            // TODO: add validation
            if (dialogButton == ButtonType.OK) {
                return new ButtonType(
                        employeeName.getText() + "," +
                                isManagerButton.isSelected() + ',' +
                                imageUrl.getText()
                );
            }
            return null;
        });

        Optional<ButtonType> result = dialog.showAndWait();
        result.ifPresent(outputFields -> {
            String[] outputs = outputFields.getText().split(",");
            String name = outputs[0];
            String isManager = outputs[1];
            String url = outputs[2];
            // If no inventory item is passed in, we need to add a new one
            if (employee.isEmpty()) {
                dbDriver.insertEmployee(new Employee(
                        Boolean.parseBoolean(isManager.trim()),
                        name
                ));
            } else {
                // If the inventoryItem is not null, we are updating an existing item
                Employee emp = employee.get();
                emp.isManager = Boolean.parseBoolean(isManager.trim());
                emp.name = name;
                dbDriver.updateEmployee(emp);
            }

            dbSnapshot.refreshEmployeeSnapshot();

            // redraw the grid to reflect the updates
            employeeItemsGridPane.getChildren().clear();
            createEmployeesGrid();
        });
    }

    /**
     * Fetches the 50 most recent orders from the database and display them in the UI
     */
    public void fetchOrderHistory() {
        ordersTable.getItems().clear();

        createOrdersTable();

        Order.setCellValueFactory(cellData ->
                new SimpleObjectProperty(cellData.getValue().orderId.toString())
        );
        Cashier.setCellValueFactory(cellData ->
                new SimpleObjectProperty(cellData.getValue().cashierId.toString())
        );


        // Setup other columns similarly
        Month.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().month.toString())
        );
        Week.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().week.toString())
        );
        Day.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().day.toString())
        );
        Hour.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().hour.toString())
        );
        Price.setCellValueFactory(cellData ->
                new SimpleStringProperty(String.valueOf(cellData.getValue().price))
        );
    }

    /**
     * Creates a summary based on item popularity as well as
     * alert low inventory stock.
     */
    public void fetchSummary() {
        summary.getChildren().clear();
        summary.setStyle("-fx-background-color: white;");
        Text summaryText = new Text("Inventory Items\n");
        summary.getChildren().add(summaryText);

        for (InventoryItem item : dbSnapshot.getInventorySnapshot().values()) {
            if (item.availableStock <= veryLowInventory) {
                summaryText = new Text(item.itemName + " has extremely low stock, restock immediately\n");
                summary.getChildren().add(summaryText);
            } else if (item.availableStock <= lowInventory) {
                summaryText = new Text(item.itemName + " has low stock, restock soon\n");
                summary.getChildren().add(summaryText);
            }
        }
        summaryText = new Text("\nMenu Items\n");
        summary.getChildren().add(summaryText);
    }


    /**
     * Generates Sales Report chart displaying menu item
     * sales in an allotted item frame.
     */
    public void fetchSalesReport() {
        Map<String, Integer> salesReportData = getSalesReportData();
        XYChart.Series newSeries = new XYChart.Series();
        salesChart.getData().clear();

        salesChart.setLegendVisible(false);

        for (Map.Entry<String, Integer> entry : salesReportData.entrySet()) {
            newSeries.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
        }
        salesChart.getData().add(newSeries);
    }

    /**
     * Generates X/Z Report showing the total sales
     * and orders per hour, determines whether
     * to show the entire day or the hours since opening
     * based on which report is being requested.
     *
     * @param wholeDay true if we're fetching the Z report, false for X report
     */
    public void fetchXOrZReport(boolean wholeDay) {
        // Get sales per hour data from DBDriverSingleton
        List<Double> hourlySales;
        List<Double> hourlyOrders;
        BarChart<String, Double> chart;

        if (wholeDay) {
            hourlySales = DBDriverSingleton.getInstance().selectSalesByHourForDay();
            hourlyOrders = DBDriverSingleton.getInstance().selectOrdersByHourForDay();
            chart = zReportBarChart;
        } else {
            hourlyOrders = DBDriverSingleton.getInstance().selectOrdersByHour();
            hourlySales = DBDriverSingleton.getInstance().selectSalesByHour();
            chart = xReportBarChart;
        }

        // Remove any existing data
        chart.getData().clear();

        // Create series to hold data
        XYChart.Series<String, Double> sales = new XYChart.Series<>();
        sales.setName("Sales ($)");

        XYChart.Series<String, Double> orders = new XYChart.Series<>();
        orders.setName("Orders Placed");
        // X-label
        String[] hours = {
                "10 AM", "11 AM", "12 PM",
                "1PM", "2PM", "3PM",
                "4PM", "5PM", "6PM",
                "7PM", "8PM", "9PM"
        };

        // Add data to series
        for (int i = 0; i < hourlySales.size(); i++) {
            sales.getData().add(new XYChart.Data<>(hours[i], hourlySales.get(i)));
            orders.getData().add(new XYChart.Data<>(hours[i], hourlyOrders.get(i)));
        }

        // Add series to bar chart
        chart.getData().add(sales);
        chart.getData().add(orders);

    }

    /**
     * Generates the Product Usage chart with its respective data
     * and updates it when certain parameters are changes.
     */
    public void updateProductUsage() {
        Map<String, Integer> productUsageData = getProductUsageData();
        XYChart.Series newSeries = new XYChart.Series();
        productUsageChart.getData().clear();

        productUsageChart.setLegendVisible(false);

        CategoryAxis xAxis = (CategoryAxis) productUsageChart.getXAxis();
        xAxis.setTickLabelFont(Font.font("Lucida Grande", 10));

        for (String item : productUsageData.keySet()) {
            newSeries.getData().add(new XYChart.Data(item, productUsageData.get(item)));
        }
        productUsageChart.getData().add(newSeries);
    }

    /**
     * Creates the inventory grid under the Inventory tab displaying
     * all inventory items in the database.
     */
    public void createInventoryGrid() {
        int columns = 5; // max columns per row
        int x = 0;
        int y = 0;

        inventoryItemsGridPane.setHgap(10);
        inventoryItemsGridPane.setVgap(50);

        for (InventoryItem item : dbSnapshot.getInventorySnapshot().values()) {

            String itemImg = sampleImg;
            String itemName = item.itemName;
            String itemStock = String.valueOf(item.availableStock);

            // Create a vertical box for image and label
            VBox layout = new VBox(10);
            layout.setAlignment(Pos.BOTTOM_CENTER);

            // Create a button, set background to img
            Button button = new Button();
            button.setMinSize(80, 80);
            button.setStyle("-fx-background-image: url('" + itemImg + "');" +
                    "-fx-background-size: cover;");

            // Handle clicks on each item (updating the item)
            button.setOnMouseClicked(e -> {
                addOrUpdateInventoryItem(Optional.of(item));
            });

            // Create labels
            Label nameLabel = new Label(itemName);
            nameLabel.setTextAlignment(TextAlignment.CENTER);
            nameLabel.setStyle("-fx-padding:5;-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #333; -fx-background-color: white");
            Label itemStockLabel = new Label("Qty: " + itemStock);
            itemStockLabel.setTextAlignment(TextAlignment.CENTER);
            itemStockLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: white;-fx-background-color: black;");

            // Allow the VBox to grow in the GridPane cell
            layout.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE); // Let it grow
            GridPane.setVgrow(layout, Priority.ALWAYS); // Let the VBox grow vertically
            GridPane.setHgrow(layout, Priority.ALWAYS); // Let the VBox grow horizontally

            // Add items to vbox
            layout.getChildren().addAll(button, nameLabel, itemStockLabel);

            // Add vbox to grid
            inventoryItemsGridPane.add(layout, x, y);

            // Update grid position
            x++;
            if (x == columns) {
                x = 0;
                y++;
            }
        }
    }

    /**
     * Creates the menu item grid under the Menu Items tab displaying
     * all menu items in the database.
     */
    public void createMenuItemsGrid() {
        int columns = 5; // max columns per row
        int x = 0;
        int y = 0;

        // Set gaps for GridPane
        menuItemsGridPane.setHgap(10); // Horizontal gap between columns
        menuItemsGridPane.setVgap(50);
        menuItemsGridPane.setAlignment(Pos.CENTER);

        for (MenuItem menuItem : dbSnapshot.getMenuSnapshot().values()) {

            String menuItemName = menuItem.itemName;
            String menuItemImg = sampleImg;

            // Create a vertical box for button and label
            VBox layout = new VBox(10);
            layout.setAlignment(Pos.CENTER);

            // Create a button; set background
            Button button = new Button();
            button.setMinSize(80, 80);
            button.setStyle("-fx-background-image: url('" + menuItemImg + "');" +
                    "-fx-background-size: cover;");

            // Handle the button click
            button.setOnMouseClicked(e -> {
                addOrUpdateMenuItem(Optional.of(menuItem));
            });

            // Create a quantity label
            Label nameLabel = new Label(menuItemName);
            nameLabel.setTextAlignment(TextAlignment.CENTER);
            nameLabel.setStyle("-fx-padding:5;-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #333; -fx-background-color: white");
//            Label menuStockLabel = new Label("Qty: " + menuItemStock);

            // Allow the VBox to grow in the GridPane cell
            layout.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE); // Let it grow
            GridPane.setVgrow(layout, Priority.ALWAYS); // Let the VBox grow vertically
            GridPane.setHgrow(layout, Priority.ALWAYS); // Let the VBox grow horizontally

            // Add items to vbox
            layout.getChildren().addAll(button, nameLabel);

            // Add Vbox to grid
            menuItemsGridPane.add(layout, x, y);

            // Update grid position
            x++;
            if (x == columns) {
                x = 0;
                y++;
            }
        }
    }

    /**
     * Creates the employee grid under the Inventory tab displaying
     * all inventory items in the database.
     */
    public void createEmployeesGrid() {
        int columns = 5; // Max number of columns in row
        int x = 0;
        int y = 0;

        // Set gaps for GridPane
        employeeItemsGridPane.setHgap(10); // Horizontal gap between columns
        employeeItemsGridPane.setVgap(50);
        employeeItemsGridPane.setAlignment(Pos.CENTER);

        for (Employee employee : dbSnapshot.getEmployeeSnapshot().values()) {
            String employeeName = employee.name;
            String employeeImage = sampleImg;
            String employeePosition = employee.isManager ? "Manager" : "Cashier";

            // Create a vbox to store items
            VBox layout = new VBox(10);
            layout.setAlignment(Pos.CENTER);

            // Create a button; set background
            Button button = new Button();
            button.setMinSize(80, 80);
            button.setStyle("-fx-background-image: url('" + employeeImage + "');" +
                    "-fx-background-size: cover;");

            // Handle the button click
            button.setOnMouseClicked(e -> {
                addOrUpdateEmployee(Optional.of(employee));
            });

            // Create name and position labels
            Label employeeNameLabel = new Label(employeeName);
            employeeNameLabel.setTextAlignment(TextAlignment.CENTER);
            employeeNameLabel.setStyle("-fx-padding:5;-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #333; -fx-background-color: white");
            Label employeePositionLabel = new Label(employeePosition);
            employeePositionLabel.setTextAlignment(TextAlignment.CENTER);
            employeePositionLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: white;-fx-background-color: black;");

            // Allow the VBox to grow in the GridPane cell
            layout.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE); // Let it grow
            GridPane.setVgrow(layout, Priority.ALWAYS); // Let the VBox grow vertically
            GridPane.setHgrow(layout, Priority.ALWAYS); // Let the VBox grow horizontally

            // Add items to vbox
            layout.getChildren().addAll(button, employeeNameLabel, employeePositionLabel);

            // Add vbox to grid pane
            employeeItemsGridPane.add(layout, x, y);

            // Update grid position
            x++;
            if (x == columns) {
                x = 0;
                y++;
            }
        }
    }

    /**
     * Retrieves the most recent 50 orders placed.
     */
    public void createOrdersTable() {
        ObservableList<Order> orderList = FXCollections.observableArrayList();
        orderList.addAll(dbSnapshot.getOrderSnapshot().values());

        ordersTable.setItems(orderList);
    }

    /**
     * Initializes the start and end dates for the Sales Report.
     */
    public void initSalesReportChart() {
        // Initialize date pickers to have the previous week as the default time period
        startDatePickerSalesReport.setValue(LocalDate.now().minusWeeks(1));
        endDatePickerSalesReport.setValue(LocalDate.now());
    }

    /**
     * Initializes the start and end dates for the Product Usage.
     */
    public void createProductUsageChart() {
        startDatePickerProductUsage.setValue(LocalDate.now().minusWeeks(1));
        endDatePickerProductUsage.setValue(LocalDate.now());
    }

    /*
    Sales Report
    Frontend: start/end date
    dictionary menuItem : sales
    Backend: menu item sales
     */

    /**
     * Retrieves Sales Report data from the database.
     */
    private Map<String, Integer> getSalesReportData() {
        int startDateMonth = startDatePickerSalesReport.getValue().getMonthValue();
        int startDateDay = startDatePickerSalesReport.getValue().getDayOfMonth();
        int endDateMonth = endDatePickerSalesReport.getValue().getMonthValue();
        int endDateDay = endDatePickerSalesReport.getValue().getDayOfMonth();

        return dbDriver.selectSalesReport(startDateMonth, endDateMonth, startDateDay, endDateDay);
    }

    /**
     * Retrieves Product Usage data from the database.
     */
    private Map<String, Integer> getProductUsageData() {
        int startDateMonth = startDatePickerProductUsage.getValue().getMonthValue();
        int startDateDay = startDatePickerProductUsage.getValue().getDayOfMonth();
        int endDateMonth = endDatePickerProductUsage.getValue().getMonthValue();
        int endDateDay = endDatePickerProductUsage.getValue().getDayOfMonth();

        return dbDriver.selectProductUsage(startDateMonth, endDateMonth, startDateDay, endDateDay);
    }
}