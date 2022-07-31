package com.example.projecteuclid.http

import com.example.projecteuclid.domain.ClosestGeoPositionService
import com.example.projecteuclid.domain.GeoPosition
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import javax.validation.ConstraintViolationException


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

    @ExceptionHandler(ConstraintViolationException::class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    fun handleConstraintViolationException(e: ConstraintViolationException): Map<String, String?> {
        return mapOf(
            Pair("error", e.javaClass.simpleName),
            Pair("message", e.message)
        )
    }

}

