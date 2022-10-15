package wholesale_business.dao.custom.impl;

import wholesale_business.dao.SQLUtil;
import wholesale_business.dao.custom.QueryDAO;
import wholesale_business.entity.CustomEntity;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;

public class QueryDAOImpl implements QueryDAO {

    @Override
    public ArrayList<CustomEntity> searchOrderByOrderID(String oid) throws SQLException, ClassNotFoundException {
        ResultSet rst = SQLUtil.executeQuery("SELECT Orders.orderID, Orders.orderDate, Orders.custID, OrderDetail.itemCode, OrderDetail.orderQty, OrderDetail.discount FROM Orders INNER JOIN OrderDetail ON Orders.orderID = OrderDetail.orderID WHERE Orders.orderID=?;", oid);
        ArrayList<CustomEntity> orderRecords = new ArrayList<>();
        while (rst.next()) {
            orderRecords.add(new CustomEntity(rst.getString(1), LocalDate.parse(rst.getString(2)), rst.getString(3), rst.getString(4), rst.getInt(5), rst.getBigDecimal(6)));
        }
        return orderRecords;
    }

    @Override
    public ArrayList<CustomEntity> getAllOrdersByDay(LocalDate date) throws SQLException, ClassNotFoundException {
        ResultSet rst = SQLUtil.executeQuery("SELECT Orders.orderID, Orders.orderDate, Orders.custID, OrderDetail.itemCode, OrderDetail.orderQty, OrderDetail.discount FROM Orders INNER JOIN OrderDetail ON Orders.orderID = OrderDetail.orderID WHERE Orders.orderDate=?;", date);
        ArrayList<CustomEntity> orderRecords = new ArrayList<>();
        while (rst.next()) {
            orderRecords.add(new CustomEntity(rst.getString(1), LocalDate.parse(rst.getString(2)), rst.getString(3), rst.getString(4), rst.getInt(5), rst.getBigDecimal(6)));
        }
        return orderRecords;
    }
}
