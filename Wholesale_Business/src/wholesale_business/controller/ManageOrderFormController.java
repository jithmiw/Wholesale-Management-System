package wholesale_business.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.view.JasperViewer;
import wholesale_business.bo.BOFactory;
import wholesale_business.bo.custom.ManageOrderBO;
import wholesale_business.dto.*;
import wholesale_business.view.tdm.OrderDetailTM;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class ManageOrderFormController {
    public JFXComboBox<String> cmbCusID;
    public JFXComboBox<String> cmbOrderID;
    public TableView<OrderDetailTM> tblItems;
    public TextField txtCash;
    public TextField txtChange;
    public JFXButton btnCancel;
    public JFXButton btnConfirmEdits;
    public JFXTextField txtCusName;
    public JFXTextField txtItemCode;
    public JFXTextField txtItemDesc;
    public JFXTextField txtCusAddress;
    public JFXTextField txtDiscount;
    public JFXTextField txtQuantity;
    public JFXTextField txtUnitPrice;
    public JFXButton btnUpdate;
    public Label txtTotalPrice;
    public Label txtDiscountAmount;
    public TextField txtTotal;
    public JFXButton btnPayAmount;
    public JFXButton btnDelete;
    public JFXButton btnRemove;
    ManageOrderBO manageOrderBO = (ManageOrderBO) BOFactory.getBoFactory().getBO(BOFactory.BOTypes.MANAGE_ORDER);
    private StackPane stackPane;
    private AnchorPane rootPane;

    public void initialize() {
        tblItems.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("itemCode"));
        tblItems.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("description"));
        tblItems.getColumns().get(2).setCellValueFactory(new PropertyValueFactory<>("orderQty"));
        tblItems.getColumns().get(3).setCellValueFactory(new PropertyValueFactory<>("unitPrice"));
        tblItems.getColumns().get(4).setCellValueFactory(new PropertyValueFactory<>("discount"));
        tblItems.getColumns().get(5).setCellValueFactory(new PropertyValueFactory<>("totalAmount"));
        tblItems.getColumns().get(6).setCellValueFactory(new PropertyValueFactory<>("totalAmountPayable"));

        btnConfirmEdits.setDisable(true);
        txtCusName.setFocusTraversable(false);
        txtCusName.setEditable(false);
        txtCusAddress.setFocusTraversable(false);
        txtCusAddress.setEditable(false);
        txtItemCode.setFocusTraversable(false);
        txtItemCode.setEditable(false);
        txtItemDesc.setFocusTraversable(false);
        txtItemDesc.setEditable(false);
        txtUnitPrice.setFocusTraversable(false);
        txtUnitPrice.setEditable(false);
        txtDiscount.setOnAction(event -> btnUpdate.fire());
        txtDiscount.setEditable(false);
        txtQuantity.setOnAction(event -> txtDiscount.requestFocus());
        txtQuantity.setEditable(false);
        btnUpdate.setDisable(true);
        btnDelete.setDisable(true);
        btnRemove.setDisable(true);

        cmbCusID.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            enableOrDisablePlaceOrderButton();
            if (newValue != null) {
                try {
                    CustomerDTO search = manageOrderBO.searchCustomer(newValue + "");
                    txtCusName.setText(search.getCustName());
                    txtCusAddress.setText(search.getCustAddress());
                    cmbOrderID.getItems().clear();
                    ArrayList<String> all = manageOrderBO.getOrdersIDsByCustomerID(newValue);
                    for (String oid : all) {
                        cmbOrderID.getItems().add(oid);
                    }
                } catch (SQLException e) {
                    new Alert(Alert.AlertType.ERROR, "Failed to load order ids").show();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });

        cmbOrderID.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            txtQuantity.setEditable(newValue != null);
            txtDiscount.setEditable(newValue != null);
            btnRemove.setDisable(newValue == null);
            tblItems.getItems().clear();

            if (newValue != null) {
                try {
                    ArrayList<CustomDTO> list = manageOrderBO.searchOrderByOrderID(newValue + "");
                    for (CustomDTO dto : list) {
                        ItemDTO item = manageOrderBO.searchItem(dto.getItemCode());
                        BigDecimal totalAmount = item.getUnitPrice().multiply(new BigDecimal(dto.getOrderQty())).setScale(2);
                        BigDecimal totalAmountPayable = totalAmount.subtract(dto.getDiscount()).setScale(2);
                        tblItems.getItems().add(new OrderDetailTM(dto.getItemCode(), item.getDescription(), dto.getOrderQty(), item.getUnitPrice(), dto.getDiscount(), totalAmount, totalAmountPayable));
                        calculateTotal();
                    }
                } catch (SQLException | ClassNotFoundException throwable) {
                    throwable.printStackTrace();
                }
            }
        });

        tblItems.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, selectedOrderDetail) -> {
            btnUpdate.setDisable(selectedOrderDetail == null);
            btnDelete.setDisable(selectedOrderDetail == null);
            calculateTotal();
            if (selectedOrderDetail != null) {
                txtItemCode.setText(selectedOrderDetail.getItemCode());
                txtItemDesc.setText(selectedOrderDetail.getDescription());
                txtUnitPrice.setText(selectedOrderDetail.getUnitPrice() + "");
                txtQuantity.setText(selectedOrderDetail.getOrderQty() + "");
                txtDiscount.setText(selectedOrderDetail.getDiscount().divide(BigDecimal.valueOf(selectedOrderDetail.getOrderQty())) + "");
            } else {
                txtQuantity.clear();
                txtDiscount.clear();
            }
        });
        loadCustomers();
    }

    private void initUI() {
        cmbOrderID.getSelectionModel().clearSelection();
        tblItems.getItems().clear();
        txtItemCode.clear();
        txtItemDesc.clear();
        txtUnitPrice.clear();
    }

    private void loadCustomers() {
        try {
            ArrayList<OrderDTO> all = manageOrderBO.getAllOrders();
            ArrayList<String> ids = new ArrayList<>();
            for (OrderDTO orderDTO : all) {
                ids.add(orderDTO.getCustID());
            }
            Set<String> set = new HashSet<>(ids);
            ids.clear();
            ids.addAll(set);
            for (String id : ids) {
                cmbCusID.getItems().add(id);
            }
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, "Failed to load customer ids").show();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void enableOrDisablePlaceOrderButton() {
        btnConfirmEdits.setDisable(!(cmbCusID.getSelectionModel().getSelectedItem() != null && !tblItems.getItems().isEmpty()));
    }

    private void calculateTotal() {
        BigDecimal totalAmount = new BigDecimal(0);
        BigDecimal totalAmountPayable = new BigDecimal(0);
        BigDecimal discount = new BigDecimal(0);
        for (OrderDetailTM detail : tblItems.getItems()) {
            totalAmount = totalAmount.add(detail.getTotalAmount());
            totalAmountPayable = totalAmountPayable.add(detail.getTotalAmountPayable());
            discount = discount.add(detail.getDiscount());
        }
        txtTotalPrice.setText("Rs. " + totalAmount + "");
        txtDiscountAmount.setText("Rs. " + discount + "");
        txtTotal.setText(totalAmountPayable + "");
    }

    public void setPane(StackPane stackPane, AnchorPane rootPane) {
        this.stackPane = stackPane;
        this.rootPane = rootPane;
    }

    public void btnCancelOnAction(ActionEvent event) {
        initUI();
    }

    public void btnConfirmEditsOnAction(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to confirm this edits?", ButtonType.YES, ButtonType.NO);
        Optional<ButtonType> buttonType = alert.showAndWait();

        if (buttonType.get().equals(ButtonType.YES)) {
            boolean b = updateOrder(cmbOrderID.getValue(), LocalDate.now(), cmbCusID.getValue(),
                    tblItems.getItems().stream().map(tm -> new OrderDetailDTO(cmbOrderID.getValue(), tm.getItemCode(), tm.getOrderQty(), tm.getDiscount())).collect(Collectors.toList()));
            if (b) {
                new Alert(Alert.AlertType.INFORMATION, "Order has been updated successfully.").show();
            } else {
                new Alert(Alert.AlertType.ERROR, "Order has not been updated successfully.").show();
            }
        }
        cmbCusID.getSelectionModel().clearSelection();
        cmbOrderID.getSelectionModel().clearSelection();
        tblItems.getItems().clear();
        txtItemCode.clear();
        txtItemDesc.clear();
        txtUnitPrice.clear();
        txtQuantity.clear();
        txtDiscount.clear();
        calculateTotal();
    }

    public boolean updateOrder(String orderId, LocalDate orderDate, String customerId, List<OrderDetailDTO> orderDetails) {
        try {
            return manageOrderBO.editOrder(new OrderDTO(orderId, orderDate, customerId, orderDetails));
        } catch (SQLException | ClassNotFoundException throwable) {
            throwable.printStackTrace();
        }
        return false;
    }

    public void btnUpdateOnAction(ActionEvent event) {
        if (!txtQuantity.getText().matches("\\d+") || Integer.parseInt(txtQuantity.getText()) <= 0 || txtDiscount.getText().matches("^[0-9.]$")) {
            new Alert(Alert.AlertType.ERROR, "Invalid Quantity").show();
            txtQuantity.requestFocus();
            txtQuantity.selectAll();
            return;
        }
        String itemCode = txtItemCode.getText();
        BigDecimal unitPrice = new BigDecimal(txtUnitPrice.getText()).setScale(2);
        int qty = Integer.parseInt(txtQuantity.getText());
        BigDecimal discount = !txtDiscount.getText().isEmpty() ? new BigDecimal(txtDiscount.getText()).multiply(BigDecimal.valueOf(qty)).setScale(2) : new BigDecimal(0).setScale(2);
        BigDecimal totalAmount = unitPrice.multiply(new BigDecimal(qty)).setScale(2);
        BigDecimal totalAmountPayable = totalAmount.subtract(discount).setScale(2);

        boolean exists = tblItems.getItems().stream().anyMatch(detail -> detail.getItemCode().equals(itemCode));

        if (exists) {
            OrderDetailTM orderDetailTM = tblItems.getItems().stream().filter(detail -> detail.getItemCode().equals(itemCode)).findFirst().get();
            orderDetailTM.setOrderQty(qty);
            orderDetailTM.setDiscount(discount);
            totalAmount = new BigDecimal(orderDetailTM.getOrderQty()).multiply(unitPrice).setScale(2);
            totalAmountPayable = new BigDecimal(orderDetailTM.getOrderQty()).multiply(unitPrice).subtract(discount).setScale(2);
            orderDetailTM.setTotalAmount(totalAmount);
            orderDetailTM.setTotalAmountPayable(totalAmountPayable);
            tblItems.refresh();
        }
        calculateTotal();
        enableOrDisablePlaceOrderButton();
    }

    public void btnDeleteOnAction(ActionEvent event) {
        String oid = cmbOrderID.getValue();
        String code = tblItems.getSelectionModel().getSelectedItem().getItemCode();
        try {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to delete this item from the list?", ButtonType.YES, ButtonType.NO);
            Optional<ButtonType> buttonType = alert.showAndWait();

            if (buttonType.get().equals(ButtonType.YES)) {
                boolean b = manageOrderBO.deleteItem(oid, code);
                if (b) {
                    new Alert(Alert.AlertType.INFORMATION, "Item has been deleted successfully.").show();
                } else {
                    new Alert(Alert.AlertType.ERROR, "Item has not been deleted successfully.").show();
                }
                tblItems.getItems().remove(tblItems.getSelectionModel().getSelectedItem());
            }
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, "Failed to delete the item " + code).show();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void btnRemoveOnAction(ActionEvent event) {
        try {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to remove this order?", ButtonType.YES, ButtonType.NO);
            Optional<ButtonType> buttonType = alert.showAndWait();

            if (buttonType.get().equals(ButtonType.YES)) {
                boolean b = manageOrderBO.removeOrder(cmbOrderID.getValue());
                if (b) {
                    new Alert(Alert.AlertType.INFORMATION, "Order has been removed successfully.").show();
                } else {
                    new Alert(Alert.AlertType.ERROR, "Order has not been removed successfully.").show();
                }
                initUI();
            }
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, "Failed to remove the order " + cmbOrderID.getValue()).show();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
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
        final ObservableList<OrderDetailTM> itemList = tblItems.getItems();
        BigDecimal totalAmount = new BigDecimal(txtTotalPrice.getText()).setScale(2);
        BigDecimal totalAmountPayable = new BigDecimal(txtTotal.getText()).setScale(2);
        BigDecimal discount = new BigDecimal(txtDiscountAmount.getText()).setScale(2);
        BigDecimal cash = new BigDecimal(txtCash.getText()).setScale(2);
        BigDecimal change = new BigDecimal(txtChange.getText()).setScale(2);
        String customerId = cmbCusID.getValue();
        String orderId = "Order ID : " + cmbOrderID.getValue();

        HashMap hashMap = new HashMap();
        hashMap.put("orderId", orderId);
        hashMap.put("customerId", customerId);
        hashMap.put("totalAmount", totalAmount);
        hashMap.put("discount", discount);
        hashMap.put("totalAmountPayable", totalAmountPayable);
        hashMap.put("cash", cash);
        hashMap.put("change", change);

        try {
            JasperReport compiledReport = (JasperReport) JRLoader.loadObject(this.getClass().getResource("/wholesale_business/view/report/POSReceipt.jasper"));
            JasperPrint jasperPrint = JasperFillManager.fillReport(compiledReport, hashMap, new JRBeanCollectionDataSource(itemList));
            JasperViewer.viewReport(jasperPrint, false);
        } catch (JRException e) {
            e.printStackTrace();
        }
    }
}
