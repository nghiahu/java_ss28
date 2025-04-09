package Baitap.Bai1;

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
                boolean autoCommit = conn.getAutoCommit();
                System.out.println("Trạng thái autoCommit ban đầu: " + autoCommit);
                conn.setAutoCommit(false);
                callSt = conn.prepareCall("{call create_user(?,?)}");
                callSt.setString(1,"Nguyễn Văn A");
                callSt.setString(2,"nguyenvana@gmail.com");
                callSt.execute();
                conn.commit();
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            ConnectionDB.closeConnection(conn, callSt);
        }
    }
}
