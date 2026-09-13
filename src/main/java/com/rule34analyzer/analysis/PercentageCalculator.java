package com.rule34analyzer.analysis;

import java.math.BigDecimal;
import java.math.RoundingMode;

public final class PercentageCalculator {
    private PercentageCalculator() {}

    public static BigDecimal calculate(int ai, int total) {
        if (total <= 0) return BigDecimal.ZERO.setScale(2);
        return BigDecimal.valueOf(ai)
            .multiply(BigDecimal.valueOf(100))
            .divide(BigDecimal.valueOf(total), 10, RoundingMode.CEILING)
            .setScale(2, RoundingMode.CEILING);
    }
}
