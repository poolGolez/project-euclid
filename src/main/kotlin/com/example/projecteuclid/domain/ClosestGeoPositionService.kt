package com.example.projecteuclid.domain

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service

@Service
class ClosestGeoPositionService {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    @Qualifier("k-d tree search")
//    @Qualifier("brute force search")
    private lateinit var strategy: GeoPositionSearchStrategy

    fun findClosest(position: GeoPosition): GeoPosition? {
        val closestPosition = strategy.search(position)
        val distance = if (closestPosition != null)
            position.distanceSquaredFrom(closestPosition)
        else
            0.0
        logger.info("Closest point to $position: $closestPosition (d=$distance)")
        return closestPosition
    }
}