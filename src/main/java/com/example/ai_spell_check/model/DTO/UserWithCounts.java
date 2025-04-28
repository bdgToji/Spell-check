package com.example.ai_spell_check.model.DTO;

import com.example.ai_spell_check.model.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class UserWithCounts {
    private User user;
    private int textEntries;
    private int documents;

    public String getId(){
        return user.getId();
    }
}
