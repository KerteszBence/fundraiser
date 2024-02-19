package org.fundraiser.config;

public enum AccountType {
    ROLE_DONOR("DONOR"),
    ROLE_CREATOR("CREATOR"),
    ROLE_ADMIN("ADMIN");
    private final String role;

    AccountType(String role) {
        this.role = role;
    }
}
