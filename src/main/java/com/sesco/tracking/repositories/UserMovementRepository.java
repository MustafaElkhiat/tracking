package com.sesco.tracking.repositories;

import com.sesco.tracking.models.AppUser;
import com.sesco.tracking.models.UserMovement;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Collection;

public interface UserMovementRepository extends MongoRepository<UserMovement,String> {
    Collection<UserMovement> findByUser(AppUser user);
    UserMovement findFirstByUser(AppUser user);
    UserMovement findTopByUserOrderByIdDesc(AppUser user);
    //UserMovement findTopByUser_UsernameOrderByIdDesc(String username);
}
