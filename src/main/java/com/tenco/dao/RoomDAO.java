package com.tenco.dao;

import com.tenco.dto.Room;
import com.tenco.util.DBConnectionManager;

import java.awt.print.Book;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RoomDAO {
    // 현재 상영관에서 상영하는 영화 정보
    public List<Room> findAll() {
        List<Room> roomList = new ArrayList<>();
        String sql = """
                    SELECT * FROM room;
                """;
        try (Connection conn = DBConnectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                roomList.add(mapToRoom(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return roomList;
    }

    // 이용(개방) 가능 여부 변경
    public Boolean useStatus(){


        return false;
    }

    
    // todo 지워야됨
    public static void main(String[] args) {
        RoomDAO dao = new RoomDAO();
        List<Room> roomList = dao.findAll();

        System.out.println(roomList);
    }

    private Room mapToRoom(ResultSet rs) throws SQLException {
        return Room.builder()
                .id(rs.getInt("id"))
                .seat(rs.getInt("seat"))
                .isAvailable(rs.getBoolean(("is_Available")))
                .build();
    }
}
