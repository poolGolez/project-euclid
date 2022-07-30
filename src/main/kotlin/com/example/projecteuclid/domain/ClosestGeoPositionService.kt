package com.example.projecteuclid.domain

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service

@Service
class ClosestGeoPositionService {

    @Autowired
    @Qualifier("k-d tree search")
//    @Qualifier("brute force search")
    lateinit var strategy: GeoPositionSearchStrategy

    fun findClosest(position: GeoPosition): GeoPosition? {
        return strategy.search(position)
    }
}