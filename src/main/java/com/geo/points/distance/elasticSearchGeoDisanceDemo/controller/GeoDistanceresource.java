package com.geo.points.distance.elasticSearchGeoDisanceDemo.controller;

import com.geo.points.distance.elasticSearchGeoDisanceDemo.model.ApiResponse;
import com.geo.points.distance.elasticSearchGeoDisanceDemo.model.UserServiceabilityRequest;
import com.geo.points.distance.elasticSearchGeoDisanceDemo.model.UserServiceabilityResponse;
import com.geo.points.distance.elasticSearchGeoDisanceDemo.service.GeoDistanceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/v1")
@Slf4j
public class GeoDistanceresource {


    @Autowired
    GeoDistanceService geoDistanceService;

    @RequestMapping(value = "/users", method = RequestMethod.GET)
    public  ResponseEntity<ApiResponse<List<UserServiceabilityResponse>>> findUsers(@RequestBody UserServiceabilityRequest UserServiceabilityRequest){
        log.info("Get Retailer serviceable API ");
        List<UserServiceabilityResponse> retailerDetails = null;
        ApiResponse<List<UserServiceabilityResponse>> genericFormResponse = null;
        try {
            //retailerDetails = geoDistanceService.findServicableRetailersDetails(UserServiceabilityRequest);
        } catch (Exception e) {
            log.error("Error fetching the serviceable retailers details ", e);
        }
        return new ResponseEntity<>(genericFormResponse, HttpStatus.OK);

    }
    
}
