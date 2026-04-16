package com.tenco.dto;


import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Room {
    private int id; // 상영관 관리 번호
    private int roomNumber;// 상영관 번호 (예약 시 참조됨)
    private int movieId; //영화 번호
    private boolean isAvailable; // 상영관 이용 가능 여부

}