package com.example.financeManagement.Controller;

import com.example.financeManagement.Entity.Transaction;
import com.example.financeManagement.Entity.TransactionCategory;
import com.example.financeManagement.Entity.User;
import com.example.financeManagement.Service.TransactionService;
import com.example.financeManagement.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/expenses")
public class ExpenseController {

    private final UserService userService;
    private final TransactionService transactionService;

    @Autowired
    public ExpenseController(UserService userService, TransactionService transactionService) {
        this.userService = userService;
        this.transactionService = transactionService;
    }

            @GetMapping
    public String listExpenses(Model model, Principal principal) {
        User user = userService.getUserByUsername(principal.getName());
        List<Transaction> expenses = transactionService.getAllByUser(user).stream()
                .filter(tx -> tx.getType() == TransactionCategory.EXPENSE)
                .toList();
        model.addAttribute("expenses", expenses);
        return "expenses/list"; // → templates/expenses/list.html
    }

    @GetMapping("/new")
    public String showAddForm(Model model) {
        Transaction expense = new Transaction();
        expense.setDate(LocalDate.now());
        model.addAttribute("expense", expense);
        return "expenses/form"; // → templates/expenses/form.html
    }

    @PostMapping
    public String saveExpense(@ModelAttribute("expense") Transaction expense, Principal principal) {
        User user = userService.getUserByUsername(principal.getName());
        expense.setUser(user);
        expense.setType(TransactionCategory.EXPENSE);
        transactionService.saveTransaction(expense);
        return "redirect:/expenses";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model, Principal principal) {
        Transaction expense = transactionService.getById(id);
        if (expense == null || !expense.getUser().getUsername().equals(principal.getName()) || expense.getType() != TransactionCategory.EXPENSE) {
            return "redirect:/expenses";
        }
        model.addAttribute("expense", expense);
        return "expenses/form";
    }

    @PostMapping("/update/{id}")
    public String updateExpense(@PathVariable Long id, @ModelAttribute("expense") Transaction expenseForm, Principal principal) {
        Transaction existing = transactionService.getById(id);
        if (existing != null && existing.getUser().getUsername().equals(principal.getName()) && existing.getType() == TransactionCategory.EXPENSE) {
            existing.setAmount(expenseForm.getAmount());
            existing.setDate(expenseForm.getDate());
            existing.setDescription(expenseForm.getDescription());
            transactionService.saveTransaction(existing);
        }
        return "redirect:/expenses";
    }

    @GetMapping("/delete/{id}")
    public String deleteExpense(@PathVariable Long id, Principal principal) {
        User user = userService.getUserByUsername(principal.getName());
        transactionService.deleteByIdAndUser(id, user);
        return "redirect:/expenses";
    }
}
