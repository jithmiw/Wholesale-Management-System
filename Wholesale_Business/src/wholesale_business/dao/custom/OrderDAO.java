package wholesale_business.dao.custom;

import wholesale_business.dao.CrudDAO;
import wholesale_business.entity.Orders;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;

public interface OrderDAO extends CrudDAO<Orders, String> {
    ArrayList<String> getOrderIDsByCustomerID(String id) throws SQLException, ClassNotFoundException;

    ArrayList<Orders> getAllOrdersByDay(LocalDate date) throws SQLException, ClassNotFoundException;
}
