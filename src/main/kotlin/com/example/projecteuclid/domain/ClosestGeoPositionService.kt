package com.example.projecteuclid.domain

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class ClosestGeoPositionService {

    @Autowired
    lateinit var strategy: BruteForceStrategy;

    fun findClosest(position: GeoPosition): GeoPosition? {
        return strategy.search(position);
    }
}