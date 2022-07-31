package com.example.projecteuclid.domain

import com.example.projecteuclid.repository.GeoPositionRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component

@Component
@Qualifier("brute force search")
class BruteForceSearchStrategy : GeoPositionSearchStrategy {

    @Autowired
    private lateinit var repository: GeoPositionRepository

    override fun search(fixedGeoPosition: GeoPosition): GeoPosition? {
        val positions = repository.findAll()

        val extremeLowerLeftValue = GeoPosition(-90.0, -180.0)
        val extremeUpperRightValue = GeoPosition(90.0, 180.0)
        var maxDistanceSquared = extremeLowerLeftValue.distanceSquaredFrom(extremeUpperRightValue)

        var closestGeoPosition: GeoPosition? = null

        for (position in positions) {
            val distanceSquared = fixedGeoPosition.distanceSquaredFrom(position)
            if (distanceSquared < maxDistanceSquared || closestGeoPosition == null) {
                maxDistanceSquared = distanceSquared
                closestGeoPosition = position
            }
        }

        return closestGeoPosition;
    }
}