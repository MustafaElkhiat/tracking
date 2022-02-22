package com.sesco.tracking.models;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.sesco.tracking.deserializer.AppUserDeserializer;
import lombok.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.Collection;

@Setter
@Getter
@NoArgsConstructor
@ToString

@Document(collection = "user")
@JsonDeserialize(using = AppUserDeserializer.class)
public class AppUser implements UserDetails {


    @Id
    private String id;
    private String name;
    private AppUser leader;
    private String password = passwordEncoder().encode("0000");
    private String username;
    private Collection<Authority> authorities = new ArrayList<>();
    private boolean accountNonExpired = true;
    private boolean accountNonLocked = true;
    private boolean credentialsNonExpired = true;
    private boolean enabled = true;
    private boolean activated = false;

    public AppUser(String name, String username, String password, Collection<Authority> authorities, AppUser leader) {
        this.name = name;
        this.leader = leader;
        this.password = password;
        this.username = username;
        this.authorities = authorities;
    }

    public AppUser(String name, String username, String password, Collection<Authority> authorities) {
        this.name = name;
        this.password = password;
        this.username = username;
        this.authorities = authorities;
    }

    public AppUser(String name, AppUser leader, String username, Collection<Authority> authorities) {
        this.name = name;
        this.leader = leader;
        this.username = username;
        this.authorities = authorities;
    }

    public AppUser(String name, String username, Collection<Authority> authorities) {
        this.name = name;
        this.username = username;
        this.authorities = authorities;
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
