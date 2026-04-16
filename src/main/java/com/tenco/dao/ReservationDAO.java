package com.tenco.dao;

import com.tenco.dto.Customer;
import com.tenco.dto.Reservation;
import com.tenco.util.DBConnectionManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReservationDAO {

    // isLoggedIn()로그인 상태 확인

    public Customer isLoggedIn(String email, String password) throws SQLException {
        String logInSql = """
                SELECT *
                FROM customer
                WHERE email = ? AND password = ? AND is_available = true
                """;
        try (Connection conn = DBConnectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(logInSql)) {
            pstmt.setString(1, email);
            pstmt.setString(2, password);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Customer.builder()
                            .id(rs.getInt("id"))
                            .email(rs.getString("email"))
                            .name(rs.getString("name"))
                            .age(rs.getInt("age"))
                            .build();
                }
            }
            return null;
        }
    }

    // ispayment()	boolean	전체 결제 상태
    public List<Reservation> isPayment() throws SQLException {
        List<Reservation> list = new ArrayList<>();
        String paySql = """
                SELECT
                    rs.id AS 예약번호,
                    c.name AS 고객이름,
                    m.title AS 영화제목,
                    rs.is_available AS 결제상태
                FROM reservation rs
                INNER JOIN customer c ON rs.customer_id = c.id
                INNER JOIN movies m ON rs.movie_id = m.id
                """;
        try (Connection conn = DBConnectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(paySql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                Reservation reservation = Reservation.builder()
                        .id(rs.getInt("예약번호"))
                        .customerName(rs.getString("고객이름"))
                        .movieName(rs.getString("영화제목"))
                        .isStatus(rs.getBoolean("결제상태"))
                        .build();
                list.add(reservation);
            }
        }
        return list;
    }

    // findAll()	List<reserve>	전체 예약 목록 조회
    public List<Reservation> findAll() throws SQLException {
        List<Reservation> reservationList = new ArrayList<>();
        String resSql = """
                SELECT
                	rs.id AS 예약번호,
                	c.name AS 고객이름,
                    m.title AS 영화제목,
                    r.id AS 영화관번호,
                    s.seat_number AS 좌석번호
                FROM reservation rs
                INNER JOIN customer c ON rs.customer_id = c.id
                INNER JOIN movies m ON rs.movie_id = m.id
                INNER JOIN room r ON rs.room_id = r.id
                INNER JOIN seat s ON rs.seat_id = s.id
                WHERE rs.is_available = true
                """;
        try (Connection conn = DBConnectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(resSql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                Reservation reservation = Reservation.builder()
                        .id(rs.getInt("예약번호"))
                        .customerName(rs.getString("고객이름"))
                        .movieName(rs.getString("영화제목"))
                        .roomNumber(rs.getInt("영화관번호"))
                        .seat(rs.getInt("좌석번호"))
                        .build();
                reservationList.add(reservation);

            }
        }
        return reservationList;
    }

    // Insert(reserve)	boolean	예약 목록 추가
    public boolean insert(Reservation reservation) throws SQLException {
        String insSql = """
                insert into reservation(customer_id, movie_id,room_id,seat_id,is_available) values
                (?,?,?,?,?)
                """;
        try (Connection conn = DBConnectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(insSql)) {
            pstmt.setInt(1, reservation.getCustomerId());
            pstmt.setInt(2, reservation.getMovieId());
            pstmt.setInt(3, reservation.getRoomId());
            pstmt.setInt(4, reservation.getSeatId());
            pstmt.setBoolean(5, reservation.isStatus());
            return pstmt.executeUpdate() > 0;
        }
    }

    // SoftDelete()	boolean	소프트삭제
    public boolean softDelete(int id) throws SQLException {
        String deleteSql = """
                UPDATE reservation
                SET is_available = false
                WHERE id = ?
                """;

        try (Connection conn = DBConnectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(deleteSql)) {
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        }
    }
}
