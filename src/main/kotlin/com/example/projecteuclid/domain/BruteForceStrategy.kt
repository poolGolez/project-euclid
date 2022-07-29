package com.example.projecteuclid.domain

import com.example.projecteuclid.repository.GeoPositionRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class BruteForceStrategy {

    @Autowired
    private lateinit var repository: GeoPositionRepository

    fun search(fixedGeoPosition: GeoPosition): GeoPosition? {
        val positions = repository.findAll()
        var bestDistanceSquared = Math.pow(180.0 - -180.0, 2.0) + Math.pow(90.0 - -90.0, 2.0)
        var closestGeoPosition: GeoPosition? = null

        for (position in positions) {
            val distanceSquared = fixedGeoPosition.distanceSquaredFrom(position)
            if (distanceSquared < bestDistanceSquared || closestGeoPosition == null) {
                bestDistanceSquared = distanceSquared
                closestGeoPosition = position
            }
        }

        return closestGeoPosition;
    }
}