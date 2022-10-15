package wholesale_business.bo.custom.impl;

import wholesale_business.bo.custom.ManageOrderBO;
import wholesale_business.dao.DAOFactory;
import wholesale_business.dao.custom.*;
import wholesale_business.db.DBConnection;
import wholesale_business.dto.*;
import wholesale_business.entity.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

public class ManageOrderBOImpl implements ManageOrderBO {
    private final CustomerDAO customerDAO = (CustomerDAO) DAOFactory.getDaoFactory().getDAO(DAOFactory.DAOTypes.CUSTOMER);
    private final ItemDAO itemDAO = (ItemDAO) DAOFactory.getDaoFactory().getDAO(DAOFactory.DAOTypes.ITEM);
    private final OrderDAO orderDAO = (OrderDAO) DAOFactory.getDaoFactory().getDAO(DAOFactory.DAOTypes.ORDER);
    private final OrderDetailDAO orderDetailDAO = (OrderDetailDAO) DAOFactory.getDaoFactory().getDAO(DAOFactory.DAOTypes.ORDER_DETAIL);
    private final QueryDAO queryDAO = (QueryDAO) DAOFactory.getDaoFactory().getDAO(DAOFactory.DAOTypes.QUERY_DAO);

    @Override
    public boolean editOrder(OrderDTO dto) throws SQLException, ClassNotFoundException {

        /*Transaction*/
        Connection connection = DBConnection.getDbConnection().getConnection();
        connection.setAutoCommit(false);
        boolean update1 = orderDAO.update(new Orders(dto.getOrderID(), dto.getOrderDate(), dto.getCustID()));

        if (!update1) {
            connection.rollback();
            connection.setAutoCommit(true);
            return false;
        }

        for (OrderDetailDTO detailDTO : dto.getOrderDetails()) {
            boolean update2 = orderDetailDAO.update(new OrderDetail(detailDTO.getOrderID(), detailDTO.getItemCode(), detailDTO.getOrderQty(), detailDTO.getDiscount()));
            if (!update2) {
                connection.rollback();
                connection.setAutoCommit(true);
                return false;
            }

            //Search & Update Item
            ItemDTO item = searchItem(detailDTO.getItemCode());
            item.setQtyOnHand(item.getQtyOnHand() - detailDTO.getOrderQty());

            //update item
            boolean update3 = itemDAO.update(new Item(item.getItemCode(), item.getDescription(), item.getPackSize(), item.getUnitPrice(), item.getQtyOnHand()));

            if (!update3) {
                connection.rollback();
                connection.setAutoCommit(true);
                return false;
            }
        }
        connection.commit();
        connection.setAutoCommit(true);
        return true;
    }

    @Override
    public ArrayList<String> getOrdersIDsByCustomerID(String id) throws SQLException, ClassNotFoundException {
        return orderDAO.getOrderIDsByCustomerID(id);
    }

    @Override
    public ArrayList<OrderDTO> getAllOrders() throws SQLException, ClassNotFoundException {
        ArrayList<Orders> all = orderDAO.getAll();
        ArrayList<OrderDTO> allOrders = new ArrayList<>();
        for (Orders entity : all) {
            allOrders.add(new OrderDTO(entity.getOrderID(), entity.getOrderDate(), entity.getCustID()));
        }
        return allOrders;
    }

    @Override
    public ArrayList<CustomDTO> searchOrderByOrderID(String oid) throws SQLException, ClassNotFoundException {
        ArrayList<CustomEntity> all = queryDAO.searchOrderByOrderID(oid);
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
    public boolean removeOrder(String oid) throws SQLException, ClassNotFoundException {
        return orderDAO.delete(oid);
    }

    @Override
    public boolean deleteItem(String oid, String code) throws SQLException, ClassNotFoundException {
        return orderDetailDAO.deleteItemFromList(oid, code);
    }

    @Override
    public CustomerDTO searchCustomer(String id) throws SQLException, ClassNotFoundException {
        Customer entity = customerDAO.search(id);
        return new CustomerDTO(entity.getCustID(), entity.getCustTitle(), entity.getCustName(), entity.getCustAddress(), entity.getCity(), entity.getProvince(), entity.getPostalCode());
    }
}
