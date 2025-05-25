package com.library.dao;

import com.library.config.DatabaseConfig;
import com.library.model.Member;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MemberDAO {
    public Member getMemberByUsername(String username) throws SQLException {
        String sql = "SELECT * FROM members WHERE username = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Member(rs.getInt("id"), rs.getString("username"),
                        rs.getString("password"), rs.getString("full_name"),
                        rs.getString("role"));
            }
        }
        return null;
    }

    public Member getMemberById(int id) throws SQLException {
        String sql = "SELECT * FROM members WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Member(rs.getInt("id"), rs.getString("username"),
                        rs.getString("password"), rs.getString("full_name"),
                        rs.getString("role"));
            }
        }
        return null;
    }

    public void addMember(Member member) throws SQLException {
        String sql = "INSERT INTO members (username, password, full_name, role) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, member.getUsername());
            stmt.setString(2, member.getPassword());
            stmt.setString(3, member.getFullName());
            stmt.setString(4, member.getRole());
            stmt.executeUpdate();
        }
    }

    public void updateMember(Member member) throws SQLException {
        String sql = "UPDATE members SET username = ?, password = ?, full_name = ?, role = ? WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, member.getUsername());
            stmt.setString(2, member.getPassword());
            stmt.setString(3, member.getFullName());
            stmt.setString(4, member.getRole());
            stmt.setInt(5, member.getId());
            stmt.executeUpdate();
        }
    }

    public void deleteMember(int id) throws SQLException {
        Connection conn = null;
        try {
            conn = DatabaseConfig.getConnection();
            conn.setAutoCommit(false); // Bắt đầu giao dịch

            // Xóa các khoản mượn liên quan đến thành viên
            String deleteLoansSql = "DELETE FROM loans WHERE member_id = ?";
            try (PreparedStatement deleteLoansStmt = conn.prepareStatement(deleteLoansSql)) {
                deleteLoansStmt.setInt(1, id);
                deleteLoansStmt.executeUpdate();
            }

            // Xóa thành viên
            String deleteMemberSql = "DELETE FROM members WHERE id = ?";
            try (PreparedStatement deleteMemberStmt = conn.prepareStatement(deleteMemberSql)) {
                deleteMemberStmt.setInt(1, id);
                int rowsAffected = deleteMemberStmt.executeUpdate();
                if (rowsAffected == 0) {
                    throw new SQLException("Không tìm thấy thành viên với ID: " + id);
                }
            }

            conn.commit(); // Commit giao dịch nếu thành công
        } catch (SQLException e) {
            if (conn != null) {
                conn.rollback(); // Rollback nếu có lỗi
            }
            throw e;
        } finally {
            if (conn != null) {
                conn.setAutoCommit(true); // Khôi phục auto-commit
                conn.close();
            }
        }
    }

    public List<Member> getAllMembers() throws SQLException {
        List<Member> members = new ArrayList<>();
        String sql = "SELECT * FROM members";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                members.add(new Member(rs.getInt("id"), rs.getString("username"),
                        rs.getString("password"), rs.getString("full_name"),
                        rs.getString("role")));
            }
        }
        return members;
    }
}