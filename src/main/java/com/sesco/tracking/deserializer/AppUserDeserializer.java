package com.sesco.tracking.deserializer;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.sesco.tracking.models.AppUser;
import com.sesco.tracking.models.Authority;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.io.IOException;
import java.util.Iterator;

@RequiredArgsConstructor
public class AppUserDeserializer extends JsonDeserializer<AppUser> {
    private final PasswordEncoder passwordEncoder;

    @Override
    public AppUser deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JacksonException {
        AppUser appUser = new AppUser();
        ObjectCodec objectCodec = jsonParser.getCodec();
        JsonNode jsonNode = objectCodec.readTree(jsonParser);

        appUser.setName(jsonNode.get("name").asText());
        appUser.setUsername(jsonNode.get("username").asText());
        if (jsonNode.get("password")!= null )
            appUser.setPassword(passwordEncoder.encode(jsonNode.get("password").asText()));
        Iterator<JsonNode> elements = jsonNode.get("authorities").elements();
        while (elements.hasNext()) {
            JsonNode next = elements.next();
            JsonNode authority = next.get("authority");
            appUser.getAuthorities().add(new Authority(authority.asText()));
        }
        if (!jsonNode.get("leader").isNull()) {
            AppUser leader = new AppUser();
            leader.setName(jsonNode.get("leader").get("name").asText());
            leader.setUsername(jsonNode.get("leader").get("username").asText());
            leader.setPassword(jsonNode.get("leader").get("password").asText());
            Iterator<JsonNode> leaderElements = jsonNode.get("leader").get("authorities").elements();
            while (leaderElements.hasNext()) {
                JsonNode next = leaderElements.next();
                JsonNode authority = next.get("authority");
                leader.getAuthorities().add(new Authority(authority.asText()));
            }

            appUser.setLeader(leader);
        }
        return appUser;
    }
}
