package com.tenco.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Movies {
    private int id; // 영화 고유 식별자
    private String title; // 영화 제목
    private String grade; // 관람 등급
    private BigDecimal price; // 티켓 가격
    private int viewCount; // 누적 관객 수
    private int roomId;
}
