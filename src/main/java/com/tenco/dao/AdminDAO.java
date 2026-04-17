package com.tenco.dao;

import com.tenco.dto.Admin;
import com.tenco.dto.Customer;
import com.tenco.util.DBConnectionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AdminDAO {
    public Admin login(String adminId, String password) throws SQLException {
        Connection conn = null;
        String sql = """
                SELECT * FROM admin WHERE admin_id = ? and password = ?
                """;
        conn = DBConnectionManager.getConnection();
       Admin admin = null;

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, adminId);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                admin = Admin.builder()
                        .id(rs.getInt("id"))
                        .adminId(rs.getString("admin_id"))
                        .password(rs.getString("password"))
                        .name(rs.getString("name"))
                        .build();
                System.out.println("로그인됨");
                return admin;
            } else {
                return null;
            }
        }
    }

}
