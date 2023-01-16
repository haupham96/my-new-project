package com.example.oath2springsecurity5.dto.oauth2;

import com.example.oath2springsecurity5.Provider;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@NoArgsConstructor
@AllArgsConstructor
@Setter
public class OAuth2UserImpl implements OAuth2User {
    private String username;
    private Map<String, Object> attributes;
    private List<SimpleGrantedAuthority> authorities;

    @Override
    public Map<String, Object> getAttributes() {
        return this.attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.authorities;
    }

    @Override
    public String getName() {
        return this.username;
    }
}
