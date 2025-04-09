package Baitap.Bai7;

import Baitap.ConnectionDB;

import java.sql.*;

public class Main {
    public static void main(String[] args) throws SQLException {
        Connection conn1 = null;
        Connection conn2 = null;
        CallableStatement callSt1 = null;
        CallableStatement callSt2 = null;
        try {
            conn1 = ConnectionDB.openConnection();
            conn2 = ConnectionDB.openConnection();
            conn1.setAutoCommit(false);
            conn2.setAutoCommit(false);
            conn1.setTransactionIsolation(Connection.TRANSACTION_READ_UNCOMMITTED);

            callSt2 = conn2.prepareCall("{call insert_order(?, ?)}");
            callSt2.setString(1, "Nguyễn Văn A");
            callSt2.setString(2, "pending");
            callSt2.execute();

            callSt1 = conn1.prepareCall("{call select_orders()}");
            ResultSet rs = callSt1.executeQuery();
            while (rs.next()) {
                System.out.println(rs.getInt("Mã đơn hàng") + " - " +
                        rs.getString("Tên khách hàng") + " - " + rs.getString("trạng thái"));
            }
        }catch (SQLException e) {
            System.out.println(e.getMessage());
            conn2.rollback();
        }catch (Exception e) {
            System.out.println(e.getMessage());
        }finally {
            ConnectionDB.closeConnection(conn1,callSt1);
            ConnectionDB.closeConnection(conn2,callSt2);
        }
    }
}
