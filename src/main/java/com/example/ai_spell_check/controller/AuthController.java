package com.example.ai_spell_check.controller;


import com.example.ai_spell_check.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {
    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String loginPage(Model model) {
        model.addAttribute("bodyContent", "login");
        return "master-template";
    }

    @GetMapping("/register")
    public String getRegisterPage(Model model) {
        model.addAttribute("bodyContent", "register");
        return "master-template";
    }

    @PostMapping("/register")
    public String registerUser(@RequestParam("username") String username,
                               @RequestParam("email") String email,
                               @RequestParam("password")String password) {
        userService.createUser(username,email,password);
        return "redirect:/index";
    }

    @GetMapping("/profile")
    public String getMyProfilePage(Model model, @AuthenticationPrincipal UserDetails user) {
        model.addAttribute("user", userService.findByUsername(user.getUsername()));
        model.addAttribute("bodyContent", "my-profile");
        return "master-template";
    }

    @PostMapping("/profile/update-info")
    public String updateInfo(@RequestParam("username") String username,
                             @RequestParam("email") String email,
                             @AuthenticationPrincipal UserDetails user,
                             HttpServletRequest request) {
        userService.updateInfo(user, username, email);
        SecurityContextHolder.clearContext();
        request.getSession().invalidate();

        return "redirect:/login";
    }

    @PostMapping("/profile/update-password")
    public String updatePassword(RedirectAttributes redirectAttributes,
                                 @RequestParam("password") String password,
                                 @RequestParam("confirmPassword") String confirmPassword,
                                 @AuthenticationPrincipal UserDetails user,
                                 HttpServletRequest request) {
        if(!password.equals(confirmPassword)){
            redirectAttributes.addFlashAttribute("passwordError", "Passwords do not match");
            return "redirect:/profile?error";
        }

        userService.updatePassword(user, password);
        request.getSession().invalidate();

        return "redirect:/login";
    }

}
