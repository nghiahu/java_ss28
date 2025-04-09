package Baitap.Bai4;

import Baitap.ConnectionDB;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;

public class Bai4 {
    public static void main(String[] args) throws SQLException {
        Connection conn = null;
        CallableStatement callSt = null;
        try {
            conn = ConnectionDB.openConnection();
            conn.setAutoCommit(false);
            if (conn != null) {
                callSt = conn.prepareCall("{call bank_transfer(?,?,?,?,?,?,?)}");
                callSt.setInt(1,1);
                callSt.setString(2,"Nguyễn Văn A");
                callSt.setString(3,"MB");
                callSt.setInt(4,2);
                callSt.setString(5,"Nguyễn Văn B");
                callSt.setString(6,"Agribank");
                callSt.setBigDecimal(7,new java.math.BigDecimal(500.00));
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
