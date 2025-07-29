package com.example.financeManagement.Controller;

import com.example.financeManagement.Entity.Admin;
import com.example.financeManagement.Service.AdminService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

@Controller
@RequestMapping("/admin/settings")
public class AdminSettingsController {

    private final AdminService adminService;
    private final  PasswordEncoder passwordEncoder;

    public AdminSettingsController(AdminService adminService, PasswordEncoder passwordEncoder) {
        this.adminService = adminService;
        this.passwordEncoder = passwordEncoder;
    }


    @PostMapping
    public String updateAdminSettings(@RequestParam String email,
                                      @RequestParam(required = false) String newPassword,
                                      @RequestParam(required = false) String confirmPassword,
                                      Principal principal,
                                      RedirectAttributes redirectAttributes,
                                      Model model) {
        Admin admin = adminService.getAdminByUsername(principal.getName());
        if (admin == null) {
            return "redirect:/login";
        }

        admin.setEmail(email);

        if (newPassword != null && !newPassword.isBlank()) {
            if (!newPassword.equals(confirmPassword)) {
                model.addAttribute("admin", admin);
                model.addAttribute("error", "Passwords do not match.");
                return "admin-settings";
            }
            admin.setPassword(passwordEncoder.encode(newPassword));
        }

        adminService.save(admin);
        redirectAttributes.addFlashAttribute("success", "Admin settings updated.");
        return "redirect:/admin/settings";
    }

    @PostMapping("/delete")
    @Transactional
    public String deleteAdmin(Principal principal,
                              HttpServletRequest request,
                              RedirectAttributes redirectAttributes) {
        Admin admin = adminService.getAdminByUsername(principal.getName());
        if (admin != null) {
            adminService.delete(admin);

            HttpSession session = request.getSession(false);
            if (session != null) session.invalidate();

            redirectAttributes.addFlashAttribute("message", "Your admin account has been deleted.");
        }

        return "redirect:/logout";
    }
}
