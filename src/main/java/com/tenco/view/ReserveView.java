package com.tenco.view;


import com.tenco.dto.Customer;
import com.tenco.dto.Movies;
import com.tenco.service.ReserveService;

import java.sql.SQLException;
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

        printMenu();
        int choice = sc.nextInt();
        try {
            switch (choice) {
                case 1:
                    login();
                    break;
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
        System.out.println("4. 영화 조회 ");
        System.out.println("5. 상영관 조회");
        System.out.println("6. 좌석 조회");
        System.out.println("7. 결제 확인");
        System.out.println("8. 예약 번호 조회");
        System.out.println("9. 나의 좌석번호 조회");

    }

    // 로그인
    private void login() throws SQLException {
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
        String name = sc.nextLine().trim();
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
        String pw = sc.nextLine().trim();
        if (pw.isEmpty()) {
            System.out.println("비밀번호는 필수 입니다.");
            return;
        }

        System.out.print("이메일: ");
        String email = sc.nextLine();
        if (email.isEmpty()) {
            System.out.println("이메일은 필수 입니다.");
            return;
        }

        service.signUp(Customer.builder().name(name).age(age).password(pw).email(email).build());
        System.out.println("회원가입 완료");
    }

    // 영화조회
    private void searchMv() throws SQLException {
        System.out.print("검색할 영화제목: ");
        String MvTitle = sc.nextLine().trim();
        if (MvTitle.isEmpty()) {
            System.out.println("검색어를 입력해주세요: ");
            return;
        }

        List<Movies> moviesList = service.findByMovieTitle(MvTitle);
        System.out.println("\n⚝⚝⚝검색결과⚝⚝⚝");
        if (moviesList.isEmpty()) {
            System.out.println("검색하신 영화가 없습니다.");
        }
    }

}
