package com.tenco.service;

import com.tenco.dao.*;
import com.tenco.dto.*;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ReserveService {

    private final ReservationDAO reservationDAO = new ReservationDAO();
    private final MoviesDAO moviesDAO = new MoviesDAO();
    private final RoomDAO roomDAO = new RoomDAO();
    private final SeatDAO seatDAO = new SeatDAO();
    private static final CustomerDAO customerDAO = new CustomerDAO();
    private static final AdminDAO adminDAO = new AdminDAO();

    private User user; // 현재 로그인한 사용자

    // 회원가입
    public Customer signUp(Customer customer) throws SQLException {
        return customerDAO.signup(customer);
    }

    public User login(String id, String password) throws SQLException {

        if (user == null) {

            // 1. 관리자 먼저 조회
            Admin admin = adminDAO.login(id, password);
            if (admin != null) {
                System.out.println("관리자 로그인");
                user = admin;
                return user;
            }

            // 2. 고객 조회
            Customer customer = customerDAO.login(id, password);
            if (customer != null) {
                System.out.println("일반 사용자 로그인");
                user = customer;
                return user;
            }

            System.out.println("로그인 실패");
            return null;
        }

        System.out.println("이미 로그인된 사용자입니다.");
        return user;
    }

    // 로그아웃
    public User logout() {
        if (user == null) {
            System.out.println("먼저 로그인부터 해주세요.");
            return null;
        }

        System.out.println("로그아웃 되었습니다.");
        user = null;
        return null;
    }

    // 예약
    public Boolean reserve(Seat seat, Customer customer, Movies movies, Room room, int seatNumber)
            throws SQLException {
        return customerDAO.reserve(seat, customer, movies, room, seatNumber);
    }

    // 전체 영화 목록 조회
    public List<Movies> allMovieList() throws SQLException {
        List<Movies> moviesList = MoviesDAO.findAll();

        if (moviesList.isEmpty()) {
            System.out.println("등록된 영화가 없습니다.");
            return new ArrayList<>();
        }

        return moviesList;
    }

    // 영화 검색
    public List<Movies> findByMovieTitle(String title) throws SQLException {
        List<Movies> moviesList = MoviesDAO.findByMovies(title);

        if (moviesList.isEmpty()) {
            System.out.println("해당 제목의 영화는 존재하지 않습니다.");
        }

        return moviesList;
    }

    // 영화 등록
    public Boolean insert(Movies movies) throws SQLException {
        if (MoviesDAO.insert(movies)) {
            System.out.println("영화가 추가되었습니다. 제목: " + movies.getTitle());
            return true;
        }

        return false;
    }

    // 영화 수정
    public void movieUpdate(Movies movies) throws SQLException {
        moviesDAO.update(movies);
        System.out.println("영화가 수정되었습니다. 수정된 영화: " + movies.getTitle());
    }

    // 영화 삭제
    public void movieDelete(Movies movies) throws SQLException {
        if (moviesDAO.softDelete(movies)) {
            System.out.println("영화가 소프트 삭제되었습니다.");
            System.out.println("삭제된 영화 제목: " + movies.getTitle());
        } else {
            System.out.println("해당 영화는 존재하지 않습니다.");
        }
    }



    // 결제 안 된 예약 조회
    public List<Reservation> isPayed() throws SQLException {
        List<Reservation> reservationList = reservationDAO.isPayment();
        List<Reservation> failPayed = new ArrayList<>();

        for (Reservation reservation : reservationList) {
            if (!reservation.isStatus()) {
                failPayed.add(reservation);
            }
        }

        System.out.println("결제가 안 된 고객 리스트: " + failPayed);
        return failPayed;
    }

    // 상영관 조회
    public List<Movies> findAllRooms() {
        return roomDAO.findAll();
    }

    // 내 좌석 조회
    public void findMySeat() {
        // TODO: 구현 필요
    }

    public List<Seat> findAllSeatsByRoom(Room room) throws SQLException {
        SeatDAO seatDAO = new SeatDAO();
        return seatDAO.findAll(room);
    }

    // 영화 예약
    public Boolean reserveMovie(Seat seat, Customer customer, Movies movies, Room room, int seatNumber)
            throws SQLException {
        return customerDAO.reserve(seat, customer, movies, room, seatNumber);
    }

    // 특정 상영관 좌석 목록
    public List<Seat> findSeatsByRoomNumber(int roomNumber) throws SQLException {
        List<Seat> seatList = seatDAO.findByRoomNumber(roomNumber);

        if (seatList.isEmpty()) {
            System.out.println("해당 상영관의 좌석이 없습니다.");
        }

        return seatList;
    }

    public Room findRoomByMovie(Movies movie) throws SQLException {
        List<Movies> roomMovies = findAllRooms();

        if (roomMovies == null || roomMovies.isEmpty()) {
            return null;
        }

        for (Movies roomMovie : roomMovies) {
            if (roomMovie.getTitle() != null
                    && movie.getTitle() != null
                    && roomMovie.getTitle().trim().equals(movie.getTitle().trim())) {

                return Room.builder()
                        .id(roomMovie.getId())
                        .roomNumber(roomMovie.getId())
                        .movieId(movie.getId())
                        .isAvailable(roomMovie.isAvailable())
                        .build();
            }
        }

        return null;
    }

}