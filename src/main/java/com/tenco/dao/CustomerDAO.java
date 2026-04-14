package com.tenco.dao;

import com.mysql.cj.protocol.Resultset;
import com.tenco.dto.Customer;
import com.tenco.dto.Movies;
import com.tenco.dto.Room;
import com.tenco.dto.Seat;
import com.tenco.util.DBConnectionManager;

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
    public Boolean reserve(Seat seat, Customer customer, Movies movies, int seatNumber) throws SQLException {
        Connection conn = null;

        conn.setAutoCommit(false);

        // 트랜잭션 시작
        try {
            // 1. findByTitle(String title) - select 제목으로 영화 존재여부 확인
            if (movies == null) {
                return false;
            }

            if (movies.getRoomId() <= 0) {
                return false;
            }

            List<Seat> seatList = SeatDAO.findAll();

            /**
             * room_id 는 PK값
             */
            String seatSelectSql = """
                    select * from seat where seat_number = ? and room_id = ?
                    """;
            conn = DBConnectionManager.getConnection();
            try (PreparedStatement seatSelectPstmt = conn.prepareStatement(seatSelectSql)) {
                seatSelectPstmt.setInt(1, seatNumber);
                seatSelectPstmt.setInt(2, movies.getRoomId());
                ResultSet resultSet = seatSelectPstmt.executeQuery();
                if (!resultSet.next()) {
                    return false;
                }
            }


            // 3. 예약 목록(reservation테이블에 예약정보)에 추가 - insert
            String reservationSql = """
                    insert into reservation(customer_id, movie_id, room_id, seat_id)
                    values(?,?,?,?)
                    """;


            try (PreparedStatement reservePstmt = conn.prepareStatement(reservationSql)) {
                reservePstmt.setInt(1, customer.getId() );
                reservePstmt.setInt(2,movies.getId());
                reservePstmt.setInt(3,movies.getRoomId());
                reservePstmt.setInt(4,seat.getId());
                reservePstmt.executeUpdate();
            }


            // 4. 좌석 이용 가능 여부 변경 - update
            String sql = """
                    update seat
                    set is_available = false 
                    where seat_number = ?
                    and is_available = true
                    """;
            try (PreparedStatement seatUpdatePstmt = conn.prepareStatement(sql)) {
                seatUpdatePstmt.setInt(1,seatNumber);
                seatUpdatePstmt.executeUpdate();

            }

            conn.commit();
            return true;


        } catch (SQLException e) {
            conn.rollback();
            return false;
        } finally {
            conn.setAutoCommit(true);
            conn.close();
        }

    }

    public static void main(String[] args) {
        CustomerDAO customerDAO = new CustomerDAO();
        Customer customer = Customer.builder()
                .id(100)
                .email("aaa@naver.com")
                .name("김다똥")
                .password("1234")
                .age(33)
                .createAt(LocalDate.now())
                .isAvailable(true)
                .build();

        try {
//            Customer customer = customerDAO.login("user1@test.com","1234");
//            System.out.println(customer.getName());
            customerDAO.signup(customer);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
