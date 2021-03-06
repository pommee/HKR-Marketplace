package Controllers;

import Database.DBHandler;
import Models.Account;
import Models.MessageHandler;
import Models.SceneChanger;
import Models.Singleton;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class InsertAccountController implements Initializable {
    @FXML
    private Button closeButton;
    @FXML
    private CheckBox trueCheckbox, falseCheckbox;
    @FXML
    private TextField usernameField, passwordField, emailField, uploadField;
    @FXML
    private AnchorPane root;
    private double x, y;
    private File imageFile;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        uploadField.setEditable(false);
        Platform.runLater(() -> root.requestFocus());
    }

    @FXML
    private void handleCheckboxes(ActionEvent event) {
        if (event.getSource() == trueCheckbox) {
            trueCheckbox.setSelected(true);
            falseCheckbox.setSelected(false);
        } else if (event.getSource() == falseCheckbox) {
            falseCheckbox.setSelected(true);
            trueCheckbox.setSelected(false);
        }
    }

    @FXML
    private void handleInsertButton() {
        if (!usernameField.getText().equals("") && !passwordField.getText().equals("") && !emailField.getText().equals("")) {
            if (Account.validateEmail(emailField.getText()) && !DBHandler.seeIfEmailAlreadyRegistered(emailField.getText())) {
                Account account = new Account(usernameField.getText(), passwordField.getText(), emailField.getText(), getSelectedCheckbox(), imageFile);
                DBHandler.insertAccountIntoDB(account);
                MessageHandler.getInformationAlert("Success", "Success", "The account was successfully added to the database!").showAndWait();
                Singleton.getInstance().setLastInsertedObject("Account");
                SceneChanger.changeScene("../Views/Administration.fxml"); // Might replace by transfering the added account to tableview later
                handleClosingButton();
            } else {
                MessageHandler.getErrorAlert("Error", "Error", "Email is either already in use or doesn't meet requirements.").showAndWait();
            }
        } else {
            MessageHandler.getErrorAlert("Error", "Error", "You have to enter name, password and email!").showAndWait();
        }
    }

    private boolean getSelectedCheckbox() {
        return !falseCheckbox.isSelected();
    }

    @FXML
    private void handleUploadButton() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("IMAGE FILES", "*.jpg", "*.png", "*.gif"));
        imageFile = fileChooser.showOpenDialog(null);
        if (imageFile != null) {
            uploadField.setText(imageFile.getPath());
        } else {
            MessageHandler.getErrorAlert("Error", "Error", "File does not exist!").showAndWait();
        }
    }

    @FXML
    private void handleResetButton() {
        imageFile = null;
        uploadField.setText("");
    }

    @FXML
    private void handleClosingButton() {
        Stage popupStage = (Stage) closeButton.getScene().getWindow();
        popupStage.close();
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
}
