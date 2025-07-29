package com.example.financeManagement.Service;

import com.example.financeManagement.Entity.Transaction;
import com.example.financeManagement.Entity.TransactionCategory;
import com.example.financeManagement.Entity.User;
import com.example.financeManagement.Repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;

    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public List<Transaction> getAll() {
        return transactionRepository.findAll();
    }

    public List<Transaction> getAllByUser(User user) {
        return transactionRepository.findAllByUser(user);
    }

    public Transaction create(Transaction transaction, User user) {
        transaction.setUser(user);
        return transactionRepository.save(transaction);
    }

    public boolean deleteByIdAndUser(Long id, User user) {
        Optional<Transaction> tx = transactionRepository.findById(id);
        if (tx.isPresent() && tx.get().getUser().getId().equals(user.getId())) {
            transactionRepository.delete(tx.get());
            return true;
        }
        return false;
    }

    public Transaction getById(Long id) {
        return transactionRepository.findById(id).orElse(null);
    }

    public double calculateTotalRevenue(User user) {
        return transactionRepository.findAllByUser(user).stream()
                .filter(tx -> tx.getType() == TransactionCategory.REVENUE)
                .mapToDouble(Transaction::getAmount)
                .sum();
    }

    public double calculateTotalExpenses(User user) {
        return transactionRepository.findAllByUser(user).stream()
                .filter(tx -> tx.getType() == TransactionCategory.EXPENSE)
                .mapToDouble(Transaction::getAmount)
                .sum();
    }

    public List<Transaction> getRecentTransactions(User user, int limit) {
        return transactionRepository.findAllByUser(user).stream()
                .sorted((a, b) -> b.getDate().compareTo(a.getDate()))
                .limit(limit)
                .toList();
    }

    public Transaction saveTransaction(Transaction transaction) {
        return transactionRepository.save(transaction);
    }

    public List<Double> getMonthlyRevenues(User user) {
        return getMonthlySums(user, TransactionCategory.REVENUE);
    }

    public List<Double> getMonthlyExpenses(User user) {
        return getMonthlySums(user, TransactionCategory.EXPENSE);
    }

    private List<Double> getMonthlySums(User user, TransactionCategory type) {
        List<Double> monthlySums = new ArrayList<>(Collections.nCopies(12, 0.0));
        List<Transaction> transactions = transactionRepository.findAllByUserAndType(user, type);

        for (Transaction tx : transactions) {
            if (tx.getDate() != null) {
                int monthIndex = tx.getDate().getMonthValue() - 1;
                double currentSum = monthlySums.get(monthIndex);
                monthlySums.set(monthIndex, currentSum + Math.abs(tx.getAmount()));
            }
        }

        return monthlySums;
    }

    public double getTotalRevenue(User user) {
        return calculateTotalRevenue(user);
    }

    public double getTotalExpenses(User user) {
        return calculateTotalExpenses(user);
    }

    public double getNetSavings(User user) {
        return calculateTotalRevenue(user) - calculateTotalExpenses(user);
    }

    public List<Transaction> getAllRevenueByUser(User user) {
        return transactionRepository.findAllByUserAndType(user, TransactionCategory.REVENUE);
    }

    public List<Transaction> getAllExpenseByUser(User user) {
        return transactionRepository.findAllByUserAndType(user, TransactionCategory.EXPENSE);
    }
}
