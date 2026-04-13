package com.tenco.dto;


import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Room {
    private int id; // 좌석 관리 번호
    private int seat; // 좌석 번호 (예약 시 참조됨)
    private boolean isAvailable; // 현재 예약 가능 여부
}