package Controllers;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;

import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class RegisterController implements Initializable {

    @FXML
    private TextField userEmail;

    @FXML
    private TextField userPassword;

    @FXML
    private TextField userName;

    private int idAccount;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    @FXML
    private void backButtonAction(ActionEvent event) {
        SceneChanger.changeScene(event, "../Views/Login.fxml", "Login Page");
    }

    @FXML
    private void registerButton(ActionEvent event) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection(
                    "jdbc:mysql://den1.mysql6.gear.host/hkrmarketplace", "hkrmarketplace", "Ez0ezh-~e3pf");
            PreparedStatement stmt = con.prepareStatement("INSERT INTO `hkrmarketplace`.`account` (`idAccount`, `Username`, `Password`, `Email`, `Admin`) VALUES (?, ?, ?, ?, ?);\n");
            PreparedStatement count = con.prepareStatement("select count(idAccount) from account;");
            ResultSet rs = count.executeQuery();
            while (rs.next()) { //Finds the amount of idAccount since its PK
                int idAccount = rs.getInt(1);
                setIdAccount(idAccount);
            }
            stmt.setInt(1, getIdAccount() + 1); //Gets the amount of idAccount and adds one
            stmt.setString(2, userName.getText());
            stmt.setString(3, userPassword.getText());
            stmt.setString(4, userEmail.getText());
            stmt.setBoolean(5, false);
            stmt.executeUpdate();
            con.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public int getIdAccount() {
        return idAccount;
    }

    public void setIdAccount(int idAccount) {
        this.idAccount = idAccount;
    }
}
