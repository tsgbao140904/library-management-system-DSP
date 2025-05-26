package com.library.model;

import com.library.model.strategy.FeeCalculator;
import com.library.model.strategy.DailyFeeCalculator;
import com.library.model.strategy.QuantityFeeCalculator;
import com.library.model.strategy.FeeContext;

import java.time.LocalDate;

public class Loan {
    private int id;
    private int bookId;
    private int memberId;
    private LocalDate borrowDate;
    private LocalDate dueDate;
    private LocalDate returnDate;
    private double overdueFee;
    private Book book;
    private FeeContext feeContext; // Sử dụng FeeContext để quản lý chiến lược

    public Loan() {
        this.feeContext = new FeeContext(new DailyFeeCalculator()); // Mặc định là DailyFeeCalculator
    }

    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getBookId() { return bookId; }
    public void setBookId(int bookId) { this.bookId = bookId; }
    public int getMemberId() { return memberId; }
    public void setMemberId(int memberId) { this.memberId = memberId; }
    public LocalDate getBorrowDate() { return borrowDate; }
    public void setBorrowDate(LocalDate borrowDate) { this.borrowDate = borrowDate; }
    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }
    public LocalDate getReturnDate() { return returnDate; }
    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
        if (returnDate != null && dueDate != null) {
            calculateOverdueFee(); // Tính phí khi trả sách
        }
    }
    public double getOverdueFee() { return overdueFee; }
    public void setOverdueFee(double overdueFee) { this.overdueFee = overdueFee; }
    public Book getBook() { return book; }
    public void setBook(Book book) { this.book = book; }

    // Phương thức tính phí trễ hạn
    public void calculateOverdueFee() {
        if (feeContext != null) {
            this.overdueFee = feeContext.calculateFee(this);
        } else {
            this.overdueFee = 0.0;
        }
    }

    // Setter để chọn chiến lược tính phí
    public void setFeeStrategy(String feeType) {
        FeeCalculator calculator;
        switch (feeType != null ? feeType.toLowerCase() : "daily") {
            case "daily":
                calculator = new DailyFeeCalculator();
                break;
            case "quantity":
                calculator = new QuantityFeeCalculator();
                break;
            default:
                calculator = new DailyFeeCalculator(); // Mặc định
                break;
        }
        this.feeContext.setFeeCalculator(calculator);
    }
}