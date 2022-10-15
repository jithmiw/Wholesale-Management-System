package wholesale_business.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.transitions.JFXFillTransition;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import wholesale_business.AppInitializer;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class AdminMenuFormController {
    public StackPane stackRootPane;
    public AnchorPane anchorPane;
    public AnchorPane centerPane;
    public JFXButton btnDashboard;
    public Label lblUser;
    public Label lblDate;
    public Label lblTime;
    public JFXButton btnPlaceOrder;
    public JFXButton btnManageOrders;
    public JFXButton btnManageItems;
    public JFXButton btnManageCustomers;
    public ImageView imgUser;
    private JFXButton activeMenuButton;
    private JFXFillTransition ft;

    public void initialize() {
        imgUser.setImage(new Image(AppInitializer.class.getResource("/wholesale_business/view/asset/img/account_circle_black.jpg").toString()));
        Timeline clock = new Timeline(new KeyFrame(Duration.ZERO, e -> {
            DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("EEEE, MMMM dd, yyyy");
            DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("hh:mm:ss a");
            lblDate.setText(LocalDateTime.now().format(dateFormat));
            lblTime.setText(LocalDateTime.now().format(timeFormat));
        }), new KeyFrame(Duration.seconds(1)));
        clock.setCycleCount(Animation.INDEFINITE);
        clock.play();

        ft = new JFXFillTransition();
        activeMenuButton = btnDashboard;
        btnDashboard.fire();
    }

    public void menuButtonMouseEntered(MouseEvent event) {
        if (event.getSource() != activeMenuButton) {
            ft = new JFXFillTransition();
            ft.setRegion((JFXButton) event.getSource());
            ft.setDuration(new Duration(500));
            ft.setFromValue(Color.WHITE);
            ft.setToValue(Color.rgb(151, 136, 134));
            ft.play();
        }
    }

    public void menuButtonMouseExited(MouseEvent event) {
        if (event.getSource() != activeMenuButton) {
            ft = new JFXFillTransition();
            ft.setRegion((JFXButton) event.getSource());
            ft.setDuration(new Duration(500));
            ft.setFromValue(Color.rgb(151, 136, 134));
            ft.setToValue(Color.WHITE);
            ft.play();
        }
    }

    public void selectMenu(String fxmlName, ActionEvent event, String controllerClass) {
        if (ft.getStatus() == Animation.Status.RUNNING)
            ft.stop();
        activeMenuButton.getStyleClass().remove("menuButtonActive");
        ((JFXButton) event.getSource()).getStyleClass().add("menuButtonActive");
        activeMenuButton = ((JFXButton) event.getSource());
        FXMLLoader fxmlLoader = new FXMLLoader();
        try {
            fxmlLoader.load(AppInitializer.class.getResource(fxmlName).openStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (controllerClass.equalsIgnoreCase("Report"))
            ((ReportFormController) fxmlLoader.getController()).setPane(stackRootPane, anchorPane);
        else if (controllerClass.equalsIgnoreCase("PlaceOrder"))
            ((PlaceOrderFormController) fxmlLoader.getController()).setPane(stackRootPane, anchorPane);
        else if (controllerClass.equalsIgnoreCase("ManageOrder"))
            ((ManageOrderFormController) fxmlLoader.getController()).setPane(stackRootPane, anchorPane);
        else if (controllerClass.equalsIgnoreCase("Item"))
            ((ItemFormController) fxmlLoader.getController()).setPane(stackRootPane, anchorPane);
        else if (controllerClass.equalsIgnoreCase("Customer"))
            ((CustomerFormController) fxmlLoader.getController()).setPane(stackRootPane, anchorPane);
        AnchorPane root = fxmlLoader.getRoot();
        centerPane.getChildren().clear();
        centerPane.getChildren().add(root);

        AnchorPane.setTopAnchor(root, 0.0);
        AnchorPane.setRightAnchor(root, 0.0);
        AnchorPane.setBottomAnchor(root, 0.0);
        AnchorPane.setLeftAnchor(root, 0.0);
    }

    public void btnDashboardClick(ActionEvent event) {
        selectMenu("../wholesale_business/view/ReportForm.fxml", event, "Report");
    }

    public void btnPlaceOrderClick(ActionEvent event) {
        selectMenu("../wholesale_business/view/PlaceOrderForm.fxml", event, "PlaceOrder");
    }

    public void btnManageOrdersClick(ActionEvent event) {
        selectMenu("../wholesale_business/view/ManageOrderForm.fxml", event, "ManageOrder");
    }

    public void btnManageItemsClick(ActionEvent event) {
        selectMenu("../wholesale_business/view/ItemForm.fxml", event, "Item");
    }

    public void btnManageCustomersClick(ActionEvent event) {
        selectMenu("../wholesale_business/view/CustomerForm.fxml", event, "Customer");
    }
}
