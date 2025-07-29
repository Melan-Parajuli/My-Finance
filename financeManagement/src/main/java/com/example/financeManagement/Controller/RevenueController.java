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
@RequestMapping("/revenues")
public class RevenueController {

    private final UserService userService;
    private final TransactionService transactionService;

    @Autowired
    public RevenueController(UserService userService, TransactionService transactionService) {
        this.userService = userService;
        this.transactionService = transactionService;
    }

    @GetMapping
    public String listRevenues(Model model, Principal principal) {
        User user = userService.getUserByUsername(principal.getName());
        List<Transaction> revenues = transactionService.getAllByUser(user).stream()
                .filter(tx -> tx.getType() == TransactionCategory.REVENUE)
                .toList();
        model.addAttribute("revenues", revenues);
        return "revenues/list";
    }

    @GetMapping("/new")
    public String showAddForm(Model model) {
        Transaction revenue = new Transaction();
        revenue.setDate(LocalDate.now());
        model.addAttribute("revenue", revenue);
        return "revenues/form";
    }

    @PostMapping
    public String saveRevenue(@ModelAttribute("revenue") Transaction revenue, Principal principal) {
        User user = userService.getUserByUsername(principal.getName());
        revenue.setUser(user);
        revenue.setType(TransactionCategory.REVENUE);
        transactionService.saveTransaction(revenue);
        return "redirect:/revenues";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model, Principal principal) {
        Transaction revenue = transactionService.getById(id);
        if (revenue == null || !revenue.getUser().getUsername().equals(principal.getName()) || revenue.getType() != TransactionCategory.REVENUE) {
            return "redirect:/revenues";
        }
        model.addAttribute("revenue", revenue);
        return "revenues/form";
    }

    @PostMapping("/update/{id}")
    public String updateRevenue(@PathVariable Long id, @ModelAttribute("revenue") Transaction revenueForm, Principal principal) {
        Transaction existing = transactionService.getById(id);
        if (existing != null && existing.getUser().getUsername().equals(principal.getName()) && existing.getType() == TransactionCategory.REVENUE) {
            existing.setAmount(revenueForm.getAmount());
            existing.setDate(revenueForm.getDate());
            existing.setDescription(revenueForm.getDescription());
            transactionService.saveTransaction(existing);
        }
        return "redirect:/revenues";
    }

    @GetMapping("/delete/{id}")
    public String deleteRevenue(@PathVariable Long id, Principal principal) {
        User user = userService.getUserByUsername(principal.getName());
        transactionService.deleteByIdAndUser(id, user);
        return "redirect:/revenues";
    }
}