package com.library.model.strategy;

import com.library.model.Loan;

public class QuantityFeeCalculator implements FeeCalculator {
    private static final double FEE_PER_BOOK = 5.0; // 5 USD/sách

    @Override
    public double calculateFee(Loan loan) {
        if (loan.getReturnDate() == null) {
            return FEE_PER_BOOK;
        }
        return 0.0;
    }
}