package wholesale_business.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import wholesale_business.AppInitializer;

import java.io.IOException;

public class LoginFormController {
    public JFXTextField txtUsername;
    public JFXPasswordField txtPassword;
    public StackPane stackPane;
    public AnchorPane rootPane;
    public ImageView bgImage;
    public JFXButton btnLogin1;
    public JFXButton btnCancel1;


    public void btnCancelClick(ActionEvent event) {
        Platform.exit();
        System.exit(0);
    }

    public void btnLoginClick(ActionEvent event) {
        if (isAuthorized().equals("admin")) {
            openAdminDashboard();
        } else if (isAuthorized().equals("user")) {
            openUserDashboard();
        } else {
            new Alert(Alert.AlertType.WARNING, "You have entered an invalid username or password. Please try again.").show();
        }
    }

    private String isAuthorized() {
        if (txtUsername.getText().equals("admin") && txtPassword.getText().equals("1234")) {
            return "admin";
        } else if (txtUsername.getText().equals("user") && txtPassword.getText().equals("2345")) {
            return "user";
        } else {
            return "invalid";
        }
    }

    private void openDashboards(String location) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(AppInitializer.class.getResource(location));
            loader.load();
            Parent parent = loader.getRoot();
            Scene scene = new Scene(parent);
            Stage dashboardStage = new Stage();
            dashboardStage.setMinHeight(626.0);
            dashboardStage.setMinWidth(926.0);
            dashboardStage.setScene(scene);
            dashboardStage.setMaximized(true);
            dashboardStage.show();
            Stage stage = (Stage) rootPane.getScene().getWindow();
            stage.close();
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    private void openAdminDashboard() {
        openDashboards("../wholesale_business/view/AdminMenuForm.fxml");
    }

    private void openUserDashboard() {
        openDashboards("../wholesale_business/view/UserMenuForm.fxml");
    }
}

