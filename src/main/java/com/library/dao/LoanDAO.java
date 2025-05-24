package com.library.dao;

import com.library.config.DatabaseConfig;
import com.library.model.Loan;
import com.library.model.strategy.FeeCalculator;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class LoanDAO {
    public void addLoan(Loan loan) throws SQLException {
        String sql = "INSERT INTO loans (book_id, member_id, borrow_date, due_date) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, loan.getBookId());
            stmt.setInt(2, loan.getMemberId());
            stmt.setObject(3, loan.getBorrowDate());
            stmt.setObject(4, loan.getDueDate());
            stmt.executeUpdate();
        }
    }

    public List<Loan> getAllLoans() throws SQLException {
        List<Loan> loans = new ArrayList<>();
        String sql = "SELECT * FROM loans";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Loan loan = new Loan(rs.getInt("id"), rs.getInt("book_id"),
                        rs.getInt("member_id"), rs.getDate("borrow_date").toLocalDate(),
                        rs.getDate("due_date").toLocalDate());
                if (rs.getDate("return_date") != null) {
                    loan.setReturnDate(rs.getDate("return_date").toLocalDate());
                }
                loan.setOverdueFee(rs.getDouble("overdue_fee"));
                loans.add(loan);
            }
        }
        return loans;
    }

    public void returnBook(int loanId, FeeCalculator feeCalculator) throws SQLException {
        Loan loan = getLoanById(loanId);
        if (loan != null) {
            double fee = feeCalculator.calculateFee(loan);
            String sql = "UPDATE loans SET return_date = ?, overdue_fee = ? WHERE id = ?";
            try (Connection conn = DatabaseConfig.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setObject(1, LocalDate.now());
                stmt.setDouble(2, fee);
                stmt.setInt(3, loanId);
                stmt.executeUpdate();
            }
        }
    }

    private Loan getLoanById(int loanId) throws SQLException {
        String sql = "SELECT * FROM loans WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, loanId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Loan loan = new Loan(rs.getInt("id"), rs.getInt("book_id"),
                        rs.getInt("member_id"), rs.getDate("borrow_date").toLocalDate(),
                        rs.getDate("due_date").toLocalDate());
                if (rs.getDate("return_date") != null) {
                    loan.setReturnDate(rs.getDate("return_date").toLocalDate());
                }
                loan.setOverdueFee(rs.getDouble("overdue_fee"));
                return loan;
            }
        }
        return null;
    }
}