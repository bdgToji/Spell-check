package com.example.ai_spell_check.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping({"/","/index"})
    public String getHomePage(Model model) {
        model.addAttribute("bodyContent", "index");
        return "master-template";
    }
}