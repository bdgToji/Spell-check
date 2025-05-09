package com.example.ai_spell_check.repository;

import com.example.ai_spell_check.model.DTO.UserWithCountsDto;
import com.example.ai_spell_check.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,String> {
    Optional<User> findByUsername(String username);

    @Query("SELECT new com.example.ai_spell_check.model.DTO.UserWithCountsDto(u, " +
            "(SELECT COUNT(t) FROM TextEntry t WHERE t.user = u), " +
            "(SELECT COUNT(d) FROM Document d WHERE d.user = u)) " +
            "FROM User u")
    List<UserWithCountsDto> findAllUsersWithCounts();
}
