package com.example.ai_spell_check.bootstrap;

import com.example.ai_spell_check.model.User;
import com.example.ai_spell_check.model.enums.UserRole;
import com.example.ai_spell_check.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class DataHolder {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataHolder(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostConstruct
    public void init(){
//        User user = new User(
//                "admin_admin",
//                "adminA@gmail.com",
//                passwordEncoder.encode("Admin123!")
//        );
//
//        user.setRole(UserRole.ADMIN);
//
//        userRepository.save(user);
    }
}
