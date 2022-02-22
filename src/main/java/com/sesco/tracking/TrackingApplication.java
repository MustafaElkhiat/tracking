package com.sesco.tracking;

import com.sesco.tracking.models.AppUser;
import com.sesco.tracking.models.Authority;
import com.sesco.tracking.models.ROLE;
import com.sesco.tracking.repositories.UserMovementRepository;
import com.sesco.tracking.repositories.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@SpringBootApplication
public class TrackingApplication {

    public static void main(String[] args) {
        SpringApplication.run(TrackingApplication.class, args);
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    @Bean
    CommandLineRunner commandLineRunner(UserRepository userRepository, UserMovementRepository userMovementRepository) {
        return args -> {
            //DateTimeFormatter format = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
            DateTimeFormatter format = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            Optional<AppUser> developer = userRepository.findAppUserByUsername("developer");
            if(developer.isEmpty()){
                userRepository.insert(new AppUser("Mustafa Mohammed", "developer", passwordEncoder().encode("1234"), List.of(new Authority(ROLE.ADMIN_ROLE.toString()))));
            }


            //userRepository.insert(new AppUser("Ahmed", "a", passwordEncoder().encode("1234"), List.of(new SimpleGrantedAuthority(Role.FOLLOWER_ROLE.toString())),leader.get()));
            /*Optional<AppUser> user = userRepository.findAppUserByUsername("m");
            Location location = new Location("Cairo");
            userMovementRepository.insert(new UserMovement(LocalDate.now().format(format), LocalDate.now().format(format), location, user.get()));*/
        };
    }

}
