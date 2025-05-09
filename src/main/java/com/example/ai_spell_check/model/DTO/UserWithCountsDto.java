package com.example.ai_spell_check.model.DTO;

import com.example.ai_spell_check.model.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class UserWithCountsDto {
    private User user;
    private long textEntries;
    private long documents;
}
