package com.example.ai_spell_check.service;


import com.example.ai_spell_check.model.User;
import com.example.ai_spell_check.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }
    public void createUser(String username, String email, String password){
        User user = new User(username,email, passwordEncoder.encode(password));
        userRepository.save(user);
    }
}
