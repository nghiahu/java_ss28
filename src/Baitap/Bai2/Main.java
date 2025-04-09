package Baitap.Bai2;

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
            if (conn != null) {
                conn.setAutoCommit(false);
                callSt = conn.prepareCall("{call create_user(?,?)}");
                callSt.setString(1,"Nguyễn Văn B");
                callSt.setString(2,"nguyenvanb@gmail.com");
                callSt.execute();
                callSt = conn.prepareCall("{call update_user(?,?)}");
                callSt.setString(1,"Nguyễn Văn C");
                callSt.setString(2,"nguyenvanb@gmail.com");
                callSt.execute();
                conn.commit();
                System.out.println("Thêm dữ liệu thành công");
            }
        } catch (SQLException e) {
            System.out.println("Có lỗi xảy ra thực hiện rollback");
            System.out.println(e.getMessage());
            try {
                if (conn != null) {
                    conn.rollback();
                }
            } catch (SQLException rollbackEx) {
                rollbackEx.printStackTrace();
            }
        } finally {
            ConnectionDB.closeConnection(conn, callSt);
        }
    }
}
