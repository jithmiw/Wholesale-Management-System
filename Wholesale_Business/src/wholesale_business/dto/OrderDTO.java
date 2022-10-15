package wholesale_business.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class OrderDTO {
    List<OrderDetailDTO> orderDetails;
    private String orderID;
    private LocalDate orderDate;
    private String custID;
    private String custName;
    private BigDecimal orderTotal;

    public OrderDTO() {
    }

    public OrderDTO(String orderID, LocalDate orderDate, String custID) {
        this.orderID = orderID;
        this.orderDate = orderDate;
        this.custID = custID;
    }

    public OrderDTO(String orderID, LocalDate orderDate, String custID, List<OrderDetailDTO> orderDetails) {
        this.orderID = orderID;
        this.orderDate = orderDate;
        this.custID = custID;
        this.orderDetails = orderDetails;
    }

    public OrderDTO(String orderID, LocalDate orderDate, String custID, String custName, BigDecimal orderTotal) {
        this.orderID = orderID;
        this.orderDate = orderDate;
        this.custID = custID;
        this.custName = custName;
        this.orderTotal = orderTotal;
    }

    public String getOrderID() {
        return orderID;
    }

    public void setOrderID(String orderID) {
        this.orderID = orderID;
    }

    public LocalDate getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDate orderDate) {
        this.orderDate = orderDate;
    }

    public String getCustID() {
        return custID;
    }

    public void setCustID(String custID) {
        this.custID = custID;
    }

    public List<OrderDetailDTO> getOrderDetails() {
        return orderDetails;
    }

    public void setOrderDetails(List<OrderDetailDTO> orderDetails) {
        this.orderDetails = orderDetails;
    }

    public String getCustName() {
        return custName;
    }

    public void setCustName(String custName) {
        this.custName = custName;
    }

    public BigDecimal getOrderTotal() {
        return orderTotal;
    }

    public void setOrderTotal(BigDecimal orderTotal) {
        this.orderTotal = orderTotal;
    }

    @Override
    public String toString() {
        return "OrderDTO{" +
                "orderDetails=" + orderDetails +
                ", orderID='" + orderID + '\'' +
                ", orderDate=" + orderDate +
                ", custID='" + custID + '\'' +
                ", custName='" + custName + '\'' +
                ", orderTotal=" + orderTotal +
                '}';
    }
}
