package wholesale_business.dao.custom.impl;

import wholesale_business.dao.SQLUtil;
import wholesale_business.dao.custom.OrderDAO;
import wholesale_business.entity.Orders;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;

public class OrderDAOImpl implements OrderDAO {

    @Override
    public ArrayList<Orders> getAll() throws SQLException, ClassNotFoundException {
        ResultSet rst = SQLUtil.executeQuery("SELECT * FROM Orders");
        ArrayList<Orders> allOrders = new ArrayList<>();
        while (rst.next()) {
            allOrders.add(new Orders(rst.getString(1), rst.getDate(2).toLocalDate(), rst.getString(3)));
        }
        return allOrders;
    }

    @Override
    public boolean save(Orders entity) throws SQLException, ClassNotFoundException {
        return SQLUtil.executeUpdate("INSERT INTO Orders (orderID, orderDate, custID) VALUES (?,?,?)", entity.getOrderID(), entity.getOrderDate(), entity.getCustID());
    }

    @Override
    public boolean update(Orders entity) throws SQLException, ClassNotFoundException {
        return SQLUtil.executeUpdate("UPDATE Orders SET orderDate=?, custID=? WHERE orderID=?", entity.getOrderDate(), entity.getCustID(), entity.getOrderID());
    }

    @Override
    public Orders search(String s) throws SQLException, ClassNotFoundException {
        return null;
    }

    @Override
    public boolean exist(String oid) throws SQLException, ClassNotFoundException {
        return SQLUtil.executeQuery("SELECT orderID FROM Orders WHERE orderID=?", oid).next();
    }

    @Override
    public boolean delete(String oid) throws SQLException, ClassNotFoundException {
        return SQLUtil.executeUpdate("DELETE FROM Orders WHERE orderID=?", oid);
    }

    @Override
    public String generateNewID() throws SQLException, ClassNotFoundException {
        ResultSet rst = SQLUtil.executeQuery("SELECT orderID FROM Orders ORDER BY orderID DESC LIMIT 1;");
        return rst.next() ? String.format("OID%03d", (Integer.parseInt(rst.getString("orderID").replace("OID", "")) + 1)) : "OID001";
    }

    @Override
    public ArrayList<String> getOrderIDsByCustomerID(String id) throws SQLException, ClassNotFoundException {
        ResultSet rst = SQLUtil.executeQuery("SELECT orderID FROM Orders WHERE custID=?", id);
        ArrayList<String> allOrders = new ArrayList<>();
        while (rst.next()) {
            allOrders.add(rst.getString(1));
        }
        return allOrders;
    }

    @Override
    public ArrayList<Orders> getAllOrdersByDay(LocalDate date) throws SQLException, ClassNotFoundException {
        ResultSet rst = SQLUtil.executeQuery("SELECT * FROM Orders WHERE orderDate=?", date);
        ArrayList<Orders> allOrders = new ArrayList<>();
        while (rst.next()) {
            allOrders.add(new Orders(rst.getString(1), rst.getDate(2).toLocalDate(), rst.getString(3)));
        }
        return allOrders;
    }
}
