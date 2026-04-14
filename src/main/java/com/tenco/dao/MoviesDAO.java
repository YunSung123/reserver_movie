package com.tenco.dao;

import com.tenco.dto.Movies;
import com.tenco.util.DBConnectionManager;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MoviesDAO {

//    MoviesDAO.java
//            메소드
//    반환 타입
//    설명


    //    findAll()	List<movie>	영화 전체 목록 (is_active=TRUE)
    public static List<Movies> findAll() throws SQLException {


        List<Movies> moviesList = new ArrayList<>();

        String sql = """
                select * from movies
                """;

        Connection conn = DBConnectionManager.getConnection();

        ResultSet rs;
        try (PreparedStatement psmt = conn.prepareStatement(sql)) {
            rs = psmt.executeQuery();
        }
        while (rs.next()) {
            Movies movies = Movies
                    .builder()
                    .id(rs.getInt("id"))
                    .title(rs.getString("title"))
                    .grade(rs.getString("grade"))
                    .price(rs.getBigDecimal("price"))
                    .viewCount(rs.getInt("view_count"))
                    .build();
            moviesList.add(movies);

        }
        return moviesList;
    }
//    제목으로 영화 조회

    public static List<Movies> findByMovies(String title) throws SQLException {
        List<Movies> moviesList = new ArrayList<>();

        String sql = """
                select * from movies where title like ?
                """;
        Connection conn = DBConnectionManager.getConnection();
        ResultSet rs;
        try (PreparedStatement psmt = conn.prepareStatement(sql)) {
            psmt.setString(1, title);
            rs = psmt.executeQuery();
        }


        while (rs.next()) {

            Movies movies = Movies
                    .builder()
                    .id(rs.getInt("id"))
                    .title(rs.getString("title"))
                    .grade(rs.getString("grade"))
                    .price(rs.getBigDecimal("price"))
                    .viewCount(rs.getInt("view_count"))
                    .build();
            moviesList.add(movies);

        }
        if (moviesList.isEmpty() == true) { // 데이터가 없으면
            System.out.println("해당 영화는 존재하지 않습니다.");
        }
        return moviesList;
    }

//    insert(Movies)	Boolean	영화 등록

    public static Boolean insert(Movies movies) throws SQLException {

        String sql = """
                insert into movies(title,grade,price)values
                ( ? , ? , ? )
                """;

        Connection conn = DBConnectionManager.getConnection();
        try (PreparedStatement psmt = conn.prepareStatement(sql)) {
            psmt.setString(1, movies.getTitle());
            psmt.setString(2, movies.getGrade());
            psmt.setBigDecimal(3, movies.getPrice());
            psmt.executeUpdate();
        }
        System.out.println("영화가 추가되었습니다. 제목: " + movies.getTitle());
        return true;

    }

    //    update(Movies)	Boolean	영화 수정//
    public static Boolean update(Movies movies) throws SQLException {
        Connection conn = DBConnectionManager.getConnection();

        String sql = """
                update movies set title = ? , price = ? , grade = ? where id = ?
                """;
        try (PreparedStatement psmt = conn.prepareStatement(sql)) {
            psmt.setString(1, movies.getTitle());
            psmt.setBigDecimal(2, movies.getPrice());
            psmt.setString(3, movies.getGrade());
            psmt.setInt(4, movies.getId());

            psmt.executeUpdate();
        }
        System.out.println("영화가 수정되었습니다. 수정된 영화: " + movies.getTitle());

        return true;


    }

//    softDelete(movie)	Boolean	소프트 삭제

    public Boolean softDelete(Movies movies) throws SQLException {
        Connection conn = DBConnectionManager.getConnection();
        String sql = """
                update movies set is_availiable = false where id = ?;
                """;

        try (PreparedStatement psmt = conn.prepareStatement(sql)) {
            psmt.setInt(1, movies.getId());
            psmt.executeUpdate();
        }
        System.out.println("영화가 소프트 삭제 되었습니다.");
        System.out.println("삭제된 영화 아이디: " + movies.getId());
        return true;

    }

    public static void main(String[] args) throws SQLException {
        MoviesDAO moviesDAO = new MoviesDAO();
//        Movies movies = Movies
//                .builder()
//                .id(1)
//                .title("감스트")
//                .grade("19")
//                .price(new BigDecimal(21000))
//                .viewCount(0)
//                .build();
//        System.out.println(moviesDAO.findAll());
//        moviesDAO.insert(movies);
//        System.out.println(moviesDAO.findByMovies("인터스텔라ㅍㅍ"));
//        moviesDAO.update(movies);

//        Movies movies = new Movies();
//        movies.setId(2);

//        moviesDAO.softDelete(movies);
    }


}
