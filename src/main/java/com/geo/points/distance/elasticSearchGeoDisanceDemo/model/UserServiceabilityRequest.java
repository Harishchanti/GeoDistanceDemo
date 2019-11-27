package com.geo.points.distance.elasticSearchGeoDisanceDemo.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@JsonInclude(Include.NON_NULL)
@EqualsAndHashCode
@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserServiceabilityRequest {

    Double latitude;
    Double longitude;
    Double radius;
    Integer pageNumber;
    Integer pageSize;

}