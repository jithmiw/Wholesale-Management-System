package wholesale_business.dao.custom;

import wholesale_business.dao.CrudDAO;
import wholesale_business.entity.OrderDetail;

import java.sql.SQLException;

public interface OrderDetailDAO extends CrudDAO<OrderDetail, String> {

    boolean deleteItemFromList(String oid, String code) throws SQLException, ClassNotFoundException;
}
