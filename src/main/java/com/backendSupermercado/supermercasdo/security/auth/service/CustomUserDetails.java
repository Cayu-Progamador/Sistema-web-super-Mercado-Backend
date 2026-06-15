package com.backendSupermercado.supermercasdo.security.auth.service;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.backendSupermercado.supermercasdo.modules.usuario.entity.Usuario;

public class CustomUserDetails implements UserDetails{
    private Long id;
    private String username;
    private String password;
    private Collection<? extends GrantedAuthority> authorities;

    public CustomUserDetails(Usuario usuario, List<SimpleGrantedAuthority> authorities) {
        this.id = usuario.getIdUsuario();
        this.username = usuario.getUsername();
        this.password = usuario.getPassword();
        this.authorities = authorities;
    }

    public Long getId() {
        return id;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return true; }
}
