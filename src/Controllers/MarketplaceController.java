package Controllers;

import Database.DBHandler;
import Models.Item;
import Models.SceneChanger;
import Models.Singleton;
import Models.ToolTipHandler;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.ResourceBundle;

public class MarketplaceController implements Initializable {

    private ObservableList<Item> items; // List contains all sales used throughout the scene.
    private double x, y;
    @FXML
    private ImageView imageView, imageView1, adminview;

    @FXML
    private Button adminButton, settingsButton, closingButton, minimizeButton, sellButton, logOutButton, monitoringButton;

    @FXML
    private TableView<Item> table;

    @FXML
    private TableColumn<Item, ImageView> pic;

    @FXML
    private TableColumn<Item, String> title;

    @FXML
    private TableColumn<Item, Double> price;

    @FXML
    private TableColumn<Item,String> category;

    @FXML
    private TextField filterField;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // imageView.setImage(new Image("https://usercontent1.hubstatic.com/11310434_f520.jpg"));
        // imageView1.setImage(new Image("https://www.kattkompaniet.nu/images/5.63.1606161417/kattleksaker-fatcat.jpeg"));
        // System.out.println("The user who just logged in is: " + Singleton.getInstance().getLoggedInAccount()); // This is here for testing purposes!

        initializeTable();
        handleClickOnItem();
        search();

        if (Singleton.getInstance().getLoggedInAccount().isAdmin()) {
            adminview.setVisible(true);
            adminButton.setDisable(false);
        }

        handleCursor();
        handleToolTip();
    }

    private void handleCursor() {
        adminButton.setCursor(Cursor.HAND);
        settingsButton.setCursor(Cursor.HAND);
        sellButton.setCursor(Cursor.HAND);
        logOutButton.setCursor(Cursor.HAND);
        monitoringButton.setCursor(Cursor.HAND);
    }

    private void handleToolTip() {
        ToolTipHandler.getToolTipSettings(settingsButton);
        ToolTipHandler.getToolTipAdmin(adminButton);
        ToolTipHandler.getToolTipCloseButton(closingButton);
        ToolTipHandler.getToolTipMinimizeButton(minimizeButton);
        ToolTipHandler.getToolTipSellScene(sellButton);
    }

    @FXML
    private void handleLogOutButton() {
        SceneChanger.changeScene("../Views/Login.fxml");
    }

    @FXML
    public void sellButton() {
        SceneChanger.changeScene("../Views/Sell.fxml");
    }


    @FXML
    private void handleClosingButton() {
        Platform.exit();
    }

    @FXML
    private void handleMinimizeButton(ActionEvent event) {
        ((Stage) ((Node) event.getSource()).getScene().getWindow()).setIconified(true);
    }

    @FXML
    void windowDragged(MouseEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setX(event.getScreenX() - x);
        stage.setY(event.getScreenY() - y);
    }

    @FXML
    void windowPressed(MouseEvent event) {
        x = event.getSceneX();
        y = event.getSceneY();
    }

    @FXML
    private void handleSettingsButton() {
        SceneChanger.changeScene("../Views/Settings.fxml");
    }

    @FXML
    private void handleAdminButton() {
        SceneChanger.changeScene("../Views/Administration.fxml");
    }

    @FXML
    private void handleMonitoringButton() {
        SceneChanger.changeScene("../Views/MonitoringSales.fxml");
    }

    private void handleClickOnItem() {
        table.setOnMouseClicked((MouseEvent event) -> {
            if (event.getClickCount() > 1) {
                if (table.getSelectionModel().getSelectedItem() != null) {
                    Singleton.getInstance().setItem(table.getSelectionModel().getSelectedItem());
                    SceneChanger.changeScene("../Views/ItemView.fxml");
                }
            }
        });
    }

    private void initializeTable() {
        fetchItemsFromDB();

        for (Item item : items) {
            if (item.getImage() != null) {
                item.setPic(item.getImage());   // Här sätter jag varje objekts imageview till dens aktuella bild
            }
        }
        category.setCellValueFactory(new PropertyValueFactory<>("category"));
        pic.setCellValueFactory(new PropertyValueFactory<>("pic"));
        title.setCellValueFactory(new PropertyValueFactory<>("name"));
        price.setCellValueFactory(new PropertyValueFactory<>("price"));
        table.setItems(items);
        table.sort();
    }

    private void fetchItemsFromDB() {
        items = DBHandler.retrieveAllSales();
        if (items != null) {
            Collections.reverse(items);
        }
    }

    private void search(){

        FilteredList<Item> filteredData = new FilteredList<>(items, p -> true);
        filterField.textProperty().addListener((observable, oldValue, newValue) -> {

            filteredData.setPredicate(item -> {
                // If filter text is empty, display all persons.
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                // Compare first name and last name of every person with filter text.
                String lowerCaseFilter = newValue.toLowerCase();

                if (item.getName().toLowerCase().contains(lowerCaseFilter)) {
                    return true; // Filter matches first name.
                } else if (item.getCategory().toLowerCase().contains(lowerCaseFilter)) {
                    return true; // Filter matches last name.
                }
                return false; // Does not match.
            });
        });

        SortedList<Item> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(table.comparatorProperty());
        table.setItems(sortedData);

    }


}