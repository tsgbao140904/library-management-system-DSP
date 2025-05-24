package com.library.model.strategy;

import com.library.model.Loan;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class DailyFeeCalculator implements FeeCalculator {
    private static final double FEE_PER_DAY = 1.0; // 1 USD/ngày

    @Override
    public double calculateFee(Loan loan) {
        if (loan.getReturnDate() == null) {
            LocalDate dueDate = loan.getDueDate();
            LocalDate currentDate = LocalDate.now();
            if (currentDate.isAfter(dueDate)) {
                long daysOverdue = ChronoUnit.DAYS.between(dueDate, currentDate);
                return daysOverdue * FEE_PER_DAY;
            }
        }
        return 0.0;
    }
}