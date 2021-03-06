package Controllers;

import Database.DBHandler;
import Models.Account;
import Models.MessageHandler;
import Models.SceneChanger;
import Models.Singleton;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.util.Objects;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class EditAccountController implements Initializable {
    private double x, y;
    private boolean changeDone;
    @FXML
    private Button closeButton;
    @FXML
    private TextField oldNameField, oldPasswordField, oldEmailField, oldAdminField,
            newNameField, newPasswordField, newEmailField, accountUploadField;
    @FXML
    private ImageView oldImageView;
    @FXML
    private CheckBox trueBox, falseBox;
    private File newImage;
    private Account accountToEdit;
    private Account newAccount;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        displayOldAccount();
        fillNewAccountFields();
        newImage = accountToEdit.getImageFile();
    }

    private void displayOldAccount() {
        accountToEdit = (Account) Singleton.getInstance().getObjectToEdit();
        oldNameField.setText(accountToEdit.getUserName());
        oldPasswordField.setText(accountToEdit.getPassword());
        oldEmailField.setText(accountToEdit.getEmail());
        if (accountToEdit.isAdmin()) {
            oldAdminField.setText("True");
            trueBox.setSelected(true);
            falseBox.setSelected(false);
        } else {
            oldAdminField.setText("False");
            falseBox.setSelected(true);
            trueBox.setSelected(false);
        }
        if (accountToEdit.getImageFile() != null) {
            try {
                oldImageView.setImage(accountToEdit.getImage());
            } catch (NullPointerException e) {
                MessageHandler.getErrorAlert("Error", "Error", "Something went wrong when trying to display the picture.").showAndWait();
            }
        }
    }

    private void fillNewAccountFields() {
        newImage = accountToEdit.getImageFile();
        newNameField.setText(oldNameField.getText());
        newPasswordField.setText(oldPasswordField.getText());
        newEmailField.setText(oldEmailField.getText());
        if (accountToEdit.isAdmin()) {
            trueBox.setSelected(true);
            falseBox.setSelected(false);
        } else {
            trueBox.setSelected(false);
            falseBox.setSelected(true);
        }
        if (newImage != null) {
            accountUploadField.setText(newImage.getPath());
        } else {
            accountUploadField.setText("Null");
        }
    }

    @FXML
    private void handleCheckBoxes(ActionEvent event) {
        if (event.getSource() == trueBox) {
            trueBox.setSelected(true);
            falseBox.setSelected(false);
        } else if (event.getSource() == falseBox) {
            falseBox.setSelected(true);
            trueBox.setSelected(false);
        }
    }

    private boolean getSelectedCheckBox() {
        return trueBox.isSelected();
    }

    @FXML
    private void handleUploadButton() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("IMAGE FILES", "*.jpg", "*.png", "*.gif"));
        newImage = fileChooser.showOpenDialog(null);
        if (newImage != null) {
            accountUploadField.setText(newImage.getPath());
        } else {
            MessageHandler.getErrorAlert("Error", "Error", "File does not exist!").showAndWait();
            accountUploadField.setText("Null");
        }
    }

    @FXML
    private void handleResetButton() {
        newImage = null;
        accountUploadField.setText("Null");
    }

    @FXML
    private void handleUpdateButton() {
        updateAccount();
    }

    private void updateAccount() {
        newAccount = new Account(accountToEdit.getUserName(), accountToEdit.getPassword(), accountToEdit.getEmail(), accountToEdit.isAdmin(), accountToEdit.getImageFile());
        boolean validationChecker = true;
        changeDone = false;
        if (!newNameField.getText().equals(oldNameField.getText())) {
            validationChecker = validateNewAccountName();
        }
        if (validationChecker && !oldPasswordField.getText().equals(newPasswordField.getText())) {
            validationChecker = validateNewPassword();
        }
        if (validationChecker && !oldEmailField.getText().equals(newEmailField.getText())) {
            validationChecker = validateNewEmail();
        }
        if (validationChecker && !Objects.equals(accountToEdit.isAdmin(), getSelectedCheckBox())) {
            validationChecker = validateNewAdminStatus();
        }
        if (validationChecker && !Objects.equals(accountToEdit.getImageFile(), newImage)) {
            validationChecker = validateNewImage();
        }

        if (validationChecker ) {    // If all checks performed are passed the values are changed.
            if (changeDone) {
                newAccount.setUserName(newNameField.getText());
                newAccount.setPassword(newPasswordField.getText());
                newAccount.setEmail(newEmailField.getText());
                if (getSelectedCheckBox()) {
                    newAccount.setAdmin(true);
                } else {
                    newAccount.setAdmin(false);
                }
                newAccount.setImageFile(newImage);
                DBHandler.updateAccountInformation(accountToEdit, newAccount);
                SceneChanger.changeScene("../Views/Administration.fxml");
                handleClosingButton();
            } else {
                MessageHandler.getErrorAlert("Error", "Error", "No changes have been made to the account.").showAndWait();
            }
        }
    }

    private boolean validateNewAccountName() {
        if (!newNameField.getText().equals("") && newNameField.getText().length() > 4) {
            changeDone = true;
            return true;
        } else {
            MessageHandler.getErrorAlert("Error", "Error", "New name has to be atleast 5 letters").showAndWait();
            return false;
        }
    }

    private boolean validateNewPassword() {
        if (!newPasswordField.getText().equals("") && newPasswordField.getText().length() > 2) {
            changeDone = true;
            return true;
        } else {
            MessageHandler.getErrorAlert("Error", "Error", "New password doesn't meet requirements.").showAndWait();
            return false;
        }
    }

    private boolean validateNewEmail() {
        if (Account.validateEmail(newEmailField.getText()) && !DBHandler.seeIfEmailAlreadyRegistered(newEmailField.getText())) {
            changeDone = true;
            return true;
        } else {
            MessageHandler.getErrorAlert("Error", "Error", "New email doesn't meet requirements.").showAndWait();
            return false;
        }
    }

    private boolean validateNewAdminStatus() {
        changeDone = true;
        return true;
    }

    private boolean validateNewImage() {
      if (!Objects.equals(accountToEdit.getImageFile(), newImage)) {
          changeDone = true;
          return true;
      } else {
          MessageHandler.getErrorAlert("Error", "Error", "Something went wrong with picture.").showAndWait();
          return false;
      }
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
