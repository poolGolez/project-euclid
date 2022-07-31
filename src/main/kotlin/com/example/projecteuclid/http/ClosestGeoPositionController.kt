package com.example.projecteuclid.http

import com.example.projecteuclid.domain.ClosestGeoPositionService
import com.example.projecteuclid.domain.GeoPosition
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RequestMapping("/closestPosition")
@RestController
class ClosestGeoPositionController {

    @Autowired
    private lateinit var service: ClosestGeoPositionService

    @GetMapping
    fun findClosestPosition(@RequestParam latitude: Double, @RequestParam longitude: Double): GeoPosition? {
        val position = GeoPosition(latitude, longitude)
        return service.findClosest(position)
    }

}

