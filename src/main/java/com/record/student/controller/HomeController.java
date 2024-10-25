package com.record.student.controller;

import com.record.student.model.Admin;
import com.record.student.sevice.AdminService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Controller
public class HomeController {

    @Autowired
    private AdminService adminService;
    @Qualifier("passwordEncoder")
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Bean
    public PasswordEncoder paasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @GetMapping("/")
    public String index() {

        return "index";
    }

    @GetMapping("/clearAuthAndHome")
    public String clearAuthAndHome(HttpServletRequest request) {
        // Invalidate the session
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }

        // Clear authentication
        SecurityContextHolder.clearContext();

        // Redirect to the home page
        return "redirect:/";
    }

    @GetMapping("/add-admin")
    public String addAdmin() {
        return "admin/add_Admin";
    }

    @PostMapping("/process-add")
    public String addAdmin(@RequestParam("name") String username,
                           @RequestParam("password") String password,
                           Model model) {

        Optional<Admin> OptionalAdmin = this.adminService.getAdminByName(username);

        if (OptionalAdmin.isPresent()) {

            Admin admin = OptionalAdmin.get();

            admin.setPassword(passwordEncoder.encode(password));

            this.adminService.createAdmin(admin);

            model.addAttribute("message", "Password Change successfully!");

        }else{

            try {

                this.adminService.createAdmin(new Admin(username, passwordEncoder.encode(password), "ADMIN"));

                model.addAttribute("message", "Admin added successfully!");

            } catch (Exception e) {


                model.addAttribute("message", "Error adding admin: " + e.getMessage());

            }
        }

        return "redirect:/"; // Redirect to the form page or a success page
    }

}
