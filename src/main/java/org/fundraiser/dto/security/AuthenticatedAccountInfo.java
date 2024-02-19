package org.fundraiser.dto.security;

import org.fundraiser.config.AccountType;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;
import java.util.stream.Collectors;

public class AuthenticatedAccountInfo {

    private final String username;
    private final List<AccountType> roles;

    public AuthenticatedAccountInfo(UserDetails userDetails) {
        this.username = userDetails.getUsername();
        this.roles = parseRoles(userDetails);
    }

    private List<AccountType> parseRoles(UserDetails user) {
        return user.getAuthorities()
                .stream()
                .map(authority -> AccountType.valueOf(authority.getAuthority()))
                .collect(Collectors.toList());
    }

    public String getUsername() {
        return username;
    }

    public List<AccountType> getRoles() {
        return roles;
    }

}