package wholesale_business.view.tdm;

import java.math.BigDecimal;

public class OrderDetailTM {
    private String itemCode;
    private String description;
    private int orderQty;
    private BigDecimal unitPrice;
    private BigDecimal discount;
    private BigDecimal totalAmount;
    private BigDecimal totalAmountPayable;

    public OrderDetailTM() {
    }

    public OrderDetailTM(String itemCode, String description, int orderQty, BigDecimal unitPrice, BigDecimal discount, BigDecimal totalAmount, BigDecimal totalAmountPayable) {
        this.itemCode = itemCode;
        this.description = description;
        this.orderQty = orderQty;
        this.unitPrice = unitPrice;
        this.discount = discount;
        this.totalAmount = totalAmount;
        this.totalAmountPayable = totalAmountPayable;
    }

    public String getItemCode() {
        return itemCode;
    }

    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getOrderQty() {
        return orderQty;
    }

    public void setOrderQty(int orderQty) {
        this.orderQty = orderQty;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public BigDecimal getDiscount() {
        return discount;
    }

    public void setDiscount(BigDecimal discount) {
        this.discount = discount;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public BigDecimal getTotalAmountPayable() {
        return totalAmountPayable;
    }

    public void setTotalAmountPayable(BigDecimal totalAmountPayable) {
        this.totalAmountPayable = totalAmountPayable;
    }

    @Override
    public String toString() {
        return "OrderDetailTM{" +
                "itemCode='" + itemCode + '\'' +
                ", description='" + description + '\'' +
                ", orderQty=" + orderQty +
                ", unitPrice=" + unitPrice +
                ", discount=" + discount +
                ", totalAmount=" + totalAmount +
                ", totalAmountPayable=" + totalAmountPayable +
                '}';
    }
}
