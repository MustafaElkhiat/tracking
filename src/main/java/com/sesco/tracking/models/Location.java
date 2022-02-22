package com.sesco.tracking.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Location {
    private String location;
    private String longitude;
    private String latitude;

    public Location(String location) {
        this.location = location;
    }

}