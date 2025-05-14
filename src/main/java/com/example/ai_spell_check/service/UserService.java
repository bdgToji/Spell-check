package com.example.ai_spell_check.service;


import com.example.ai_spell_check.model.DTO.CorrectionHistoryDto;
import com.example.ai_spell_check.model.DTO.UserDetailsDto;
import com.example.ai_spell_check.model.DTO.UserWithCountsDto;
import com.example.ai_spell_check.model.User;
import com.example.ai_spell_check.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final CorrectionHistoryService correctionHistoryService;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, CorrectionHistoryService correctionHistoryService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.correctionHistoryService = correctionHistoryService;
    }
    public void createUser(String username, String email, String password){
        User user = new User(username,email, passwordEncoder.encode(password));
        userRepository.save(user);
    }
    public User findByUsername(String username) {
        return userRepository.findByUsername(username).orElseThrow();
    }

    public void updateInfo(UserDetails userDetails, String newUsername, String email) {
        User user = findByUsername(userDetails.getUsername());

        boolean usernameChanged = !user.getUsername().equals(newUsername);

        user.setUsername(newUsername);
        user.setEmail(email);
        userRepository.save(user);

        if (usernameChanged) {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            List<GrantedAuthority> updatedAuthorities = new ArrayList<>(auth.getAuthorities());
            Authentication newAuth = new UsernamePasswordAuthenticationToken(
                    newUsername, auth.getCredentials(), updatedAuthorities);
            SecurityContextHolder.getContext().setAuthentication(newAuth);
        }
    }

    public void updatePassword(UserDetails user, String password){
        User userObj = findByUsername(user.getUsername());
        userObj.setPassword(passwordEncoder.encode(password));
        userRepository.save(userObj);
    }

    public List<User> listAllUsers(){
        return userRepository.findAll();
    }

    public List<UserWithCountsDto> listAllUsersWithCounts() {
        return userRepository.findAllUsersWithCounts();
    }

    public User findUserById(String id){
        return userRepository.findById(id).orElseThrow();
    }

    @Transactional
    public void updateUser(String id, String username, String email, String password){
        User user = userRepository.findById(id).orElseThrow();
        boolean usernameChanged = !user.getUsername().equals(username);

        if(usernameChanged){
            user.setUsername(username);
        }
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));

        userRepository.save(user);
    }

    public void deleteUserById(String id){
        User user = userRepository.findById(id).orElseThrow();
        userRepository.delete(user);
    }

    public UserDetailsDto findUserByIdWithTextEntriesAndDocuments(String id, Pageable pageable) {
        User userObj = userRepository.findById(id).orElseThrow();
        Page<CorrectionHistoryDto> correctionHistoryPage = correctionHistoryService.getCombinedCorrectionHistory(id, pageable);

        return new UserDetailsDto(userObj, correctionHistoryPage);
    }
}
