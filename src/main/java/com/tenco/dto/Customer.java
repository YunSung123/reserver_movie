package com.tenco.dto;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor

public class Customer extends User{
    private String email; // 고객 이메일 (로그인 ID 등)
    private int age; // 고객 나이
    private LocalDate createAt; // 계정 생성 일시
    private boolean isAvailable; // 계정 활성화 상태

}
