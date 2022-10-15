package wholesale_business.dao.custom.impl;

import wholesale_business.dao.SQLUtil;
import wholesale_business.dao.custom.OrderDetailDAO;
import wholesale_business.entity.OrderDetail;

import java.sql.SQLException;
import java.util.ArrayList;

public class OrderDetailDAOImpl implements OrderDetailDAO {

    @Override
    public ArrayList<OrderDetail> getAll() throws SQLException, ClassNotFoundException {
        return null;
    }

    @Override
    public boolean save(OrderDetail entity) throws SQLException, ClassNotFoundException {
        return SQLUtil.executeUpdate("INSERT INTO OrderDetail (orderID, itemCode, orderQty, discount) VALUES (?,?,?,?)", entity.getOrderID(), entity.getItemCode(), entity.getOrderQty(), entity.getDiscount());
    }

    @Override
    public boolean update(OrderDetail entity) throws SQLException, ClassNotFoundException {
        return SQLUtil.executeUpdate("UPDATE OrderDetail SET orderQty=?, discount=? WHERE orderID=? && itemCode=?", entity.getOrderQty(), entity.getDiscount(), entity.getOrderID(), entity.getItemCode());
    }

    @Override
    public OrderDetail search(String s) throws SQLException, ClassNotFoundException {
        return null;
    }

    @Override
    public boolean exist(String s) throws SQLException, ClassNotFoundException {
        return false;
    }

    @Override
    public boolean delete(String s) throws SQLException, ClassNotFoundException {
        return false;
    }

    @Override
    public String generateNewID() throws SQLException, ClassNotFoundException {
        return null;
    }

    @Override
    public boolean deleteItemFromList(String oid, String code) throws SQLException, ClassNotFoundException {
        return SQLUtil.executeUpdate("DELETE FROM OrderDetail WHERE orderID=? && itemCode=?", oid, code);
    }
}
