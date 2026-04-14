package com.tenco.dao;


import com.tenco.dto.Room;
import com.tenco.dto.Seat;

import java.util.ArrayList;
import java.util.List;
public class SeatDAO {

    // 현재 좌석 정보
    public List<Seat> findAll() {
        List<Seat> seatList = new ArrayList<>();

        return seatList;
    }

    // 이용(예약) 가능 여부 변경
    public boolean useStatus(Room roomList){

        return false;
    }
}
