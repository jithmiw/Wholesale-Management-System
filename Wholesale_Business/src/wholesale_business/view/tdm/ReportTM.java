package wholesale_business.view.tdm;

import java.math.BigDecimal;

public class ReportTM {
    private String itemCode;
    private String description;
    private int qtyOnHand;
    private BigDecimal revenue;

    public ReportTM() {
    }

    public ReportTM(String itemCode, String description, int qtyOnHand, BigDecimal revenue) {
        this.itemCode = itemCode;
        this.description = description;
        this.qtyOnHand = qtyOnHand;
        this.revenue = revenue;
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

    public int getQtyOnHand() {
        return qtyOnHand;
    }

    public void setQtyOnHand(int qtyOnHand) {
        this.qtyOnHand = qtyOnHand;
    }

    public BigDecimal getRevenue() {
        return revenue;
    }

    public void setRevenue(BigDecimal revenue) {
        this.revenue = revenue;
    }
}
