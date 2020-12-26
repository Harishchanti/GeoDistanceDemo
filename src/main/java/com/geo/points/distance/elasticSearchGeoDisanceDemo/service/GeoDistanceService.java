package com.geo.points.distance.elasticSearchGeoDisanceDemo.service;

import com.geo.points.distance.elasticSearchGeoDisanceDemo.utils.ElasticSearchUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Slf4j
public class GeoDistanceService {

    @Autowired
    ElasticSearchUtils utils;

    @PostConstruct
    public void loadRetailerDetails() {
        updateRetailerDetailsEs();
    }

    public void updateRetailerDetailsEs() {

        log.info("Tryinh tot create user Indexing...");
        try {
            boolean isIndexExist = utils.isAlreadyDataPublished();
            log.info("isIndexExist {}", isIndexExist);
        } catch (Exception e) {
            log.error("Error while populating retailer details in ES", e);
        }
    }


}
