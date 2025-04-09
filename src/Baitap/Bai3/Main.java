package Baitap.Bai3;

import Baitap.ConnectionDB;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args) throws SQLException {
        Connection conn = null;
        CallableStatement callSt = null;
        try {
            conn = ConnectionDB.openConnection();
            if (conn != null) {
                callSt = conn.prepareCall("{call transfer_money(?,?,?)}");
                callSt.setInt(1, 1);
                callSt.setInt(2, 2);
                callSt.setBigDecimal(3, new java.math.BigDecimal(500.00));
                callSt.execute();
                conn.commit();
                System.out.println("Chuyển tiền thành công");
            }
        }catch (SQLException e){
            conn.rollback();
            System.out.println("Có lỗi thực hiện rollback");
            e.printStackTrace();
        }catch (Exception e){
            System.out.println(e.getMessage());
        }finally {
            ConnectionDB.closeConnection(conn, callSt);
        }
    }
}
