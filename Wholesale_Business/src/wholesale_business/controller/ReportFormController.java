package wholesale_business.controller;

import com.jfoenix.controls.JFXComboBox;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import wholesale_business.bo.BOFactory;
import wholesale_business.bo.custom.ReportBO;
import wholesale_business.dto.CustomDTO;
import wholesale_business.dto.ItemDTO;
import wholesale_business.dto.OrderDTO;
import wholesale_business.view.tdm.ReportTM;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class ReportFormController {
    public JFXComboBox<String> cmbSearchReport;
    public Label lblRevenue;
    public Label lblTotalOrders;
    public Label lblAvgOrders;
    public Label lblBItem1;
    public Label lblBItem2;
    public Label lblBItem3;
    public Label lblBItem4;
    public Label lblBItem5;
    public Label lblLItem1;
    public Label lblLItem2;
    public Label lblLItem3;
    public Label lblLItem4;
    public Label lblLItem5;
    public TableView<ReportTM> tblRevenue;
    ReportBO reportBO = (ReportBO) BOFactory.getBoFactory().getBO(BOFactory.BOTypes.REPORT);
    private StackPane stackPane;
    private AnchorPane rootPane;

    public void initialize() {
        tblRevenue.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("itemCode"));
        tblRevenue.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("description"));
        tblRevenue.getColumns().get(2).setCellValueFactory(new PropertyValueFactory<>("qtyOnHand"));
        tblRevenue.getColumns().get(3).setCellValueFactory(new PropertyValueFactory<>("revenue"));

        loadCmbSearchReport();
        loadTblRevenue();
        setLabels();
    }

    private void loadTblRevenue() {
        tblRevenue.getItems().clear();
        try {
            ArrayList<CustomDTO> list = reportBO.getAllOrdersByDay(LocalDate.now());
            HashMap<String, BigDecimal> hashMap = new HashMap<>();

            for (CustomDTO dto : list) {
                ItemDTO item = reportBO.searchItem(dto.getItemCode());
                BigDecimal totalRevenue = item.getUnitPrice().multiply(new BigDecimal(dto.getOrderQty())).setScale(2);
                hashMap.put(dto.getItemCode(), totalRevenue.subtract(dto.getDiscount()).setScale(2));
            }
            for (CustomDTO dto : list) {
                ItemDTO item = reportBO.searchItem(dto.getItemCode());
                if (hashMap.containsKey(dto.getItemCode())) {
                    BigDecimal revenue = hashMap.get(dto.getItemCode());
                    hashMap.remove(dto.getItemCode(), revenue);
                    hashMap.put(dto.getItemCode(), revenue.add(item.getUnitPrice().multiply(new BigDecimal(dto.getOrderQty())).setScale(2)));
                }
            }
            ArrayList<String> itemCodes = new ArrayList<>(hashMap.keySet());
            Collections.sort(itemCodes);

            for (String itemCode : itemCodes) {
                ItemDTO dto = reportBO.searchItem(itemCode);
                BigDecimal revenue = hashMap.get(itemCode);
                tblRevenue.getItems().add(new ReportTM(dto.getItemCode(), dto.getDescription(), dto.getQtyOnHand(), revenue));
            }
        } catch (SQLException | ClassNotFoundException e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
        }
    }

    private void loadCmbSearchReport() {
        String[] search = {"Today", "This Month", "This Year"};
        for (String s : search) {
            cmbSearchReport.getItems().add(s);
        }
    }

    public void setLabels() {
        try {
            BigDecimal totalRevenue = new BigDecimal(0);
            int totalOrders = 0;
            ArrayList<CustomDTO> list = reportBO.getAllOrdersByDay(LocalDate.now());
            HashMap<String, Integer> hashMap = new HashMap<>();

            for (CustomDTO dto : list) {
                ItemDTO item = reportBO.searchItem(dto.getItemCode());
                BigDecimal totalAmount = item.getUnitPrice().multiply(new BigDecimal(dto.getOrderQty())).setScale(2);
                totalRevenue = totalRevenue.add(totalAmount.subtract(dto.getDiscount()).setScale(2));

                hashMap.put(dto.getItemCode(), dto.getQtyOnHand());
            }
            for (CustomDTO dto : list) {
                if (hashMap.containsKey(dto.getItemCode())) {
                    Integer qty = hashMap.get(dto.getItemCode());
                    hashMap.remove(dto.getItemCode(), qty);
                    hashMap.put(dto.getItemCode(), qty + dto.getOrderQty());
                }
            }
            ArrayList<Integer> integers = new ArrayList<>(hashMap.values());
            Collections.sort(integers);
            Collections.reverse(integers);

            for (Object key : hashMap.keySet()) {
                if (hashMap.get(key).equals(integers.get(0)))
                    lblBItem1.setText(key + "");
                if (hashMap.get(key).equals(integers.get(integers.size() - 1)))
                    lblLItem1.setText(key + "");
            }
            ArrayList<OrderDTO> orderDTOS = reportBO.getTotalOrders(LocalDate.now());
            ArrayList<String> cusIds = new ArrayList<>();
            for (OrderDTO dto : orderDTOS) {
                if (!cusIds.contains(dto.getCustID())) {
                    cusIds.add(dto.getCustID());
                }
                totalOrders++;
            }
            lblRevenue.setText("Rs. " + totalRevenue + "");
            lblTotalOrders.setText(totalOrders + "");
            if (cusIds.size() != 0)
                lblAvgOrders.setText(totalOrders / cusIds.size() + "");
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void setPane(StackPane stackPane, AnchorPane rootPane) {
        this.stackPane = stackPane;
        this.rootPane = rootPane;
    }
}
