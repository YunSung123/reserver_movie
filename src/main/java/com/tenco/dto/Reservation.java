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
    private int movieId; // 예약한 영화 ID
    private int seat; // 예약한 좌석 번호
    private boolean isStatus; // 현재 결제 상태
}
