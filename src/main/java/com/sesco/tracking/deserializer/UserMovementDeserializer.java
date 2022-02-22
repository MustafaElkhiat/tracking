package com.sesco.tracking.deserializer;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.sesco.tracking.models.AppUser;
import com.sesco.tracking.models.Location;
import com.sesco.tracking.models.UserMovement;
import com.sesco.tracking.services.UserService;
import lombok.RequiredArgsConstructor;

import java.io.IOException;

@RequiredArgsConstructor
public class UserMovementDeserializer extends JsonDeserializer<UserMovement> {
    final UserService userService;

    @Override
    public UserMovement deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JacksonException {
        UserMovement userMovement = new UserMovement();
        ObjectCodec objectCodec = jsonParser.getCodec();
        JsonNode jsonNode = objectCodec.readTree(jsonParser);
        System.out.println("jsonNode :" + jsonNode.asText());
        userMovement.setFrom(jsonNode.get("from").asText());
        userMovement.setTo(jsonNode.get("to").asText());
        Location location = new Location();
        location.setLatitude(jsonNode.get("location").get("latitude").asText());
        location.setLongitude(jsonNode.get("location").get("longitude").asText());
        location.setLocation(jsonNode.get("location").get("location").asText());
        userMovement.setLocation(location);
        AppUser user = userService.getUserByUsername(jsonNode.get("user").get("username").asText());
        userMovement.setUser(user);
        return userMovement;
    }
}
