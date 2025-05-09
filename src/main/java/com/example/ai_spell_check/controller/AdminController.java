package com.example.ai_spell_check.controller;

import com.example.ai_spell_check.model.DTO.UserDetailsDto;
import com.example.ai_spell_check.model.DTO.UserWithCountsDto;
import com.example.ai_spell_check.model.User;
import com.example.ai_spell_check.service.UserService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {
    private final UserService userService;


    public AdminController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public String getUsers(Model model){
        List<UserWithCountsDto> usersWithCounts = userService.listAllUsersWithCounts();
        model.addAttribute("users", usersWithCounts);
        model.addAttribute("bodyContent", "admin-users");

        return "master-template";
    }


    @GetMapping("/users/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional(readOnly = true)
    public String getUserDetailsPage(
            @PathVariable String id,
            @RequestParam(name="pageNum", defaultValue = "1") int pageNum,
            @RequestParam(name="pageSize", defaultValue = "10") int pageSize,
            Model model) {
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize, Sort.by(Sort.Direction.DESC, "id"));
        UserDetailsDto user = userService.findUserByIdWithTextEntriesAndDocuments(id, pageable);

        model.addAttribute("userDetails", user);
        model.addAttribute("page", user.getCorrectionHistoryPage());
        model.addAttribute("bodyContent", "admin-user-details");

        return "master-template";
    }

    @GetMapping("/users/update/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional(readOnly = true)
    public String getUpdateUserPage(@PathVariable String id, Model model){
        User user = userService.findUserById(id);
        model.addAttribute("user", user);
        model.addAttribute("bodyContent", "admin-user-edit");

        return "master-template";
    }

    @PostMapping("/users/edit")
    @PreAuthorize("hasRole('ADMIN')")
    public String updateUser(@RequestParam(required = false) String id,
                             @RequestParam String username,
                             @RequestParam String email,
                             @RequestParam String password){
        userService.updateUser(id, username, email, password);

        return "redirect:/admin/users";
    }

    @PostMapping("/users/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String deleteUser(@PathVariable String id){
        userService.deleteUserById(id);

        return "redirect:/admin/users";
    }
}
