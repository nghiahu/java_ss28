package Baitap.Bai9;

import Baitap.ConnectionDB;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;

public class Main {
    public static void main(String[] args) throws SQLException {
        Connection conn = null;
        CallableStatement checkBalance = null;
        CallableStatement getHighestBid = null;
        CallableStatement placeBid = null;
        CallableStatement logFail = null;

        try {
            conn = ConnectionDB.openConnection();
            conn.setAutoCommit(false);

            boolean enoughBalance = false;
            checkBalance = conn.prepareCall("{call check_user_balance(?, ?, ?)}");
            checkBalance.setInt(1, 1);
            checkBalance.setDouble(2, 150);
            checkBalance.registerOutParameter(3, Types.BOOLEAN);
            checkBalance.execute();
            enoughBalance = checkBalance.getBoolean(3);

            if (!enoughBalance) {
                logFail = conn.prepareCall("{call log_failed_bid(?, ?, ?)}");
                logFail.setInt(1, 1);
                logFail.setInt(2, 101);
                logFail.setString(3, "Số dư không đủ");
                logFail.execute();
                conn.rollback();
                System.out.println("Đặt giá thất bại: không đủ số dư.");
                return;
            }

            double currentHighest = 0;
            getHighestBid = conn.prepareCall("{call get_current_highest_bid(?, ?)}");
            getHighestBid.setInt(1, 101);
            getHighestBid.registerOutParameter(2, Types.DECIMAL);
            getHighestBid.execute();
            currentHighest = getHighestBid.getDouble(2);

            if (150 <= currentHighest) {
                logFail = conn.prepareCall("{call log_failed_bid(?, ?, ?)}");
                logFail.setInt(1, 1);
                logFail.setInt(2, 101);
                logFail.setString(3, "Giá đặt thấp hơn giá hiện tại");
                logFail.execute();
                conn.rollback();
                System.out.println("Đặt giá thất bại: giá không hợp lệ.");
                return;
            }

            placeBid = conn.prepareCall("{call place_bid(?, ?, ?)}");
            placeBid.setInt(1, 101);
            placeBid.setInt(2, 1);
            placeBid.setDouble(3, 150);
            placeBid.execute();

            conn.commit();
            System.out.println("Đặt giá thành công!");

        } catch (SQLException e) {
            System.out.println("Lỗi trong quá trình đặt giá: " + e.getMessage());
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        } finally {
            conn.close();
        }
    }
}
