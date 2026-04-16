package com.tenco.dao;

import com.mysql.cj.protocol.Resultset;
import com.tenco.dto.Customer;
import com.tenco.dto.Movies;
import com.tenco.dto.Room;
import com.tenco.dto.Seat;
import com.tenco.util.DBConnectionManager;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class CustomerDAO {
    // 로그인 인증 (Customer id + password)
    public Customer login(String customerId, String password) throws SQLException {
        Connection conn = null;
        String sql = """
                SELECT * FROM customer WHERE email = ? and password = ?
                """;
        conn = DBConnectionManager.getConnection();
        Customer customer = null;

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, customerId);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                customer = Customer.builder()
                        .id(rs.getInt("id"))
                        .email(rs.getString("email"))
                        .password(rs.getString("password"))
                        .name(rs.getString("name"))
                        .age(rs.getInt("age"))
                        .createAt(rs.getDate("create_at").toLocalDate())
                        .isAvailable(rs.getBoolean("is_available"))
                        .build();
                System.out.println("로그인됨");
                return customer;
            } else {
                return null;
            }
        }
    }

    // 회원가입
    public Customer signup(Customer customer) throws SQLException {
        System.out.println("회원가입");
        String sql = """
                insert into customer (email,password,name,age) 
                values (?,?,?,?)
                """;
        try (Connection conn = DBConnectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            //pstmt.setInt(1,customer.getId());
            pstmt.setString(1, customer.getEmail());
            pstmt.setString(2, customer.getPassword());
            pstmt.setString(3, customer.getName());
            pstmt.setInt(4, customer.getAge());
            pstmt.executeUpdate();
            System.out.println("가입완료");
            return customer;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            ;
        }
        return null;

    }

    // 영화 예약 (영화제목으로 받음)
    public Boolean reserve(Seat seat, Customer customer, Movies movies, Room room, int seatNumber) throws SQLException {

        Connection conn = null;

        try {
            conn = DBConnectionManager.getConnection();
            conn.setAutoCommit(false);

            System.out.println("=== customerDAO.reserve() 시작 ===");
            System.out.println("[DAO] customer = " + customer);
            System.out.println("[DAO] movies   = " + movies);
            System.out.println("[DAO] room     = " + room);
            System.out.println("[DAO] seat     = " + seat);
            System.out.println("[DAO] seatNo   = " + seatNumber);

            // 1. 기본 검증
            if (customer == null || movies == null || room == null || seat == null) {
                System.out.println("[DAO] false - null 객체 존재");
                return false;
            }

            if (customer.getId() <= 0 || movies.getId() <= 0 || room.getId() <= 0) {
                System.out.println("[DAO] false - id 값 비정상");
                return false;
            }

            // 2. 좌석 존재 및 사용 가능 여부 확인
            String seatSelectSql = """
                select id, room_id, seat_number, is_available
                from seat
                where seat_number = ?
                  and room_id = ?
                  and is_available = true
                """;

            try (PreparedStatement seatSelectPstmt = conn.prepareStatement(seatSelectSql)) {
                seatSelectPstmt.setInt(1, seatNumber);
                seatSelectPstmt.setInt(2, room.getId());

                System.out.println("[DAO] seat 조회 조건: seat_number = " + seatNumber + ", room_id = " + room.getId());

                try (ResultSet rs = seatSelectPstmt.executeQuery()) {
                    if (!rs.next()) {
                        System.out.println("[DAO] false - 좌석 조회 실패");
                        System.out.println("[DAO] seat 테이블에 해당 room_id / seat_number 조합이 없거나 이미 예약됨");
                        conn.rollback();
                        return false;
                    }

                    int dbSeatId = rs.getInt("id");
                    int dbRoomId = rs.getInt("room_id");
                    int dbSeatNumber = rs.getInt("seat_number");
                    boolean dbAvailable = rs.getBoolean("is_available");

                    seat.setId(dbSeatId);
                    seat.setRoomId(dbRoomId);
                    seat.setSeatNumber(dbSeatNumber);
                    seat.setAvailable(dbAvailable);

                    System.out.println("[DAO] seat 조회 성공: id=" + dbSeatId
                            + ", room_id=" + dbRoomId
                            + ", seat_number=" + dbSeatNumber
                            + ", is_available=" + dbAvailable);
                }
            }

            // 3. 이미 같은 좌석이 reservation에 들어가 있는지 확인
            String duplicateCheckSql = """
                select count(*)
                from reservation
                where room_id = ?
                  and seat_id = ?
                """;

            try (PreparedStatement dupPstmt = conn.prepareStatement(duplicateCheckSql)) {
                dupPstmt.setInt(1, room.getId());
                dupPstmt.setInt(2, seat.getId());

                try (ResultSet rs = dupPstmt.executeQuery()) {
                    if (rs.next()) {
                        int count = rs.getInt(1);
                        System.out.println("[DAO] reservation 중복 count = " + count);
                        if (count > 0) {
                            System.out.println("[DAO] false - 이미 reservation에 존재");
                            conn.rollback();
                            return false;
                        }
                    }
                }
            }

            // 4. reservation insert
            String reservationSql = """
                insert into reservation(customer_id, movie_id, room_id, seat_id)
                values (?, ?, ?, ?)
                """;

            try (PreparedStatement reservePstmt = conn.prepareStatement(reservationSql)) {
                reservePstmt.setInt(1, customer.getId());
                reservePstmt.setInt(2, movies.getId());
                reservePstmt.setInt(3, room.getId());
                reservePstmt.setInt(4, seat.getId());

                int insertCount = reservePstmt.executeUpdate();
                System.out.println("[DAO] reservation insert 결과 = " + insertCount);

                if (insertCount <= 0) {
                    System.out.println("[DAO] false - reservation insert 실패");
                    conn.rollback();
                    return false;
                }
            }

            // 5. 좌석 사용 불가 처리
            String seatUpdateSql = """
                update seat
                set is_available = false
                where id = ?
                  and room_id = ?
                  and is_available = true
                """;

            try (PreparedStatement seatUpdatePstmt = conn.prepareStatement(seatUpdateSql)) {
                seatUpdatePstmt.setInt(1, seat.getId());
                seatUpdatePstmt.setInt(2, room.getId());

                int updateCount = seatUpdatePstmt.executeUpdate();
                System.out.println("[DAO] seat update 결과 = " + updateCount);

                if (updateCount <= 0) {
                    System.out.println("[DAO] false - seat update 실패");
                    conn.rollback();
                    return false;
                }
            }

            conn.commit();
            System.out.println("[DAO] true - 예약 성공");
            return true;

        } catch (Exception e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ignored) {
                }
            }
            System.out.println("[DAO] 예외 발생");
            e.printStackTrace();
            return false;

        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException ignored) {
                }
            }
        }
    }

}
