package com.library.model.strategy;

import com.library.model.Loan;
import com.library.util.DateProvider;

import java.time.LocalDate;

public class QuantityFeeCalculator implements FeeCalculator {
    private static final double FEE_PER_BOOK = 5.0; // 5 USD/sách

    @Override
    public double calculateFee(Loan loan) {
        if (loan.getReturnDate() == null && loan.getDueDate() != null) {
            LocalDate currentDate = DateProvider.getInstance().getCurrentDate();
            if (currentDate.isAfter(loan.getDueDate())) {
                return FEE_PER_BOOK; // Phí 5 USD khi sách quá hạn
            }
        }
        return 0.0; // Không có phí nếu chưa quá hạn hoặc đã trả
    }
}