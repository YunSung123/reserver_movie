package com.tenco.view;


import com.tenco.service.ReserveService;

import java.util.Scanner;

public class ReserveView {
    private final ReserveService service = new ReserveService();
    private final Scanner sc = new Scanner(System.in);

    private Integer currentCustomerId = null; // 로그인 중인 고객 아이디
    private String currentCustomerName = null; // 로그인 중인 고객 이름

    public void start() {
        System.out.println("⚝⚝⚝영화예약관리시스템⚝⚝⚝");

    }

    private void printMenu() {
        System.out.println("\n⚝⚝⚝영화예약관리시스템⚝⚝⚝");
        if (currentCustomerId == null) {
            System.out.println("❗ 로그아웃 상태 ❗");
        } else {
            System.out.println("❗ 로그인: " + currentCustomerName + " ❗");
        }
        System.out.println("\uD83C\uDF7F\uD83C\uDF7F\uD83C\uDF7F\uD83C\uDF7F\uD83C\uDF7F");
        System.out.println("1. 로그인");
        System.out.println("2. 로그아웃");
        System.out.println("3. 회원가입");
        System.out.println("4. 영화 조회 ");
        System.out.println("5. 상영관 조회");
        System.out.println("6. 좌석 조회");
        System.out.println("7. 결제 확인");
        System.out.println("8. 예약 번호 조회");
        System.out.println("9. 나의 좌석번호 조회");
        System.out.println("10. 종료");
    }
}
