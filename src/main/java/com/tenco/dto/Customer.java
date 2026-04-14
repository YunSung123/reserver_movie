package com.tenco.dto;

import com.google.gson.Gson;
import lombok.*;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "password")
public class Customer {
    private int id; // 고객 고유 식별자
    private String email; // 고객 이메일 (로그인 ID 등)
    private String password; // 고객 비밀번호
    private String name; // 고객 이름
    private int age; // 고객 나이
    private LocalDate createAt; // 계정 생성 일시
    private boolean isAvailable; // 계정 활성화 상태


}
