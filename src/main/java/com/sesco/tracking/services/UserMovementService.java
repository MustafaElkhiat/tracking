package com.sesco.tracking.services;

import com.sesco.tracking.models.AppUser;
import com.sesco.tracking.models.Location;
import com.sesco.tracking.models.UserMovement;
import com.sesco.tracking.repositories.UserMovementRepository;
import com.sesco.tracking.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserMovementService {
    private final UserMovementRepository userMovementRepository;
    private final UserRepository userRepository;
    private final UserService userService;
    //DateTimeFormatter format = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
    DateTimeFormatter format = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    public UserMovement getFirstMovementByUsername(String username) {
        Optional<AppUser> user = userRepository.findAppUserByUsername(username);
        if (user.isPresent())
            return userMovementRepository.findFirstByUser(user.get());
        else
            throw new UsernameNotFoundException("username not found");

    }

    public UserMovement getLastMovementByUsername(String username) {
        /*return userMovementRepository.findTopByUser_UsernameOrderByIdDesc(username);*/
        Optional<AppUser> user = userRepository.findAppUserByUsername(username);
        if (user.isPresent())
            return userMovementRepository.findTopByUserOrderByIdDesc(user.get());
        else
            throw new UsernameNotFoundException("username not found");

    }

    public Collection<UserMovement> getUserMovementByUser(String username) {
        Optional<AppUser> user = userRepository.findAppUserByUsername(username);
        if (user.isPresent())
            return userMovementRepository.findByUser(user.get());
        else
            throw new UsernameNotFoundException("username not found");
    }

    public Collection<UserMovement> getFollowersLastMovementByLeaderUsername(String username) throws Exception {
        Collection<AppUser> followers = userService.getFollowersByLeaderUsername(username);
        return getFollowersLastMovement(followers);
    }

    public Collection<UserMovement> getFollowersLastMovement(Collection<AppUser> followers) throws Exception {
        Collection<UserMovement> lastMovementCollection = new ArrayList<>();
        followers.forEach(
                follower -> {
                    lastMovementCollection.add(getLastMovementByUsername(follower.getUsername()));
                }
        );
        return lastMovementCollection;
    }

    public void addUserMovement(String from, String to, Location location, AppUser user) {
        userMovementRepository.insert(new UserMovement(from, to, location, user));
    }

    public void addUserMovement(UserMovement userMovement) {
        userMovementRepository.insert(userMovement);
    }

}
