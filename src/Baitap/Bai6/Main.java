package Baitap.Bai6;

import Baitap.ConnectionDB;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;

public class Main {
    public static void main(String[] args) {
        Connection conn = null;
        CallableStatement callSt = null;
        try {
            conn.setAutoCommit(false);

            callSt = conn.prepareCall("{call create_department(?, ?)}");
            callSt.setString(1, "Phòng Nhân sự");
            callSt.registerOutParameter(2, Types.INTEGER);
            callSt.execute();
            int departmentId = callSt.getInt(2);

            callSt = conn.prepareCall("{call add_employee(?, ?, ?)}");
            callSt.setInt(1, 201);
            callSt.setString(2, "Nguyễn Văn A");
            callSt.setInt(3, departmentId);
            callSt.execute();

            callSt.setInt(1, 202);
            callSt.setString(2, "Trần Thị B");
            callSt.setInt(3, departmentId);
            callSt.execute();

            conn.commit();
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
        }catch (Exception e) {
            System.out.println(e.getMessage());
        }finally {
            ConnectionDB.closeConnection(conn, callSt);
        }
    }
}
