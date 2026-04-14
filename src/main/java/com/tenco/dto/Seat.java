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
    private int id; // 좌석 관리 번호
    private int roomId; // 상영관 번호 ID
    private int seatNumber; // 좌석 번호
    private boolean isAvailable; // 이용 가능 여부
}
