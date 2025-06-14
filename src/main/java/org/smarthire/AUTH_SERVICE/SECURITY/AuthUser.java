package org.smarthire.AUTH_SERVICE.SECURITY;

import java.util.Collection;
import java.util.List;

import org.smarthire.AUTH_SERVICE.MODELS.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import lombok.AllArgsConstructor;
import lombok.Builder;

@AllArgsConstructor
@Builder
public class AuthUser implements UserDetails {

    private User userEO;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Prefix role with "ROLE_" as Spring Security convention
        String roleName = userEO.getRole().getRoleName();
        String authority = roleName.startsWith("ROLE_") ? roleName : "ROLE_" + roleName;

        return List.of(new SimpleGrantedAuthority(authority));
    }

    @Override
    public String getPassword() {
        return userEO.getPassword();
    }

    @Override
    public String getUsername() {
        return userEO.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // Implement account expiration logic if needed
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // Implement account locking logic if needed
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // Implement credential expiration logic if needed
    }

    @Override
    public boolean isEnabled() {
        return userEO.getEnable() != null ? userEO.getEnable() : false;
    }

    // Custom getters
    public Long getUserId() {
        return userEO.getId();
    }

    public User getUser() {
        return userEO;
    }

    public String getEmail() {
        return userEO.getEmail();
    }

    public String getPhoneNumber() {
        return userEO.getPhoneNumber();
    }

    public String getRoleName() {
        return userEO.getRole().getRoleName();
    }
}