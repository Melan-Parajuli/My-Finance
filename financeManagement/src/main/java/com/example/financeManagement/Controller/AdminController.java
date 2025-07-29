package com.example.financeManagement.Controller;

import com.example.financeManagement.Entity.Admin;
import com.example.financeManagement.Entity.User;
import com.example.financeManagement.Repository.UserRepository;
import com.example.financeManagement.Service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    @Autowired
    private UserRepository userRepository;
    @Autowired private AdminService adminService;

    @GetMapping("/dashboard")
    public String adminDashboard(Model model, @AuthenticationPrincipal UserDetails currentUser) {
        long userCount = userRepository.count();
        model.addAttribute("userCount", userCount);
        model.addAttribute("adminName", currentUser.getUsername());
        return "admin-dashboard";
    }

    @GetMapping("/users")
    public String viewAllUsers(Model model) {
        List<User> users = userRepository.findAll();
        model.addAttribute("users", users);
        return "admin-user-list";
    }

    @GetMapping("/settings")
    public String showAdminSettings(Model model, @AuthenticationPrincipal UserDetails currentUser) {
        Admin admin = adminService.getAdminByUsername(currentUser.getUsername());
        if (admin == null) {
            return "redirect:/admin/dashboard?error=notfound";
        }
        model.addAttribute("admin", admin);
        return "admin-settings";
    }

    @PostMapping("/profile")
    public String updateAdminProfile(@ModelAttribute("admin") Admin updatedAdmin,
                                     @AuthenticationPrincipal UserDetails currentUser,
                                     RedirectAttributes redirectAttributes) {
        Admin admin = adminService.getAdminByUsername(currentUser.getUsername());

        if (admin != null) {
            admin.setEmail(updatedAdmin.getEmail());
            admin.setPreferredCurrency(updatedAdmin.getPreferredCurrency());
            admin.setNotificationsEnabled(updatedAdmin.isNotificationsEnabled());
            adminService.save(admin);
            redirectAttributes.addAttribute("success", "true");
        } else {
            redirectAttributes.addAttribute("error", "notfound");
        }

        return "redirect:/admin/settings";
    }

    @PostMapping("/change-password")
    public String changeAdminPassword(@RequestParam String oldPassword,
                                      @RequestParam String newPassword,
                                      @RequestParam String confirmPassword,
                                      @AuthenticationPrincipal UserDetails currentUser,
                                      RedirectAttributes redirectAttributes) {
        Admin admin = adminService.getAdminByUsername(currentUser.getUsername());

        if (admin == null) {
            redirectAttributes.addAttribute("error", "notfound");
            return "redirect:/admin/settings";
        }

        if (!adminService.isOldPasswordCorrect(admin, oldPassword)) {
            redirectAttributes.addAttribute("error", "incorrectOld");
            return "redirect:/admin/settings";
        }

        if (!newPassword.equals(confirmPassword)) {
            redirectAttributes.addAttribute("error", "passwordMismatch");
            return "redirect:/admin/settings";
        }

        if (!isStrongPassword(newPassword)) {
            redirectAttributes.addAttribute("error", "weakPassword");
            return "redirect:/admin/settings";
        }

        adminService.changePassword(admin, oldPassword, newPassword);
        redirectAttributes.addAttribute("passwordSuccess", "true");
        return "redirect:/admin/settings";
    }

    private boolean isStrongPassword(String password) {
        return password.length() >= 8 &&
                password.matches(".*[A-Z].*") &&
                password.matches(".*\\d.*");
    }

    @PostMapping("/users/reset/{id}")
    public String resetUserPassword(@PathVariable Long id) {
        userRepository.findById(id).ifPresent(user -> {
            user.setPassword(adminService.encode("default123"));
            userRepository.save(user);
        });
        return "redirect:/admin/users?resetSuccess";
    }

    @PostMapping("/users/delete/{id}")
    public String deleteUser(@PathVariable Long id, @AuthenticationPrincipal UserDetails currentUser) {
        userRepository.findById(id).ifPresent(user -> {
            if (!user.getUsername().equals(currentUser.getUsername())) {
                userRepository.delete(user);
            }
        });
        return "redirect:/admin/users?deleted";
    }
}
