package com.tenco.dao;

import com.tenco.dto.Seat;
import com.tenco.util.DBConnectionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SeatDAO {
    // 현재 좌석 정보
    public List<Seat> findAll() {
        List<Seat> seatList = new ArrayList<>();
        String sql = """
                    SELECT * FROM seat WHERE is_available = true
                """;
        try (Connection conn = DBConnectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery();
        ) {
            while (rs.next()) {

                seatList.add(mapToSeat(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return seatList;
    }

    // 이용(예약) 가능 여부 변경
    public boolean useStatus(Seat seat){
        List<Seat> seatList = new ArrayList<>();
        String sql = """
                    SELECT * FROM seat WHERE id = ?
                """;
        try (Connection conn = DBConnectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, seat.getId());
            int rs = pstmt.executeUpdate();
            return rs > 0;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Seat mapToSeat(ResultSet rs) throws SQLException {
        return Seat.builder()
                .id(rs.getInt("id"))
                .roomNumber(rs.getInt("room_Number"))
                .seatNumber(rs.getInt("seat_Number"))
                .isAvailable(rs.getBoolean("is_available"))
                .build();
    }

    // 해당 상영관 자리 확인
    public List<Seat> findByRoomNumber(int roomNumber) {
        List<Seat> seatList = new ArrayList<>();
        String sql = """
                SELECT id, room_id, seat_number, is_available
                FROM seat
                WHERE room_id = ?
                ORDER BY seat_number
            """;
        try (Connection conn = DBConnectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, roomNumber);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Seat seat = Seat.builder()
                            .id(rs.getInt("id"))
                            .roomNumber(rs.getInt("room_id"))
                            .seatNumber(rs.getInt("seat_number"))
                            .isAvailable(rs.getBoolean("is_available"))
                            .build();

                    seatList.add(seat);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return seatList;
    }
}
