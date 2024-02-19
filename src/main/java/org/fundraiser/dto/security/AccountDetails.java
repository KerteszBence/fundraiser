package org.fundraiser.dto.security;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
public class AccountDetails {

    private Long accountId;

    private String username;

    private List<String> roles;

    public AccountDetails(Long id, User user) {
        this.accountId = id;
        this.username = user.getUsername();
        this.roles = loadRoles(user.getAuthorities());
    }

    private List<String> loadRoles(Collection<GrantedAuthority> authorities) {
        return authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
    }

}