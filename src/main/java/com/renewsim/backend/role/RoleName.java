package com.renewsim.backend.role;

public enum RoleName {
    USER("User"),
    ADMIN("Administrator");

    private final String displayName;

    RoleName(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
