package wholesale_business.dao.custom;

import wholesale_business.dao.SuperDAO;
import wholesale_business.entity.CustomEntity;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;

public interface QueryDAO extends SuperDAO {
    ArrayList<CustomEntity> searchOrderByOrderID(String oid) throws SQLException, ClassNotFoundException;

    ArrayList<CustomEntity> getAllOrdersByDay(LocalDate date) throws SQLException, ClassNotFoundException;
}
