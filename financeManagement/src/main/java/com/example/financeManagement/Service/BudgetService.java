package com.example.financeManagement.Service;


import com.example.financeManagement.Entity.Financial;
import com.example.financeManagement.Entity.User;
import com.example.financeManagement.Repository.FinancialRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BudgetService {

    private final FinancialRepository financialRepository;

    public BudgetService(FinancialRepository financialRepository) {
        this.financialRepository = financialRepository;
    }


    public List<Financial> getAllByUser(User user) {
        return financialRepository.findAllByUser(user);
    }

    public Financial getByIdAndUser(Long id, User user) {
        Optional<Financial> budget = financialRepository.findById(id);
        return budget.filter(b -> b.getUser().getId().equals(user.getId())).orElse(null);
    }

    public Financial create(Financial financial, User user) {
        financial.setUser(user);
        return financialRepository.save(financial);
    }

    public boolean deleteByIdAndUser(Long id, User user) {
        Financial budget = getByIdAndUser(id, user);
        if (budget != null) {
            financialRepository.delete(budget);
            return true;
        }
        return false;
    }

    public List<Financial> getAll() {
        return financialRepository.findAll();
    }
}
