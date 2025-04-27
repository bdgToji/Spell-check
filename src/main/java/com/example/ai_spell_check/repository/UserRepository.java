package com.example.ai_spell_check.repository;

import com.example.ai_spell_check.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,String> {
    Optional<User> findByUsername(String username);

    @Query("SELECT COUNT(t) FROM TextEntry t WHERE t.user.username = :username")
    int countTextEntries(@Param("username") String username);

    @Query("SELECT COUNT(d) FROM Document d WHERE d.user.username = :username")
    int countDocuments(@Param("username") String username);
}
