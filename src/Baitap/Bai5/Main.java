package Baitap.Bai5;

import Baitap.ConnectionDB;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args) {
        Connection conn = null;
        CallableStatement callSt = null;
        try {
            conn = ConnectionDB.openConnection();
            conn.setAutoCommit(false);
            callSt = conn.prepareCall("{call create_order(?,?)}");
            callSt.setString(1,"Nguyễn Văn A");
            callSt.registerOutParameter(2,java.sql.Types.INTEGER);
            callSt.execute();
            int OrderID = callSt.getInt(2);
            callSt = conn.prepareCall("{call add_order_detail(?,?,?)}");
            callSt.setInt(1,OrderID);
            callSt.setString(2,"Sản phẩm A");
            callSt.setInt(3,5);
            callSt.execute();
            callSt = conn.prepareCall("{call add_order_detail(?,?,?)}");
            callSt.setInt(1,OrderID);
            callSt.setString(2,"Sản phẩm B");
            callSt.setInt(3,-1);
            callSt.execute();
            conn.commit();
        }catch (SQLException e){
            System.out.println("Có lỗi xảy ra thực hiện rollback");
            try {
                if (conn != null) {
                    conn.rollback();
                }
            } catch (SQLException rollbackEx) {
                rollbackEx.printStackTrace();
            }
        }catch (Exception e){
            System.out.println(e.getMessage());
        }finally {
            ConnectionDB.closeConnection(conn, callSt);
        }
    }
}
