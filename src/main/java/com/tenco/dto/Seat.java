package com.tenco.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Seat {
    private int id; // 좌석 정보 ID
    private int roomNumber; // 상영관 번호(PK)
    private int seatNumber; // 좌석번호
    private boolean isAvailable; // 상영관 예약 가능 여부
}