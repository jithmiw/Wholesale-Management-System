package wholesale_business.bo.custom;

import wholesale_business.bo.SuperBO;
import wholesale_business.dto.CustomDTO;
import wholesale_business.dto.CustomerDTO;
import wholesale_business.dto.ItemDTO;
import wholesale_business.dto.OrderDTO;

import java.sql.SQLException;
import java.util.ArrayList;

public interface ManageOrderBO extends SuperBO {
    boolean editOrder(OrderDTO dto) throws SQLException, ClassNotFoundException;

    ArrayList<String> getOrdersIDsByCustomerID(String id) throws SQLException, ClassNotFoundException;

    ArrayList<OrderDTO> getAllOrders() throws SQLException, ClassNotFoundException;

    CustomerDTO searchCustomer(String id) throws SQLException, ClassNotFoundException;

    ArrayList<CustomDTO> searchOrderByOrderID(String oid) throws SQLException, ClassNotFoundException;

    ItemDTO searchItem(String code) throws SQLException, ClassNotFoundException;

    boolean removeOrder(String oid) throws SQLException, ClassNotFoundException;

    boolean deleteItem(String id, String code) throws SQLException, ClassNotFoundException;
}
