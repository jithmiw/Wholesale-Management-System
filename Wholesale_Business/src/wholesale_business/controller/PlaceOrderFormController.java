package wholesale_business.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.view.JasperViewer;
import wholesale_business.bo.BOFactory;
import wholesale_business.bo.custom.PlaceOrderBO;
import wholesale_business.dto.CustomerDTO;
import wholesale_business.dto.ItemDTO;
import wholesale_business.dto.OrderDTO;
import wholesale_business.dto.OrderDetailDTO;
import wholesale_business.view.tdm.OrderDetailTM;

import java.awt.*;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class PlaceOrderFormController {
    public Label lblOrderID;
    public JFXComboBox<String> cmbCusID;
    public JFXComboBox<String> cmbItemCode;
    public JFXButton btnAddToList;
    public JFXTextField txtCusName;
    public JFXTextField txtItemDesc;
    public JFXTextField txtQtyOnHand;
    public JFXTextField txtCusAddress;
    public JFXTextField txtDiscount;
    public JFXTextField txtQuantity;
    public JFXTextField txtUnitPrice;
    public TextField txtTotal;
    public TableView<OrderDetailTM> tblOrderDetail;
    public JFXButton btnCancel;
    public JFXButton btnConfirmOrder;
    public TextField txtCash;
    public TextField txtChange;
    public JFXButton btnPayAmount;
    public Label txtTotalPrice;
    public Label txtDiscountAmount;
    PlaceOrderBO placeOrderBO = (PlaceOrderBO) BOFactory.getBoFactory().getBO(BOFactory.BOTypes.PLACE_ORDER);
    private StackPane stackPane;
    private AnchorPane rootPane;
    private String orderId;

    public void setPane(StackPane stackPane, AnchorPane rootPane) {
        this.stackPane = stackPane;
        this.rootPane = rootPane;
    }

    public void initialize() {
        tblOrderDetail.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("itemCode"));
        tblOrderDetail.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("description"));
        tblOrderDetail.getColumns().get(2).setCellValueFactory(new PropertyValueFactory<>("orderQty"));
        tblOrderDetail.getColumns().get(3).setCellValueFactory(new PropertyValueFactory<>("unitPrice"));
        tblOrderDetail.getColumns().get(4).setCellValueFactory(new PropertyValueFactory<>("discount"));
        tblOrderDetail.getColumns().get(5).setCellValueFactory(new PropertyValueFactory<>("totalAmount"));
        tblOrderDetail.getColumns().get(6).setCellValueFactory(new PropertyValueFactory<>("totalAmountPayable"));
        TableColumn<OrderDetailTM, Button> lastCol = (TableColumn<OrderDetailTM, Button>) tblOrderDetail.getColumns().get(7);
        lastCol.setCellValueFactory(param -> {
            Button btnDelete = new Button("Delete");
            btnDelete.setStyle("-fx-text-fill: #000000;");
            btnDelete.setOnAction(event -> {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to remove this item from the list?", ButtonType.YES, ButtonType.NO);
                Optional<ButtonType> buttonType = alert.showAndWait();

                if (buttonType.get().equals(ButtonType.YES)) {
                    tblOrderDetail.getItems().remove(param.getValue());
                    tblOrderDetail.getSelectionModel().clearSelection();
                    calculateTotal();
                    enableOrDisablePlaceOrderButton();
                }
            });
            return new ReadOnlyObjectWrapper<>(btnDelete);
        });
        orderId = generateNewOrderId();
        lblOrderID.setText("Order ID: " + orderId);
        btnConfirmOrder.setDisable(true);
        txtCusName.setFocusTraversable(false);
        txtCusName.setEditable(false);
        txtCusAddress.setFocusTraversable(false);
        txtCusAddress.setEditable(false);
        txtItemDesc.setFocusTraversable(false);
        txtItemDesc.setEditable(false);
        txtUnitPrice.setFocusTraversable(false);
        txtUnitPrice.setEditable(false);
        txtQtyOnHand.setFocusTraversable(false);
        txtQtyOnHand.setEditable(false);
        txtDiscount.setOnAction(event -> btnAddToList.fire());
        txtDiscount.setEditable(false);
        txtQuantity.setEditable(false);
        txtQuantity.setOnAction(event -> txtDiscount.requestFocus());
        btnAddToList.setDisable(true);

        cmbCusID.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            enableOrDisablePlaceOrderButton();

            if (newValue != null) {
                try {
                    /*Search Customer*/
                    try {
                        if (!existCustomer(newValue + "")) {
                            new Alert(Alert.AlertType.ERROR, "There is no such customer associated with the id " + newValue + "").show();
                        }

                        CustomerDTO search = placeOrderBO.searchCustomer(newValue + "");
                        txtCusName.setText(search.getCustName());
                        txtCusAddress.setText(search.getCustAddress());

                    } catch (SQLException e) {
                        new Alert(Alert.AlertType.ERROR, "Failed to find the customer " + newValue + "" + e).show();
                    }
                } catch (ClassNotFoundException throwable) {
                    throwable.printStackTrace();
                }
            } else {
                txtCusName.clear();
                txtCusAddress.clear();
            }
        });

        cmbItemCode.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newItemCode) -> {
            txtQuantity.setEditable(newItemCode != null);
            txtDiscount.setEditable(newItemCode != null);
            btnAddToList.setDisable(newItemCode == null);

            if (newItemCode != null) {

                /*Find Item*/
                try {
                    if (!existItem(newItemCode + "")) {
                        new Alert(Alert.AlertType.ERROR, "There is no such item associated with the id " + newItemCode + "").show();
                    }

                    //Search Item
                    ItemDTO item = placeOrderBO.searchItem(newItemCode + "");
                    txtItemDesc.setText(item.getDescription());
                    txtUnitPrice.setText(item.getUnitPrice().setScale(2).toString());

                    txtQuantity.clear();
                    txtDiscount.clear();

                    Optional<OrderDetailTM> optOrderDetail = tblOrderDetail.getItems().stream().filter(detail -> detail.getItemCode().equals(newItemCode)).findFirst();
                    txtQtyOnHand.setText((optOrderDetail.isPresent() ? item.getQtyOnHand() - optOrderDetail.get().getOrderQty() : item.getQtyOnHand()) + "");

                } catch (SQLException | ClassNotFoundException throwable) {
                    throwable.printStackTrace();
                }
            } else {
                txtItemDesc.clear();
                txtQuantity.clear();
                txtQtyOnHand.clear();
                txtUnitPrice.clear();
                txtDiscount.clear();
            }
        });

        tblOrderDetail.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, selectedOrderDetail) -> {

            if (selectedOrderDetail != null) {
                cmbItemCode.setDisable(true);
                cmbItemCode.setValue(selectedOrderDetail.getItemCode());
                btnAddToList.setText("Update");
                txtQtyOnHand.setText(Integer.parseInt(txtQtyOnHand.getText()) + selectedOrderDetail.getOrderQty() + "");
                txtQuantity.setText(selectedOrderDetail.getOrderQty() + "");
                txtDiscount.setText(String.valueOf(selectedOrderDetail.getDiscount().divide(BigDecimal.valueOf(selectedOrderDetail.getOrderQty()))));
            } else {
                btnAddToList.setText("Add to List");
                cmbItemCode.setDisable(false);
                cmbItemCode.getSelectionModel().clearSelection();
                txtQuantity.clear();
                txtDiscount.clear();
            }
        });
        loadAllCustomerIds();
        loadAllItemCodes();
    }

    private boolean existItem(String code) throws SQLException, ClassNotFoundException {
        return placeOrderBO.checkItemIsAvailable(code);
    }

    boolean existCustomer(String id) throws SQLException, ClassNotFoundException {
        return placeOrderBO.checkCustomerIsAvailable(id);
    }

    public String generateNewOrderId() {
        try {
            return placeOrderBO.generateNewOrderID();
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, "Failed to generate a new order id").show();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return "OID001";
    }

    private void loadAllCustomerIds() {
        try {
            ArrayList<CustomerDTO> all = placeOrderBO.getAllCustomers();
            for (CustomerDTO customerDTO : all) {
                cmbCusID.getItems().add(customerDTO.getCustID());
            }
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, "Failed to load customer ids").show();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void loadAllItemCodes() {
        try {
            /*Get all items*/
            ArrayList<ItemDTO> all = placeOrderBO.getAllItems();
            for (ItemDTO dto : all) {
                cmbItemCode.getItems().add(dto.getItemCode());
            }
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void btnAddToListOnAction(ActionEvent actionEvent) {
        if (!txtQuantity.getText().matches("\\d+") || Integer.parseInt(txtQuantity.getText()) <= 0 ||
                Integer.parseInt(txtQuantity.getText()) > Integer.parseInt(txtQtyOnHand.getText()) || txtDiscount.getText().matches("^[0-9.]$")) {
            new Alert(Alert.AlertType.ERROR, "Invalid Quantity").show();
            txtQuantity.requestFocus();
            txtQuantity.selectAll();
            return;
        }
        String itemCode = cmbItemCode.getSelectionModel().getSelectedItem();
        String description = txtItemDesc.getText();
        BigDecimal unitPrice = new BigDecimal(txtUnitPrice.getText()).setScale(2);
        int qty = Integer.parseInt(txtQuantity.getText());
        BigDecimal discount = !txtDiscount.getText().isEmpty() ? new BigDecimal(txtDiscount.getText()).multiply(BigDecimal.valueOf(qty)).setScale(2) : new BigDecimal(0).setScale(2);
        BigDecimal totalAmount = unitPrice.multiply(new BigDecimal(qty)).setScale(2);
        BigDecimal totalAmountPayable = totalAmount.subtract(discount).setScale(2);

        boolean exists = tblOrderDetail.getItems().stream().anyMatch(detail -> detail.getItemCode().equals(itemCode));

        if (exists) {
            OrderDetailTM orderDetailTM = tblOrderDetail.getItems().stream().filter(detail -> detail.getItemCode().equals(itemCode)).findFirst().get();

            if (btnAddToList.getText().equalsIgnoreCase("Update")) {
                orderDetailTM.setOrderQty(qty);
                orderDetailTM.setTotalAmount(totalAmount);
                orderDetailTM.setTotalAmountPayable(totalAmountPayable);
                orderDetailTM.setDiscount(discount);

                tblOrderDetail.getSelectionModel().clearSelection();
            } else {
                orderDetailTM.setOrderQty(orderDetailTM.getOrderQty() + qty);
                orderDetailTM.setDiscount(orderDetailTM.getDiscount().add(discount));
                totalAmount = new BigDecimal(orderDetailTM.getOrderQty()).multiply(unitPrice).setScale(2);
                totalAmountPayable = new BigDecimal(orderDetailTM.getOrderQty()).multiply(unitPrice).subtract(discount).setScale(2);
                orderDetailTM.setTotalAmount(totalAmount);
                orderDetailTM.setTotalAmountPayable(totalAmountPayable);
            }
            tblOrderDetail.refresh();
        } else {
            tblOrderDetail.getItems().add(new OrderDetailTM(itemCode, description, qty, unitPrice, discount, totalAmount, totalAmountPayable));
        }
        cmbItemCode.getSelectionModel().clearSelection();
        cmbItemCode.requestFocus();
        calculateTotal();
        enableOrDisablePlaceOrderButton();
    }

    private void calculateTotal() {
        BigDecimal totalAmount = new BigDecimal(0);
        BigDecimal totalAmountPayable = new BigDecimal(0);
        BigDecimal discount = new BigDecimal(0);
        for (OrderDetailTM detail : tblOrderDetail.getItems()) {
            totalAmount = totalAmount.add(detail.getTotalAmount());
            totalAmountPayable = totalAmountPayable.add(detail.getTotalAmountPayable());
            discount = discount.add(detail.getDiscount());
        }
        txtTotal.setText(totalAmountPayable + "");
        txtTotalPrice.setText(totalAmount + "");
        txtDiscountAmount.setText(discount + "");
    }

    private void enableOrDisablePlaceOrderButton() {
        btnConfirmOrder.setDisable(!(cmbCusID.getSelectionModel().getSelectedItem() != null && !tblOrderDetail.getItems().isEmpty()));
    }

    public void btnConfirmOrderOnAction(ActionEvent actionEvent) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to confirm this order?", ButtonType.YES, ButtonType.NO);
        Optional<ButtonType> buttonType = alert.showAndWait();

        if (buttonType.get().equals(ButtonType.YES)) {
            boolean b = saveOrder(orderId, LocalDate.now(), cmbCusID.getValue(),
                    tblOrderDetail.getItems().stream().map(tm -> new OrderDetailDTO(orderId, tm.getItemCode(), tm.getOrderQty(), tm.getDiscount())).collect(Collectors.toList()));
            if (b) {
                new Alert(Alert.AlertType.INFORMATION, "Order has been placed successfully.").show();
            } else {
                new Alert(Alert.AlertType.ERROR, "Order has not been placed successfully.").show();
            }
        }
        orderId = generateNewOrderId();
        lblOrderID.setText("Order ID: " + orderId);
        cmbCusID.getSelectionModel().clearSelection();
        cmbItemCode.getSelectionModel().clearSelection();
        tblOrderDetail.getItems().clear();
        txtQuantity.clear();
        txtDiscount.clear();
        calculateTotal();
    }

    public boolean saveOrder(String orderId, LocalDate orderDate, String customerId, List<OrderDetailDTO> orderDetails) {
        try {
            return placeOrderBO.purchaseOrder(new OrderDTO(orderId, orderDate, customerId, orderDetails));
        } catch (SQLException | ClassNotFoundException throwable) {
            throwable.printStackTrace();
        }
        return false;
    }

    public void btnCancelOnAction(ActionEvent event) {
        cmbCusID.getSelectionModel().clearSelection();
        cmbItemCode.getSelectionModel().clearSelection();
        txtQuantity.clear();
        txtDiscount.clear();
        txtTotalPrice.setText("");
        txtDiscountAmount.setText("");
        txtTotal.clear();
        txtCash.clear();
        txtChange.clear();
        tblOrderDetail.getItems().clear();
    }

    public void txtChangeKeyPressed(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER)
            calculateChange();
    }

    private void calculateChange() {
        BigDecimal change = new BigDecimal(txtCash.getText()).subtract(new BigDecimal(txtTotal.getText())).setScale(2);
        txtChange.setText(String.valueOf(change));
    }

    public void btnPayAmountOnAction(ActionEvent event) {
        final ObservableList<OrderDetailTM> orders = tblOrderDetail.getItems();
        BigDecimal totalAmount = new BigDecimal(txtTotalPrice.getText()).setScale(2);
        BigDecimal totalAmountPayable = new BigDecimal(txtTotal.getText()).setScale(2);
        BigDecimal discount = new BigDecimal(txtDiscountAmount.getText()).setScale(2);
        BigDecimal cash = new BigDecimal(txtCash.getText()).setScale(2);
        BigDecimal change = new BigDecimal(txtChange.getText()).setScale(2);
        String customerId = cmbCusID.getValue();
        String orderId = lblOrderID.getText();

        HashMap map = new HashMap();
        map.put("orderId", orderId);
        map.put("customerId", customerId);
        map.put("totalAmount", totalAmount);
        map.put("discount", discount);
        map.put("totalAmountPayable", totalAmountPayable);
        map.put("cash", cash);
        map.put("change", change);

        try {
            JasperReport compiledReport = (JasperReport) JRLoader.loadObject(this.getClass().getResource("/wholesale_business/view/report/POSReceipt.jasper"));
            JasperPrint jasperPrint = JasperFillManager.fillReport(compiledReport, map, new JRBeanCollectionDataSource(orders));
            JasperViewer.viewReport(jasperPrint, false);
        } catch (JRException e) {
            e.printStackTrace();
        }
    }
}
