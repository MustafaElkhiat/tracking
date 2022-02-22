package com.sesco.tracking.repositories;

import com.sesco.tracking.models.AppUser;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.Optional;

public interface UserRepository extends MongoRepository<AppUser, String> {
    Optional<AppUser>
    findAppUserByUsername(String username);

    Optional<Collection<AppUser>> findAppUsersByAuthoritiesContaining(Collection<GrantedAuthority> authorities);

    Optional<AppUser> findAppUserByLeader_Username(String username);

    Optional<Collection<AppUser>> findAppUsersByLeader_Username(String username);

    Optional<Collection<AppUser>> findAppUsersByLeader(AppUser leader);
}
