package com.sesco.tracking.services;

import com.sesco.tracking.models.AppUser;
import com.sesco.tracking.models.Authority;
import com.sesco.tracking.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<AppUser> userOptional = userRepository.findAppUserByUsername(username);
        userOptional.ifPresentOrElse(userValue -> {
                    log.info("User found in Database {}", username);
                }, () -> {
                    log.error("User Not found in Database");
                    throw new UsernameNotFoundException("User not fount in database");
                }

        );
        AppUser user = userOptional.get();
        return user;
        //Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
        //user.getRoles().forEach(role -> authorities.add(new SimpleGrantedAuthority(role.toString())));
        //return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), user.getAuthorities());
    }

    public Collection<AppUser> getAllUsers() {
        return userRepository.findAll();
    }

    public Collection<AppUser> getUsersByRole(Authority authority) {
        return userRepository.findAppUsersByAuthoritiesContaining(List.of(authority)).get();
    }

    public Collection<AppUser> getLeaders() {
        return userRepository.findAppUsersByAuthoritiesContaining(List.of(new Authority("LEADER_ROLE"))).get();
    }

    public AppUser getFollowerByLeaderUsername(String username) {
        Optional<AppUser> follower = userRepository.findAppUserByLeader_Username(username);
        if (follower.isEmpty())
            throw new RuntimeException("No Follower for this username");
        return follower.get();

    }

    public Collection<AppUser> getFollowersByLeaderUsername(String username) {
        Optional<Collection<AppUser>> followers = userRepository.findAppUsersByLeader_Username(username);
        if (followers.isEmpty())
            throw new RuntimeException("Not Found followers for this leader username : " + username);
        return followers.get();
        /*Optional<AppUser> leader = userRepository.findAppUserByUsername(username);

        if (leader.isPresent()) {
            System.out.println(leader.get().getName());
            if (leader.get().getAuthorities().contains(new SimpleGrantedAuthority(Role.LEADER_ROLE.toString()))) {
                System.out.println("Yessss");
                Optional<Collection<AppUser>> followers = userRepository.findAppUsersByLeader(leader.get());
                if (followers.isPresent()) {
                    System.out.println(followers.get().size());
                    return followers.get();
                } else
                    throw new Exception("Not found follower for this leader =" + leader.get().getName());
            } else
                throw new Exception(leader.get().getName() + " isn't leader");
        } else
            throw new UsernameNotFoundException("this user not found");*/
    }

    public AppUser getUserByUsername(String username) {
        Optional<AppUser> userOptional = userRepository.findAppUserByUsername(username);
        if (userOptional.isPresent()) {
            return userOptional.get();
        } else {
            throw new UsernameNotFoundException("this username not found");
        }
    }

    public void addUser(AppUser user) {
        userRepository.findAppUserByUsername(user.getUsername()).ifPresentOrElse(userValue -> {
            log.error("Username is already used");
            throw new RuntimeException("this username is already used");
        }, () -> {
            log.info("user {} has been added", user.getName());
            userRepository.insert(user);
        });

    }

    public AppUser activateUser(AppUser user, String password) {
        Optional<AppUser> userOptional = userRepository.findAppUserByUsername(user.getUsername());
        if (userOptional.isPresent()) {
            userOptional.get().setActivated(true);
            userOptional.get().setPassword(passwordEncoder.encode(password));
            userRepository.save(userOptional.get());
            return userOptional.get();
        }
        return null;
    }


}
