package com.library.dao;

import com.library.config.DatabaseConfig;
import com.library.model.Loan;
import com.library.util.DateProvider;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class LoanDAO {
    public void addLoan(Loan loan) throws SQLException {
        String sql = "INSERT INTO loans (book_id, member_id, borrow_date, due_date) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, loan.getBookId());
            stmt.setInt(2, loan.getMemberId());
            stmt.setDate(3, Date.valueOf(loan.getBorrowDate()));
            stmt.setDate(4, Date.valueOf(loan.getDueDate()));
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    loan.setId(rs.getInt(1));
                }
            }
        }
    }

    public List<Loan> getAllLoans() throws SQLException {
        List<Loan> loans = new ArrayList<>();
        String sql = "SELECT * FROM loans";
        System.out.println("Đang lấy danh sách loans từ DB...");
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Loan loan = new Loan();
                loan.setId(rs.getInt("id"));
                loan.setBookId(rs.getInt("book_id"));
                loan.setMemberId(rs.getInt("member_id"));
                loan.setBorrowDate(rs.getDate("borrow_date").toLocalDate());
                loan.setDueDate(rs.getDate("due_date").toLocalDate());
                Date returnDate = rs.getDate("return_date");
                loan.setReturnDate(returnDate != null ? returnDate.toLocalDate() : null);
                loan.setOverdueFee(rs.getDouble("overdue_fee"));
                // Tính phí trễ hạn nếu chưa trả và đã quá hạn
                if (loan.getReturnDate() == null && loan.getDueDate() != null) {
                    LocalDate currentDate = DateProvider.getInstance().getCurrentDate();
                    if (currentDate.isAfter(loan.getDueDate())) {
                        loan.setFeeStrategy("daily"); // Hoặc "quantity"
                        loan.calculateOverdueFee();
                    }
                }
                loans.add(loan);
            }
            System.out.println("Số lượng loans lấy được: " + loans.size());
            System.out.println("Số lượng sách đang mượn (return_date IS NULL): " +
                    loans.stream().filter(loan -> loan.getReturnDate() == null).count());
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy danh sách loans: " + e.getMessage());
            throw e;
        }
        return loans;
    }

    public Loan getLoanById(int id) throws SQLException {
        String sql = "SELECT * FROM loans WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Loan loan = new Loan();
                    loan.setId(rs.getInt("id"));
                    loan.setBookId(rs.getInt("book_id"));
                    loan.setMemberId(rs.getInt("member_id"));
                    loan.setBorrowDate(rs.getDate("borrow_date").toLocalDate());
                    loan.setDueDate(rs.getDate("due_date").toLocalDate());
                    Date returnDate = rs.getDate("return_date");
                    loan.setReturnDate(returnDate != null ? returnDate.toLocalDate() : null);
                    loan.setOverdueFee(rs.getDouble("overdue_fee"));
                    return loan;
                }
            }
        }
        return null;
    }

    public List<Loan> getLoansByMember(int memberId) throws SQLException {
        List<Loan> loans = new ArrayList<>();
        String sql = "SELECT * FROM loans WHERE member_id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, memberId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Loan loan = new Loan();
                    loan.setId(rs.getInt("id"));
                    loan.setBookId(rs.getInt("book_id"));
                    loan.setMemberId(rs.getInt("member_id"));
                    loan.setBorrowDate(rs.getDate("borrow_date").toLocalDate());
                    loan.setDueDate(rs.getDate("due_date").toLocalDate());
                    Date returnDate = rs.getDate("return_date");
                    loan.setReturnDate(returnDate != null ? returnDate.toLocalDate() : null);
                    loan.setOverdueFee(rs.getDouble("overdue_fee"));
                    loans.add(loan);
                }
            }
        }
        return loans;
    }

    public void updateLoan(Loan loan) throws SQLException {
        String sql = "UPDATE loans SET return_date = ?, overdue_fee = ? WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDate(1, loan.getReturnDate() != null ? Date.valueOf(loan.getReturnDate()) : null);
            stmt.setDouble(2, loan.getOverdueFee());
            stmt.setInt(3, loan.getId());
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                System.out.println("Không có hàng nào được cập nhật cho loan id: " + loan.getId());
            } else {
                System.out.println("Cập nhật thành công loan id: " + loan.getId());
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi cập nhật loan: " + e.getMessage());
            throw e;
        }
    }
}