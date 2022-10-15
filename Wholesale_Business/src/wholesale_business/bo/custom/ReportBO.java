package wholesale_business.bo.custom;

import wholesale_business.bo.SuperBO;
import wholesale_business.dto.CustomDTO;
import wholesale_business.dto.ItemDTO;
import wholesale_business.dto.OrderDTO;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;

public interface ReportBO extends SuperBO {
    ArrayList<CustomDTO> getAllOrdersByDay(LocalDate date) throws SQLException, ClassNotFoundException;

    ItemDTO searchItem(String code) throws SQLException, ClassNotFoundException;

    ArrayList<OrderDTO> getTotalOrders(LocalDate date) throws SQLException, ClassNotFoundException;
}
