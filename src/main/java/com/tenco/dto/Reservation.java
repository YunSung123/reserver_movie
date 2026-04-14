package com.tenco.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Reservation {
    private int id; // 예약 고유 식별자
    private int customerId; // 예약한 고객 ID
    private String customerName; //예약한 고객 이름
    private int movieId; // 예약한 영화 ID
    private String  movieName; //영화 이름
    private int roomId; // 예약한 영화관 ID
    private int roomNumber; //예약한 영화관 번호
    private int seatId; //좌석 ID
    private int seat; // 예약한 좌석 번호
    private boolean isStatus; // 현재 결제 상태
}
