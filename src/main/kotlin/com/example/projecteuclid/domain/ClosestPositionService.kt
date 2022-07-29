package com.example.projecteuclid.domain

import org.springframework.stereotype.Service

@Service
class ClosestPositionService {

    fun findClosest(position: Position): Position {
        return Position(1023, -31.3312, 114.1617);
    }
}