package com.example.ai_spell_check.model.enums;

public enum UserRole {
    USER, ADMIN;
    public String roleName() {
        return "ROLE_" + this.name();
    }
}

