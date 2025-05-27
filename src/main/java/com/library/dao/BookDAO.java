package com.library.dao;

import com.library.config.DatabaseConfig;
import com.library.model.Book;
import com.library.model.book.AcademicBookFactory;
import com.library.model.book.EntertainmentBookFactory;
import com.library.model.book.BookFactory;
import com.library.model.decorator.FavoriteBookDecorator;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class BookDAO {
    public void addBook(Book book) throws SQLException {
        String sql = "INSERT INTO books (title, author, type) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, book.getTitle());
            stmt.setString(2, book.getAuthor());
            stmt.setString(3, book.getType());
            stmt.executeUpdate();
        }
    }

    public List<Book> getAllBooks() throws SQLException {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM books";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                BookFactory factory = rs.getString("type").equals("Printed") ? new AcademicBookFactory() : new EntertainmentBookFactory();
                Book book = factory.createBook(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("author")
                );
                if (rs.getBoolean("is_favorite")) {
                    book = new FavoriteBookDecorator(book);
                }
                books.add(book);
            }
        }
        return books;
    }

    public Book getBookById(int id) throws SQLException {
        String sql = "SELECT * FROM books WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                BookFactory factory = rs.getString("type").equals("Printed") ? new AcademicBookFactory() : new EntertainmentBookFactory();
                Book book = factory.createBook(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("author")
                );
                if (rs.getBoolean("is_favorite")) {
                    book = new FavoriteBookDecorator(book);
                }
                return book;
            }
        }
        return null;
    }

    public void updateBook(Book book) throws SQLException {
        String sql = "UPDATE books SET title = ?, author = ?, type = ? WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, book.getTitle());
            stmt.setString(2, book.getAuthor());
            stmt.setString(3, book.getType());
            stmt.setInt(4, book.getId());
            stmt.executeUpdate();
        }
    }

    public void deleteBook(int id) throws SQLException {
        String deleteLoansSql = "DELETE FROM loans WHERE book_id = ?";
        String deleteBookSql = "DELETE FROM books WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement deleteLoansStmt = conn.prepareStatement(deleteLoansSql);
             PreparedStatement deleteBookStmt = conn.prepareStatement(deleteBookSql)) {
            conn.setAutoCommit(false);

            deleteLoansStmt.setInt(1, id);
            deleteLoansStmt.executeUpdate();

            deleteBookStmt.setInt(1, id);
            deleteBookStmt.executeUpdate();

            conn.commit();
        } catch (SQLException e) {
            throw new SQLException("Loi khi xoa sach: " + e.getMessage(), e);
        }
    }

    public void markAsFavorite(int id) throws SQLException {
        String sql = "UPDATE books SET is_favorite = TRUE WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    public void unmarkAsFavorite(int id) throws SQLException {
        String sql = "UPDATE books SET is_favorite = FALSE WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    public List<Book> getAvailableBooks() throws SQLException {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT b.* FROM books b LEFT JOIN loans l ON b.id = l.book_id AND l.return_date IS NULL WHERE l.book_id IS NULL";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                BookFactory factory = rs.getString("type").equals("Printed") ? new AcademicBookFactory() : new EntertainmentBookFactory();
                Book book = factory.createBook(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("author")
                );
                if (rs.getBoolean("is_favorite")) {
                    book = new FavoriteBookDecorator(book);
                }
                books.add(book);
            }
        }
        return books;
    }
}