package com.sesco.tracking.models;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.sesco.tracking.deserializer.AppUserDeserializer;
import com.sesco.tracking.deserializer.UserMovementDeserializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Document
@JsonDeserialize(using = UserMovementDeserializer.class)
public class UserMovement {
    @Id
    private String id ;
    private String from ;
    private String to;
    private Location location;
    @DocumentReference
    private AppUser user;

    public UserMovement(String from, String to, Location location, AppUser user) {
        this.from = from;
        this.to = to;
        this.location = location;
        this.user = user;
    }
}
