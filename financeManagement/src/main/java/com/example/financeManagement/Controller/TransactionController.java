package com.example.financeManagement.Controller;

import com.example.financeManagement.Entity.Transaction;
import com.example.financeManagement.Entity.User;
import com.example.financeManagement.Service.TransactionService;
import com.example.financeManagement.Service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;
import java.util.List;

@Controller
public class TransactionController {

    private final TransactionService transactionService;
    private final UserService userService;

    public TransactionController(TransactionService transactionService, UserService userService) {
        this.transactionService = transactionService;
        this.userService = userService;
    }

    @GetMapping("/transactions")
    public String listAllTransactions(Model model, Principal principal) {
        User user = userService.getUserByUsername(principal.getName());
        List<Transaction> transactions = transactionService.getAllByUser(user);
        model.addAttribute("transactions", transactions);
        return "transactions/list";
    }

    @GetMapping("/transactions/view/{id}")
    public String viewTransaction(@org.springframework.web.bind.annotation.PathVariable Long id, Model model, Principal principal) {
        Transaction transaction = transactionService.getById(id);
        if (transaction == null || !transaction.getUser().getUsername().equals(principal.getName())) {
            return "redirect:/transactions";
        }
        model.addAttribute("transaction", transaction);
        return "transactions/view";
    }
}
