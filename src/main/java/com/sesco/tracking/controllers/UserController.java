package com.sesco.tracking.controllers;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sesco.tracking.models.ActivationUser;
import com.sesco.tracking.models.AppUser;
import com.sesco.tracking.models.UserMovement;
import com.sesco.tracking.services.UserMovementService;
import com.sesco.tracking.services.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserService userService;
    private final UserMovementService userMovementService;

    @GetMapping
    public Collection<AppUser> getAllUsers() {
        return userService.getAllUsers();
    }

   /* @GetMapping("/byRole")
    public Collection<User> getUsersByRole(@RequestParam String role) {
        return userService.getUsersByRole(Role.valueOf(role));
    }*/

    @GetMapping("/userAllMoves")
    public Collection<UserMovement> getUserMovementByUsername(@RequestParam String username) {
        return userMovementService.getUserMovementByUser(username);
    }

    @GetMapping("/userFirstMove")
    public UserMovement getFirstMovementByUsername(@RequestParam String username) {
        return userMovementService.getFirstMovementByUsername(username);
    }

    @GetMapping("/userLastMove")
    public UserMovement getLastMovementByUsername(@RequestParam String username) {
        return userMovementService.getLastMovementByUsername(username);
    }

    @GetMapping("/followerOfLeader")
    public AppUser getFollowerOfLeader(@RequestParam String username) {
        return userService.getFollowerByLeaderUsername(username);
    }

    @GetMapping("/followersOfLeader")
    public Collection<AppUser> getFollowersOfLeader(@RequestParam String username) throws Exception {
        return userService.getFollowersByLeaderUsername(username);
    }

    @GetMapping("/lastMoveOfFollowersOfLeader")
    public Collection<UserMovement> getLastMoveOfFollowersOfLeader(@RequestParam String username) throws Exception {
        return userMovementService.getFollowersLastMovementByLeaderUsername(username);
    }

    @GetMapping("/lastMoveOfUser")
    public Collection<UserMovement> getLastMoveOfUser(@RequestParam String username) {
        Collection<UserMovement> userMovementCollection = new ArrayList<>();
        userMovementCollection.add(userMovementService.getLastMovementByUsername(username));
        return userMovementCollection;
    }

    @GetMapping("/refreshToken")
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String authorizationHeader = request.getHeader(AUTHORIZATION);
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            try {
                String refreshToken = authorizationHeader.substring("Bearer ".length());
                Algorithm algorithm = Algorithm.HMAC256("secret".getBytes());
                JWTVerifier verifier = JWT.require(algorithm).build();
                DecodedJWT decodedJWT = verifier.verify(refreshToken);
                String username = decodedJWT.getSubject();
                AppUser user = userService.getUserByUsername(username);
               /* Collection<GrantedAuthority> authorities = new ArrayList<>();
                user.getRoles().forEach(role -> {
                    authorities.add(new SimpleGrantedAuthority("role"));
                });*/
                String accessToken = JWT.create().withSubject(user.getUsername())
                        .withExpiresAt(new Date(System.currentTimeMillis() + 10 * 60 * 1000))
                        .withIssuer(request.getRequestURL().toString())
                        .withClaim("roles", user.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
                        .sign(algorithm);
                Map<String, Object> data = new HashMap<>();
                data.put("access_token", accessToken);
                data.put("refresh_token", refreshToken);
                data.put("user", user);
                response.setContentType(APPLICATION_JSON_VALUE);
                new ObjectMapper().writeValue(response.getOutputStream(), data);
                /*response.getOutputStream().println("Access Token : " + accessToken);
                response.getOutputStream().println("Refresh Token : " + refreshToken);*/

            } catch (Exception exception) {
                log.error("Error logging in {}", exception.getMessage());
                response.setStatus(UNAUTHORIZED.value());
                Map<String, String> error = new HashMap<>();
                error.put("error_message", exception.getMessage());
                response.setContentType(APPLICATION_JSON_VALUE);
                new ObjectMapper().writeValue(response.getOutputStream(), error);
            }
        } else {
            throw new RuntimeException("Refresh token is missing");
        }
    }

    @PostMapping(value = "/user", consumes = APPLICATION_JSON_VALUE)
    public void addUser(@RequestBody AppUser user) {
        userService.addUser(user);

    }

    @PostMapping(value = "/userMovement", consumes = APPLICATION_JSON_VALUE)
    public void addUserMovement(@RequestBody UserMovement userMovement) {
        System.out.println("adding user movement");
        log.info("{} add movement", userMovement.getUser().getName());
        userMovementService.addUserMovement(userMovement);
        /*Location location = new Location(payload.get("location"), payload.get("longitude"), payload.get("latitude"));
        AppUser user = userService.getUserByUsername(payload.get("username"));
        userMovementService.addUserMovement(payload.get("from"), payload.get("to"), location, user);*/
    }

    @GetMapping("/leaders")
    public Collection<AppUser> getLeaders() {
        return userService.getLeaders();
    }

    @PutMapping(value = "/activate", consumes = APPLICATION_JSON_VALUE,produces =APPLICATION_JSON_VALUE )
    public AppUser activateUser(@RequestBody ActivationUser activationUser) {
        System.out.println("user : "+activationUser);
       return userService.activateUser(activationUser.getUser(), activationUser.getPassword());
    }

}
