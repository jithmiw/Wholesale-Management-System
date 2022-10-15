package wholesale_business.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import wholesale_business.bo.BOFactory;
import wholesale_business.bo.custom.CustomerBO;
import wholesale_business.dto.CustomerDTO;
import wholesale_business.validation.Validations;
import wholesale_business.view.tdm.CustomerTM;

import java.sql.SQLException;
import java.util.*;
import java.util.regex.Pattern;

import static javafx.scene.input.KeyCode.ENTER;

public class CustomerFormController {
    private final CustomerBO customerBO = (CustomerBO) BOFactory.getBoFactory().getBO(BOFactory.BOTypes.CUSTOMER);
    public JFXTextField txtSearch;
    public JFXButton btnSearch;
    public JFXButton btnRefresh;
    public JFXTextField txtCusID;
    public JFXTextField txtCusTitle;
    public JFXTextField txtCusName;
    public JFXTextField txtCusAddress;
    public JFXTextField txtCusCity;
    public JFXTextField txtCusProvince;
    public JFXTextField txtCusPostalCode;
    public JFXButton btnAddNewCustomer;
    public JFXButton btnSave;
    public JFXButton btnDelete;
    public TableView<CustomerTM> tblCustomers;
    public LinkedHashMap<JFXTextField, Pattern> map = new LinkedHashMap<>();
    private StackPane stackPane;
    private AnchorPane rootPane;

    public void setPane(StackPane stackPane, AnchorPane rootPane) {
        this.stackPane = stackPane;
        this.rootPane = rootPane;
    }

    public void initialize() {
        tblCustomers.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("custID"));
        tblCustomers.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("custTitle"));
        tblCustomers.getColumns().get(2).setCellValueFactory(new PropertyValueFactory<>("custName"));
        tblCustomers.getColumns().get(3).setCellValueFactory(new PropertyValueFactory<>("custAddress"));
        tblCustomers.getColumns().get(4).setCellValueFactory(new PropertyValueFactory<>("city"));
        tblCustomers.getColumns().get(5).setCellValueFactory(new PropertyValueFactory<>("province"));
        tblCustomers.getColumns().get(6).setCellValueFactory(new PropertyValueFactory<>("postalCode"));

        initUI();
        addPattern();

        tblCustomers.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            btnDelete.setDisable(newValue == null);
            btnSave.setText(newValue != null ? "Update" : "Save");
            btnSave.setDisable(newValue == null);

            if (newValue != null) {
                txtCusID.setText(newValue.getCustID());
                txtCusTitle.setText(newValue.getCustTitle());
                txtCusName.setText(newValue.getCustName());
                txtCusAddress.setText(newValue.getCustAddress());
                txtCusCity.setText(newValue.getCity());
                txtCusProvince.setText(newValue.getProvince());
                txtCusPostalCode.setText(newValue.getPostalCode());

                txtCusID.setDisable(false);
                txtCusTitle.setDisable(false);
                txtCusName.setDisable(false);
                txtCusAddress.setDisable(false);
                txtCusCity.setDisable(false);
                txtCusProvince.setDisable(false);
                txtCusPostalCode.setDisable(false);
            }
        });
        txtCusPostalCode.setOnAction(event -> btnSave.fire());
        loadAllCustomers();
    }

    private void loadAllCustomers() {
        tblCustomers.getItems().clear();
        /*Get all customers*/
        try {
            ArrayList<CustomerDTO> allCustomers = customerBO.getAllCustomers();
            for (CustomerDTO customer : allCustomers) {
                tblCustomers.getItems().add(new CustomerTM(customer.getCustID(), customer.getCustTitle(), customer.getCustName(), customer.getCustAddress(), customer.getCity(), customer.getProvince(), customer.getPostalCode()));
            }
        } catch (SQLException | ClassNotFoundException e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
        }
    }

    private void initUI() {
        txtCusID.clear();
        txtCusTitle.clear();
        txtCusName.clear();
        txtCusAddress.clear();
        txtCusCity.clear();
        txtCusProvince.clear();
        txtCusPostalCode.clear();
        txtSearch.clear();
        txtCusID.setDisable(true);
        txtCusTitle.setDisable(true);
        txtCusName.setDisable(true);
        txtCusAddress.setDisable(true);
        txtCusCity.setDisable(true);
        txtCusProvince.setDisable(true);
        txtCusPostalCode.setDisable(true);
        txtCusID.setEditable(false);
        btnSave.setDisable(true);
        btnDelete.setDisable(true);
    }

    public void btnAddNewOnAction(ActionEvent actionEvent) {
        txtCusID.setDisable(false);
        txtCusTitle.setDisable(false);
        txtCusName.setDisable(false);
        txtCusAddress.setDisable(false);
        txtCusCity.setDisable(false);
        txtCusProvince.setDisable(false);
        txtCusPostalCode.setDisable(false);
        txtCusID.clear();
        txtCusID.setText(generateNewId());
        txtCusTitle.clear();
        txtCusName.clear();
        txtCusAddress.clear();
        txtCusCity.clear();
        txtCusProvince.clear();
        txtCusPostalCode.clear();
        txtSearch.clear();
        txtCusTitle.requestFocus();
        btnSave.setDisable(false);
        btnSave.setText("Save");
        tblCustomers.getSelectionModel().clearSelection();
    }

    private void addPattern() {
        Pattern patternTitle = Pattern.compile("^[A-z ]{3,5}$");
        Pattern patternName = Pattern.compile("^[A-z. ]{3,30}$");
        Pattern patternAddress = Pattern.compile("^[A-z0-9 ,/]{4,30}$");
        Pattern patternCity = Pattern.compile("^[A-z]{3,20}$");
        Pattern patternProvince = Pattern.compile("^[A-z ]{3,20}$");
        Pattern patternPostalCode = Pattern.compile("^[A-z0-9 ,/]{3,9}$");

        map.put(txtCusTitle, patternTitle);
        map.put(txtCusName, patternName);
        map.put(txtCusAddress, patternAddress);
        map.put(txtCusCity, patternCity);
        map.put(txtCusProvince, patternProvince);
        map.put(txtCusPostalCode, patternPostalCode);
    }

    public void textFieldsKeyReleased(KeyEvent keyEvent) {
        Validations.validate(map, btnSave);

        if (keyEvent.getCode() == ENTER) {
            Object response = Validations.validate(map, btnSave);

            if (response instanceof TextField) {
                TextField textField = (TextField) response;
                textField.requestFocus();
            } else if (response instanceof Boolean) {
                btnSaveOnAction();
            }
        }
    }

    public void btnSaveOnAction() {
        String id = txtCusID.getText();
        String title = txtCusTitle.getText();
        String name = txtCusName.getText();
        String address = txtCusAddress.getText();
        String city = txtCusCity.getText();
        String province = txtCusProvince.getText();
        String postalCode = txtCusPostalCode.getText();

        if (!title.matches("^[A-z ]{3,5}$")) {
            new Alert(Alert.AlertType.ERROR, "Invalid title").show();
            txtCusTitle.requestFocus();
            return;
        } else if (!name.matches("^[A-z. ]{3,30}$")) {
            new Alert(Alert.AlertType.ERROR, "Invalid name").show();
            txtCusName.requestFocus();
            return;
        } else if (!address.matches("^[A-z0-9 ,/]{4,30}$")) {
            new Alert(Alert.AlertType.ERROR, "Invalid address").show();
            txtCusAddress.requestFocus();
            return;
        } else if (!city.matches("^[A-z]{3,20}$")) {
            new Alert(Alert.AlertType.ERROR, "Invalid city").show();
            txtCusCity.requestFocus();
            return;
        } else if (!province.matches("^[A-z ]{3,20}$")) {
            new Alert(Alert.AlertType.ERROR, "Invalid province").show();
            txtCusProvince.requestFocus();
            return;
        } else if (!postalCode.matches("^[A-z0-9 ,/]{3,9}$")) {
            new Alert(Alert.AlertType.ERROR, "Invalid postal code").show();
            txtCusPostalCode.requestFocus();
            return;
        }

        if (btnSave.getText().equalsIgnoreCase("Save")) {
            /*Save Customer*/
            try {
                if (existCustomer(id)) {
                    new Alert(Alert.AlertType.ERROR, id + " already exists").show();
                }

                customerBO.saveCustomer(new CustomerDTO(id, title, name, address, city, province, postalCode));
                tblCustomers.getItems().add(new CustomerTM(id, title, name, address, city, province, postalCode));
            } catch (SQLException e) {
                new Alert(Alert.AlertType.ERROR, "Failed to save the customer " + e.getMessage()).show();
                e.printStackTrace();

            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            /*Update customer*/
            try {
                if (!existCustomer(id)) {
                    new Alert(Alert.AlertType.ERROR, "There is no such customer associated with the id " + id).show();
                }
                //Customer update
                customerBO.updateCustomer(new CustomerDTO(id, title, name, address, city, province, postalCode));

            } catch (SQLException e) {
                new Alert(Alert.AlertType.ERROR, "Failed to update the customer " + id + e.getMessage()).show();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

            CustomerTM selectedCustomer = tblCustomers.getSelectionModel().getSelectedItem();
            selectedCustomer.setCustTitle(title);
            selectedCustomer.setCustName(name);
            selectedCustomer.setCustAddress(address);
            selectedCustomer.setCity(city);
            selectedCustomer.setProvince(province);
            selectedCustomer.setPostalCode(postalCode);
            tblCustomers.refresh();
        }
        btnAddNewCustomer.fire();
    }


    boolean existCustomer(String id) throws SQLException, ClassNotFoundException {
        return customerBO.customerExist(id);
    }


    public void btnDeleteOnAction() {
        /*Delete Customer*/
        String id = tblCustomers.getSelectionModel().getSelectedItem().getCustID();
        try {
            if (!existCustomer(id)) {
                new Alert(Alert.AlertType.ERROR, "There is no such customer associated with the id " + id).show();
            }
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to delete this customer?", ButtonType.YES, ButtonType.NO);
            Optional<ButtonType> buttonType = alert.showAndWait();

            if (buttonType.get().equals(ButtonType.YES)) {
                customerBO.deleteCustomer(id);
                tblCustomers.getItems().remove(tblCustomers.getSelectionModel().getSelectedItem());
                tblCustomers.getSelectionModel().clearSelection();
                initUI();
            }
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, "Failed to delete the customer " + id).show();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void findCustomer(String id) {
        tblCustomers.getItems().clear();
        try {
            CustomerDTO customer = customerBO.searchCustomer(id);
            if (customer != null) {
                tblCustomers.getItems().add(new CustomerTM(customer.getCustID(), customer.getCustTitle(), customer.getCustName(), customer.getCustAddress(), customer.getCity(), customer.getProvince(), customer.getPostalCode()));
            } else {
                new Alert(Alert.AlertType.INFORMATION, "No search results.").show();
            }
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException("Failed to find the Item " + id, e);
        }
    }

    private String generateNewId() {
        try {
            return customerBO.generateNewCustomerID();
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, "Failed to generate a new id " + e.getMessage()).show();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        if (tblCustomers.getItems().isEmpty()) {
            return "C0-001";
        } else {
            String id = getLastCustomerId();
            int newCustomerId = Integer.parseInt(id.replace("C0-", "")) + 1;
            return String.format("C0-%03d", newCustomerId);
        }

    }

    private String getLastCustomerId() {
        List<CustomerTM> tempCustomersList = new ArrayList<>(tblCustomers.getItems());
        Collections.sort(tempCustomersList);
        return tempCustomersList.get(tempCustomersList.size() - 1).getCustID();
    }

    public void menuDeleteOnAction(ActionEvent event) {
        btnDeleteOnAction();
    }

    public void txtSearchKeyPressed(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER)
            findCustomer(txtSearch.getText());
    }

    public void btnSearchOnAction(ActionEvent event) {
        findCustomer(txtSearch.getText());
    }

    public void btnRefreshOnAction(ActionEvent event) {
        initUI();
        loadAllCustomers();
    }
}
