package com.tenco.view;


import com.tenco.dao.SeatDAO;
import com.tenco.dto.Customer;
import com.tenco.dto.Movies;
import com.tenco.dto.Room;
import com.tenco.dto.Seat;
import com.tenco.service.ReserveService;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ReserveView {
    private final ReserveService service = new ReserveService();
    private final Scanner sc = new Scanner(System.in);

    Customer customer;
    private Integer currentCustomerId = null; // 로그인 중인 고객 아이디
    private String currentCustomerName = null; // 로그인 중인 고객 이름

    public void start() {
        System.out.println("⚝⚝⚝영화예약관리시스템⚝⚝⚝");


        try {

            while (true) {
                printMenu();
                System.out.print("선택:");
                int choice = sc.nextInt();
                switch (choice) {
                    case 1:
                        login();
                        break;
                    case 2:
                        logout();
                        break;
                    case 3:
                        addCustomer();
                        break;
                    case 4:
                        findAllMovies();
                        break;
                    case 5:
                        searchMv();
                        break;
                    case 6:
                        findAllRooms();
                        break;
                    case 8:
                        reserve();
                        break;
                }
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void printMenu() {
        System.out.println("\n⚝⚝⚝영화예약관리시스템⚝⚝⚝");
        if (currentCustomerId == null) {
            System.out.println("❗ 로그아웃 상태 ❗");
        } else {
            System.out.println("❗ 로그인: " + currentCustomerName + " ❗");
        }
        System.out.println("\uD83C\uDF7F\uD83C\uDF7F\uD83C\uDF7F\uD83C\uDF7F\uD83C\uDF7F");
        System.out.println("0. 종료");
        System.out.println("1. 로그인");
        System.out.println("2. 로그아웃");
        System.out.println("3. 회원가입");
        System.out.println("4. 전체영화조회");
        System.out.println("5. 영화 조회 ");
        System.out.println("6. 상영관 조회");
        System.out.println("7. 좌석 조회");
        System.out.println("8. 영화 예약하기");
        System.out.println("9. 결제 확인");
        System.out.println("10. 예약 번호 조회");
        System.out.println("11. 나의 좌석번호 조회");

    }

    // 로그인
    private void login() throws SQLException {
        if (currentCustomerId != null) {
            System.out.println("현재 로그인 상태 입니다.");
            return;
        }

        System.out.print("이메일을 입력하세요.");
        String email = sc.next();
        System.out.print("비밀번호를 입력하세요.");
        String pw = sc.next();


        customer = service.login(email, pw);
        currentCustomerId = customer.getId();
        currentCustomerName = customer.getName();
    }

    // 로그아웃
    private void logout() {
        if (currentCustomerId == null) {
            System.out.println("현재 로그인 상태가 아닙니다.");
        } else {
            System.out.println(currentCustomerName + "님이 로그아웃 되었습니다.");
            currentCustomerId = null;
            currentCustomerName = null;
        }
    }

    // 회원가입
    private void addCustomer() throws SQLException {
        System.out.print("이름: ");
        String name = sc.next().trim();
        if (name.isEmpty()) {
            System.out.println("이름은 필수 입니다.");
            return;
        }

        System.out.print("나이: ");
        int age = sc.nextInt();
        if (age <= 0) {
            System.out.println("잘못된 입력입니다. 1부터 입력가능");
            return;
        }

        System.out.print("비밀번호: ");
        String pw = sc.next().trim();
        if (pw.isEmpty()) {
            System.out.println("비밀번호는 필수 입니다.");
            return;
        }

        System.out.print("이메일: ");
        String email = sc.next();
        if (email.isEmpty()) {
            System.out.println("이메일은 필수 입니다.");
            return;
        }

        service.signUp(Customer.builder().name(name).age(age).password(pw).email(email).build());
        System.out.println("회원가입 완료");
    }

    // 영화 목록 조회
    private void findAllMovies() throws SQLException {
        System.out.println("\n⚝⚝⚝영화 목록⚝⚝⚝");
        List<Movies> moviesList = service.allMovieList();
        if (moviesList.isEmpty()) {
            System.out.println("영화가 존재하지 않습니다");
            return;
        }
        for (Movies m : moviesList) {
            System.out.println("제목: " + m.getTitle() + " | 등급: " + m.getGrade() +
                    " | 가격: " + m.getPrice() + " | 이용가능여부: " + (m.isAvailable() ? "이용가능" : "이용 불가능"));
        }
    }

    // 영화조회
    private void searchMv() throws SQLException {
        System.out.print("검색할 영화제목: ");
        String MvTitle = sc.next().trim();
        if (MvTitle.isEmpty()) {
            System.out.println("검색어를 입력해주세요: ");
            return;
        }

        List<Movies> moviesList = service.findByMovieTitle(MvTitle);
        if (moviesList.isEmpty()) {
            System.out.println("검색하신 영화가 없습니다.");
            return;
        }
        System.out.println("\n⚝⚝⚝검색결과⚝⚝⚝");
        for (Movies m : moviesList) {
            System.out.println("제목: " + m.getTitle() + " | 등급: " + m.getGrade() +
                    " | 가격: " + m.getPrice() + " | 이용가능여부: " + (m.isAvailable() ? "이용가능" : "이용 불가능"));
        }

    }

    private void findAllRooms() {
        List<Movies> moviesList = service.findAllRooms();

        if (moviesList.isEmpty()) {
            System.out.println("헌재 상영관에 영화가 개봉되지 않았습니다");
        }
        for (Movies m : moviesList) {
            System.out.println("상영관: " + m.getId() + " | 제목: " + m.getTitle());
        }

    }

    private void reserve() throws SQLException {
        System.out.print("어떤 영화를 보실지 입력해 주세요: ");
        String title = sc.next().trim();
        Movies movies = new Movies();
        while (true) {
            List<Movies> moviesList = service.findByMovieTitle(title);
            if (moviesList.isEmpty()) {
                System.out.println("영화가 존재하지 않습니다.");
                break;
            } else if (moviesList.size() >= 2) {
                System.out.println("정확한 영화이름을 입력해주세요: ");
            } else {
                movies = moviesList.get(0);
                break;
            }
        }
        List<Movies> roomMoives = service.findAllRooms();

        Room room;
        for (int i = 0; i < roomMoives.size(); i++) {
            if (roomMoives.get(i).getTitle().trim().equals(movies.getTitle().trim())) {
                room = Room.builder()
                        .id(roomMoives.get(i).getId())
                        .movieId(movies.getId())
                        .isAvailable(roomMoives.get(i).isAvailable())
                        .build();
                SeatDAO seatDAO = new SeatDAO();
                List<Seat> seatList = seatDAO.findAll(room);
                System.out.print("좌석을 선택해주세요: ");
                int seatNumber = sc.nextInt();
                Seat seat = Seat.builder()
                        .seatNumber(seatNumber)
                        .roomId(room.getId())
                        .build();
                System.out.println(service.reserve(seat,customer,movies,room,seatNumber));

            }

        }

    }
}
