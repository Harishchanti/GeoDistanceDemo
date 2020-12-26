package com.geo.points.distance.elasticSearchGeoDisanceDemo.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;


@JsonInclude(JsonInclude.Include.NON_NULL)
@EqualsAndHashCode
@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserDocument implements Serializable {

    Long id;
    String name;
    String mobileNumber;
    String streetAddress;
    String city;
    String state;
    String pincode;
    Location location;
    Double distance;
}

