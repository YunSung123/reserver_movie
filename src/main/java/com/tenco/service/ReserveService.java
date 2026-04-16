package com.tenco.service;

import com.tenco.dao.*;
import com.tenco.dto.*;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ReserveService {
    ReservationDAO reservationDAO = new ReservationDAO();
    MoviesDAO moviesDAO = new MoviesDAO();
    RoomDAO roomDAO = new RoomDAO();
    SeatDAO seatDAO = new SeatDAO();
    static CustomerDAO customerDAO = new CustomerDAO();
    Customer customer;

    // 로그인 여부


    //회원가입
    public Customer signUp(Customer customer) throws SQLException {

        return customerDAO.signup(customer);

    }

    // 로그인
    public Customer login(String email, String password) throws SQLException {
        // 로그인 상태 확인용
        if (customer == null) {
            customer = customerDAO.login(email, password);
            return customerDAO.login(email, password);
        } else {
            return customer;
        }

    }

    // 로그아웃
    public Customer logout() {
        if(customer == null){
            System.out.println("먼저 로그인 부터 해주세요.");
            return customer;
        }
        if (customer != null) {
            System.out.println("로그아웃 되었습니다.");
            customer = null;
            return customer;
        }
        return customer;
    }

    // 예약기능(좌석조회, 상영관 )

    public Boolean reserve(Seat seat, Customer customer, Movies movies, Room room, int seatNumber) throws SQLException {

        return customerDAO.reserve(seat, customer, movies, room, seatNumber);

    }

    //전체 영화목록 조회

    public List<Movies> allmovieList() throws SQLException {
        List<Movies> moviesList = new ArrayList<>();
        moviesList = MoviesDAO.findAll();
        if (moviesList.isEmpty()) {
            System.out.println("등록된 영화가 없습니다");
            return null;
        } else {
            return moviesList;
        }

    }

    //영화검색
    public List<Movies> findByMovieTitle(String title) throws SQLException {
        List<Movies> moviesList = new ArrayList<>();
        moviesList = MoviesDAO.findByMovies(title);
        if (moviesList.isEmpty()) {
            System.out.println("해당제목의 영화는 존재하지 않습니다.");
        }

        return moviesList;
    }

    // 영화 등록
    public Boolean insert(Movies movies) throws SQLException {

        if (MoviesDAO.insert(movies)) {
            System.out.println("영화가 추가되었습니다. 제목: " + movies.getTitle());
            return true;
        } else {

            return false;

        }

    }

    // 영화수정
    public void movieUpdate(Movies movies) throws SQLException {

        moviesDAO.update(movies);
        System.out.println("영화가 수정되었습니다. 수정된 영화: " + movies.getTitle());


    }

    // 영화삭제
    public void movieDelete(Movies movies) throws SQLException {
        if (moviesDAO.softDelete(movies)) {
            System.out.println("영화가 소프트 삭제 되었습니다.");
            System.out.println("삭제된 영화 제목: " + movies.getTitle());
        } else {
            System.out.println("해당영화는 존재하지 않습니다.");
        }
    }

    //    상영관조회 추가 설명 필요

    //    좌석조회 현재 상영관에서 사용가능한 좌석 조회
    public List<Seat> findSeat() {
        List<Seat> seatList = new ArrayList<>();
        seatList = seatDAO.findAll();
        return seatList;
    }

//    결제확인

    public List<Reservation> isPayed() throws SQLException {

        List<Reservation> reservationList = new ArrayList<>();
        List<Reservation> failPayed = new ArrayList<>(); // 결제 실패 고객

        reservationList = reservationDAO.isPayment();


        for (int i = 0; i < reservationList.size(); i++) {
            if (reservationList.get(i).isStatus() == false) {
                failPayed.add(reservationList.get(i));
            }
        }

        System.out.println("결제가 안된 고객 리스트: " + failPayed);
        return failPayed;
    }

//    예약번호조회


//    나의 좌석번호 조회

    public void findMySeat(){

    }


    public static void main(String[] args) {

        ReserveService service = new ReserveService();
//            Customer customer = service.login("user1@test.com","1234");
//            service.logout();
//            System.out.println(service.allmovieList());
//            System.out.println(service.findByMovieTitle("어벤져스:"));
//            Movies movies = Movies
//                .builder()
//                .id(4)
//                .title("가으루흐")
//                .grade("123")
//                .price(new BigDecimal(20000))
//                .viewCount(0)
//                .build();
//            service.movieUpdate(movies);
//            service.insert(movies);
//            service.movieDelete(movies);
//            String id = "user1@test.com";
//            String password = "1234";
//            service.login(id,password);
//        try {
//            service.isPayed();
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        }

    }

    // 좌석 목록
    public List<Seat> findSeatsByRoomNumber(int roomNumber) throws SQLException {
        List<Seat> seatList = seatDAO.findByRoomNumber(roomNumber);
        if (seatList.isEmpty()) {
            System.out.println("해당 상영관의 좌석이 없습니다.");
        }
        return seatList;
    }
}
