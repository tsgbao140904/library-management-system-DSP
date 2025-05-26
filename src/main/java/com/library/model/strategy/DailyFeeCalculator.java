package com.library.model.strategy;

import com.library.model.Loan;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class DailyFeeCalculator implements FeeCalculator {
    private static final double FEE_PER_DAY = 1.0; // 1 USD/ngày

    @Override
    public double calculateFee(Loan loan) {
        LocalDate dueDate = loan.getDueDate();
        if (dueDate == null) {
            return 0.0;
        }

        LocalDate endDate = loan.getReturnDate() != null ? loan.getReturnDate() : LocalDate.now();
        if (endDate.isBefore(dueDate) || endDate.isEqual(dueDate)) {
            return 0.0; // Không tính phí nếu trả trước hoặc đúng hạn
        }

        long daysOverdue = ChronoUnit.DAYS.between(dueDate, endDate);
        return daysOverdue * FEE_PER_DAY;
    }
}