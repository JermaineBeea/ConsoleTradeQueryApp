package co.za.Main.ConsoleApplication;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.math.BigDecimal;

public class ConsoleJavaFXApplication extends Application {
    
    private TableView<TradeVariable> table;
    private ConsoleDatabase db;
    private TextField spreadField, rateKAField, ratePNField;
    private CheckBox marketRateCheckbox;
    
    public static void main(String[] args) {
        launch(args);
    }
    
    @Override
    public void start(Stage primaryStage) {
        // Initialize database
        BigDecimal spread = new BigDecimal("0.01");
        BigDecimal rateKA = new BigDecimal("17.7055");
        BigDecimal ratePN = new BigDecimal("1.0");
        
        db = new ConsoleDatabase(spread, rateKA, ratePN);
        
        // Create main layout
        BorderPane root = new BorderPane();
        
        // Top panel - controls
        VBox topPanel = createControlPanel();
        root.setTop(topPanel);
        
        // Center - table
        table = createTable();
        root.setCenter(table);
        
        // Bottom - buttons
        HBox buttonPanel = createButtonPanel();
        root.setBottom(buttonPanel);
        
        // Load initial data
        loadDataIntoTable();
        
        // Create scene and show
        Scene scene = new Scene(root, 800, 500);
        primaryStage.setTitle("Trade Console Application");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
    private VBox createControlPanel() {
        VBox panel = new VBox(10);
        panel.setPadding(new Insets(10));
        panel.setStyle("-fx-border-color: gray; -fx-border-width: 1;");
        
        Label title = new Label("Trading Parameters");
        title.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        
        HBox controls = new HBox(15);
        controls.setAlignment(Pos.CENTER_LEFT);
        
        controls.getChildren().addAll(
            new Label("Spread:"), 
            spreadField = new TextField("0.01"),
            new Label("Rate KA:"), 
            rateKAField = new TextField("17.7055"),
            new Label("Rate PN:"), 
            ratePNField = new TextField("1.0"),
            marketRateCheckbox = new CheckBox("Based on Market Rate")
        );
        
        // Set field widths
        spreadField.setPrefWidth(80);
        rateKAField.setPrefWidth(80);
        ratePNField.setPrefWidth(80);
        
        panel.getChildren().addAll(title, controls);
        return panel;
    }
    
    private TableView<TradeVariable> createTable() {
        TableView<TradeVariable> table = new TableView<>();
        
        // Create columns
        TableColumn<TradeVariable, String> variableCol = new TableColumn<>("Variable");
        variableCol.setCellValueFactory(new PropertyValueFactory<>("variable"));
        variableCol.setPrefWidth(120);
        
        TableColumn<TradeVariable, String> minCol = new TableColumn<>("Minimum");
        minCol.setCellValueFactory(new PropertyValueFactory<>("minimum"));
        minCol.setCellFactory(TextFieldTableCell.forTableColumn());
        minCol.setPrefWidth(120);
        minCol.setOnEditCommit(e -> {
            e.getTableView().getItems().get(e.getTablePosition().getRow()).setMinimum(e.getNewValue());
            updateDatabaseValue(e.getRowValue().getVariable(), "minimum", e.getNewValue());
        });
        
        TableColumn<TradeVariable, String> maxCol = new TableColumn<>("Maximum");
        maxCol.setCellValueFactory(new PropertyValueFactory<>("maximum"));
        maxCol.setCellFactory(TextFieldTableCell.forTableColumn());
        maxCol.setPrefWidth(120);
        maxCol.setOnEditCommit(e -> {
            e.getTableView().getItems().get(e.getTablePosition().getRow()).setMaximum(e.getNewValue());
            updateDatabaseValue(e.getRowValue().getVariable(), "maximum", e.getNewValue());
        });
        
        TableColumn<TradeVariable, String> returnMinCol = new TableColumn<>("Return Min");
        returnMinCol.setCellValueFactory(new PropertyValueFactory<>("returnMin"));
        returnMinCol.setPrefWidth(120);
        returnMinCol.setEditable(false);
        
        TableColumn<TradeVariable, String> returnMaxCol = new TableColumn<>("Return Max");
        returnMaxCol.setCellValueFactory(new PropertyValueFactory<>("returnMax"));
        returnMaxCol.setPrefWidth(120);
        returnMaxCol.setEditable(false);
        
        table.getColumns().addAll(variableCol, minCol, maxCol, returnMinCol, returnMaxCol);
        table.setEditable(true);
        
        return table;
    }
    
    private HBox createButtonPanel() {
        HBox panel = new HBox(10);
        panel.setPadding(new Insets(10));
        panel.setAlignment(Pos.CENTER);
        
        Button calculateBtn = new Button("Run Calculations");
        calculateBtn.setOnAction(e -> runCalculations());
        
        Button examplesBtn = new Button("Load Examples");
        examplesBtn.setOnAction(e -> loadExampleData());
        
        Button resetBtn = new Button("Reset to Zero");
        resetBtn.setOnAction(e -> resetData());
        
        Button exportBtn = new Button("Export Data");
        exportBtn.setOnAction(e -> exportData());
        
        panel.getChildren().addAll(calculateBtn, examplesBtn, resetBtn, exportBtn);
        return panel;
    }
    
    private void loadDataIntoTable() {
        try {
            ObservableList<TradeVariable> data = FXCollections.observableArrayList();
            String[] variables = {"tradeprofit", "profitfactor", "tradeamount", "buyvariable", "sellvariable"};
            
            for (String variable : variables) {
                BigDecimal min = db.getValueFromColumn(variable, "minimum");
                BigDecimal max = db.getValueFromColumn(variable, "maximum");
                BigDecimal returnMin = db.getValueFromColumn(variable, "returnmin");
                BigDecimal returnMax = db.getValueFromColumn(variable, "returnmax");
                
                data.add(new TradeVariable(
                    variable,
                    min.toPlainString(),
                    max.toPlainString(),
                    returnMin.toPlainString(),
                    returnMax.toPlainString()
                ));
            }
            
            table.setItems(data);
            
        } catch (Exception e) {
            showError("Error loading data", e.getMessage());
        }
    }
    
    private void updateDatabaseValue(String variable, String column, String value) {
        try {
            BigDecimal bdValue = new BigDecimal(value);
            db.updateValue(variable, column, bdValue);
        } catch (Exception e) {
            showError("Error updating value", e.getMessage());
            loadDataIntoTable(); // Reload to revert invalid changes
        }
    }
    
    private void runCalculations() {
        try {
            // Update database parameters
            BigDecimal spread = new BigDecimal(spreadField.getText());
            BigDecimal rateKA = new BigDecimal(rateKAField.getText());
            BigDecimal ratePN = new BigDecimal(ratePNField.getText());
            boolean basedOnMarketRate = marketRateCheckbox.isSelected();
            
            // Recreate database with new parameters
            db.close();
            db = new ConsoleDatabase(spread, rateKA, ratePN);
            db.setBasedOnMarketRate(basedOnMarketRate);
            
            // Update database with current table values
            for (TradeVariable item : table.getItems()) {
                db.updateValue(item.getVariable(), "minimum", new BigDecimal(item.getMinimum()));
                db.updateValue(item.getVariable(), "maximum", new BigDecimal(item.getMaximum()));
            }
            
            // Run calculations
            db.populateQueryVariables();
            
            // Reload table with results
            loadDataIntoTable();
            
            showInfo("Success", "Calculations completed successfully!");
            
        } catch (Exception e) {
            showError("Calculation Error", e.getMessage());
        }
    }
    
    private void loadExampleData() {
        try {
            // Example data
            db.updateValue("tradeprofit", "minimum", new BigDecimal("-88.000000000"));
            db.updateValue("tradeprofit", "maximum", new BigDecimal("-88.000000000"));
            db.updateValue("profitfactor", "minimum", new BigDecimal("-0.000497021"));
            db.updateValue("profitfactor", "maximum", new BigDecimal("-0.000497021"));
            db.updateValue("tradeamount", "minimum", new BigDecimal("10000.00"));
            db.updateValue("tradeamount", "maximum", new BigDecimal("10000.00"));
            db.updateValue("buyvariable", "minimum", new BigDecimal("17.7055"));
            db.updateValue("buyvariable", "maximum", new BigDecimal("17.7055"));
            db.updateValue("sellvariable", "minimum", new BigDecimal("17.6967"));
            db.updateValue("sellvariable", "maximum", new BigDecimal("17.6967"));
            
            loadDataIntoTable();
            showInfo("Success", "Example data loaded!");
            
        } catch (Exception e) {
            showError("Error", "Error loading examples: " + e.getMessage());
        }
    }
    
    private void resetData() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Reset Confirmation");
        alert.setHeaderText("Are you sure you want to reset all values to zero?");
        
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    String[] variables = {"tradeprofit", "profitfactor", "tradeamount", "buyvariable", "sellvariable"};
                    
                    for (String variable : variables) {
                        db.updateValue(variable, "minimum", BigDecimal.ZERO);
                        db.updateValue(variable, "maximum", BigDecimal.ZERO);
                        db.updateValue(variable, "returnmin", BigDecimal.ZERO);
                        db.updateValue(variable, "returnmax", BigDecimal.ZERO);
                    }
                    
                    loadDataIntoTable();
                    showInfo("Success", "All values reset to zero!");
                    
                } catch (Exception e) {
                    showError("Error", "Error resetting data: " + e.getMessage());
                }
            }
        });
    }
    
    private void exportData() {
        try {
            db.exportToSQL();
            db.exportToCSV();
            showInfo("Success", "Data exported successfully!");
        } catch (Exception e) {
            showError("Error", "Error exporting data: " + e.getMessage());
        }
    }
    
    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}