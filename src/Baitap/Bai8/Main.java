package Baitap.Bai8;

import Baitap.ConnectionDB;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;

public class Main {
    public static void main(String[] args) throws SQLException {
        Connection conn = null;
        CallableStatement checkRoom = null;
        CallableStatement markUnavailable = null;
        CallableStatement createBooking = null;
        CallableStatement logFailed = null;
        try {
            conn = ConnectionDB.openConnection();
            conn.setAutoCommit(false);
            boolean isAvailable = false;
            checkRoom = conn.prepareCall("{call check_room_availability(?, ?)}");
            checkRoom.setInt(1, 1);
            checkRoom.registerOutParameter(2, Types.BOOLEAN);
            checkRoom.execute();
            isAvailable = checkRoom.getBoolean(2);

            if (isAvailable) {
                markUnavailable = conn.prepareCall("{call mark_room_unavailable(?)}");
                markUnavailable.setInt(1, 101);
                markUnavailable.execute();
                createBooking = conn.prepareCall("{call create_booking(?, ?, ?)}");
                createBooking.setInt(1, 1);
                createBooking.setInt(2, 101);
                createBooking.setString(3, "Đã đặt");
                createBooking.execute();

                conn.commit();
                System.out.println("Đặt phòng thành công!");
            } else {
                logFailed = conn.prepareCall("{call log_failed_booking(?, ?, ?)}");
                logFailed.setString(1, "Nguyễn Văn A");
                logFailed.setInt(2, 101);
                logFailed.setString(3, "Phòng đã được đặt");
                logFailed.execute();

                conn.rollback();
                System.out.println("Phòng đã được đặt");
            }
        }catch (SQLException e){
            conn.rollback();
            System.out.println(e.getMessage());
        }catch (Exception e){
            System.out.println(e.getMessage());
        }finally {
            conn.close();
        }
    }
}
