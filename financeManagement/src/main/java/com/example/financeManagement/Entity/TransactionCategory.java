package com.example.financeManagement.Entity;

public enum TransactionCategory {
    EXPENSE,
    REVENUE;

    @Override
    public String toString() {
        return name(); // Or return name().toLowerCase() if needed
    }
}
