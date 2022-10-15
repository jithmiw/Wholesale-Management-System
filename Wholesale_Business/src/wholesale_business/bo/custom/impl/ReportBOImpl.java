package wholesale_business.bo.custom.impl;

import wholesale_business.bo.custom.ReportBO;
import wholesale_business.dao.DAOFactory;
import wholesale_business.dao.custom.ItemDAO;
import wholesale_business.dao.custom.OrderDAO;
import wholesale_business.dao.custom.QueryDAO;
import wholesale_business.dto.CustomDTO;
import wholesale_business.dto.ItemDTO;
import wholesale_business.dto.OrderDTO;
import wholesale_business.entity.CustomEntity;
import wholesale_business.entity.Item;
import wholesale_business.entity.Orders;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;

public class ReportBOImpl implements ReportBO {
    private final ItemDAO itemDAO = (ItemDAO) DAOFactory.getDaoFactory().getDAO(DAOFactory.DAOTypes.ITEM);
    private final OrderDAO orderDAO = (OrderDAO) DAOFactory.getDaoFactory().getDAO(DAOFactory.DAOTypes.ORDER);
    private final QueryDAO queryDAO = (QueryDAO) DAOFactory.getDaoFactory().getDAO(DAOFactory.DAOTypes.QUERY_DAO);

    @Override
    public ArrayList<CustomDTO> getAllOrdersByDay(LocalDate date) throws SQLException, ClassNotFoundException {
        ArrayList<CustomEntity> all = queryDAO.getAllOrdersByDay(date);
        ArrayList<CustomDTO> customDTOS = new ArrayList<>();
        for (CustomEntity entity : all) {
            customDTOS.add(new CustomDTO(entity.getOrderID(), entity.getOrderDate(), entity.getCustID(), entity.getItemCode(), entity.getOrderQty(), entity.getDiscount()));
        }
        return customDTOS;
    }

    @Override
    public ItemDTO searchItem(String code) throws SQLException, ClassNotFoundException {
        Item entity = itemDAO.search(code);
        return new ItemDTO(entity.getItemCode(), entity.getDescription(), entity.getPackSize(), entity.getUnitPrice(), entity.getQtyOnHand());
    }

    @Override
    public ArrayList<OrderDTO> getTotalOrders(LocalDate date) throws SQLException, ClassNotFoundException {
        ArrayList<Orders> all = orderDAO.getAllOrdersByDay(date);
        ArrayList<OrderDTO> allOrders = new ArrayList<>();
        for (Orders entity : all) {
            allOrders.add(new OrderDTO(entity.getOrderID(), entity.getOrderDate(), entity.getCustID()));
        }
        return allOrders;
    }
}
