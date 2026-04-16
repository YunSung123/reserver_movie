package com.tenco.dao;

import com.tenco.dto.Movies;
import com.tenco.dto.Room;
import com.tenco.util.DBConnectionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RoomDAO {
    // 현재 상영관에서 상영하는 영화 정보
    public List<Movies> findAll() {
        List<Movies> moviesList = new ArrayList<>();
        String sql = """
                    SELECT title, grade, price, view_count
                    FROM movies
                    JOIN room on room.movie_id = movies.id;
                """;
        try (Connection conn = DBConnectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery();
        ) {
            while (rs.next()) {

                moviesList.add(mapToMovies(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return moviesList;
    }
        //3###
    // 이용(개방) 가능 여부 변경
    public Boolean useStatus(Room room) {
        List<Movies> moviesList = new ArrayList<>();
        String sql = """
                    SELECT * FROM room WHERE id = ?
                """;
        try (Connection conn = DBConnectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, room.getId());
            int rs = pstmt.executeUpdate();
            return rs > 0;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    private Movies mapToMovies(ResultSet rs) throws SQLException {
        return Movies.builder()
                .title(rs.getString("title"))
                .grade(rs.getString("grade"))
                .price(rs.getBigDecimal("price"))
                .viewCount(rs.getInt("view_count"))
                .build();
    }
}
