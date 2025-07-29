package com.example.financeManagement.Controller;

import com.example.financeManagement.Entity.User;
import com.example.financeManagement.Service.TransactionService;
import com.example.financeManagement.Service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;

@Controller
public class DashboardController {

    private final TransactionService transactionService;
    private final UserService userService;

    public DashboardController(TransactionService transactionService, UserService userService) {
        this.transactionService = transactionService;
        this.userService = userService;
    }

    @GetMapping("/dashboard")
    public String dashboardPage(Model model, Principal principal) {
        User user = userService.getUserByUsername(principal.getName());

        double totalRevenue = transactionService.getTotalRevenue(user);
        double totalExpenses = transactionService.getTotalExpenses(user);
        double netSavings = transactionService.getNetSavings(user);

        model.addAttribute("username", user.getUsername());
        model.addAttribute("totalRevenue", totalRevenue);
        model.addAttribute("totalExpenses", totalExpenses);
        model.addAttribute("netSavings", netSavings);

        return "dashboard";
    }

    @GetMapping("/reports")
    public String reportsPage(Model model, Principal principal) {
        User user = userService.getUserByUsername(principal.getName());

        model.addAttribute("revenues", transactionService.getMonthlyRevenues(user));
        model.addAttribute("expenses", transactionService.getMonthlyExpenses(user));

        return "reports";
    }
}
