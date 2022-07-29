package com.example.projecteuclid.http

import com.example.projecteuclid.domain.ClosestPositionService
import com.example.projecteuclid.domain.Position
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RequestMapping("/closestPosition")
@RestController
class ClosestPositionController {

    @Autowired
    lateinit var closestPositionService: ClosestPositionService

    @GetMapping
    fun findClosestPosition(@RequestParam latitude: Double, @RequestParam longitude: Double): Position {
        val position = Position(latitude, longitude)
        return closestPositionService.findClosest(position)
    }

}

