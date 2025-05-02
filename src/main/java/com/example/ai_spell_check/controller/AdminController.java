package com.example.ai_spell_check.controller;

import com.example.ai_spell_check.model.DTO.UserDetailsDTO;
import com.example.ai_spell_check.model.DTO.UserWithCounts;
import com.example.ai_spell_check.model.Document;
import com.example.ai_spell_check.model.TextEntry;
import com.example.ai_spell_check.model.User;
import com.example.ai_spell_check.service.DocumentService;
import com.example.ai_spell_check.service.TextEntryService;
import com.example.ai_spell_check.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {
    private final UserService userService;
    private final DocumentService documentService;
    private final TextEntryService textEntryService;

    public AdminController(UserService userService, DocumentService documentService, TextEntryService textEntryService) {
        this.userService = userService;
        this.documentService = documentService;
        this.textEntryService = textEntryService;
    }

    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public String getUsers(Model model){
        List<User> users = userService.listAllUsers();
        List<UserWithCounts> usersWithCounts = new ArrayList<>();
        for(User user: users){
            int textCount = userService.countTextEntries(user.getUsername());
            int docCount = userService.countDocuments(user.getUsername());
            usersWithCounts.add(new UserWithCounts(user, textCount, docCount));
        }
        model.addAttribute("users", usersWithCounts);
        model.addAttribute("bodyContent", "admin-users");

        return "master-template";
    }

    @GetMapping("/users/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional(readOnly = true)
    public String getUpdateUserPage(@PathVariable String id,
                                    @RequestParam(name="docPage", defaultValue = "0") int docPage,
                                    @RequestParam(name="textPage", defaultValue = "0") int textPage,
                                    Model model){
        User user = userService.findUserById(id);
        Page<Document> documents = documentService.getDocumentsByUser(user, PageRequest.of(docPage, 3));
        Page<TextEntry> textEntries = textEntryService.getTextEntriesByUser(user, PageRequest.of(textPage, 3));

        model.addAttribute("userDetails", new UserDetailsDTO(user, documents, textEntries));
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
    public String deleteUser(@PathVariable String id,
                             Model model){
        userService.deleteUserById(id);
        return "redirect:/admin/users";
    }
}
