package com.library.model.strategy;

import com.library.model.Loan;
import com.library.util.DateProvider;

import java.time.LocalDate;

public class QuantityFeeCalculator implements FeeCalculator {
    private static final double FEE_PER_BOOK = 5.0; // 5 USD/sách

    @Override
    public double calculateFee(Loan loan) {
        LocalDate dueDate = loan.getDueDate();
        if (dueDate == null) {
            return 0.0;
        }

        LocalDate endDate = loan.getReturnDate() != null ? loan.getReturnDate() : DateProvider.getInstance().getCurrentDate();
        if (endDate.isBefore(dueDate) || endDate.isEqual(dueDate)) {
            return 0.0; // Không tính phí nếu trả trước hoặc đúng hạn
        }

        return FEE_PER_BOOK; // Phí 5 USD nếu quá hạn
    }
}