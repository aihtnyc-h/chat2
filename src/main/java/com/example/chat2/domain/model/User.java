package com.example.chat2.domain.model;


import com.example.chat2.domain.dto.UserDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity

public class User implements UserDetails {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 300, nullable = false, unique = true)
    private String email;

    @Column(length = 100, nullable = false)
    private String userName;

    @Column(length = 300, nullable = false)
    private String password;

    @Column(length = 300)
    private String userProfile; // 이미지

    @ElementCollection
    private List<String> userInterested; // 빈 배열

    @ElementCollection(fetch = FetchType.EAGER)
    @Builder.Default
    private List<String> roles = new ArrayList<>();

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.roles.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    @Override
    public String getUsername() {
        return userName;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    //프로필 업데이트
    public void update(UserDto userDto){
        this.id = userDto.getId();
        this.email = userDto.getEmail();
        this.userName = userDto.getUserName();
        this.userProfile = userDto.getUserProfile();
        this.userInterested = userDto.getUserInterested();
        this.roles = userDto.getRoles();
    }

}