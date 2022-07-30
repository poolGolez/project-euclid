package com.example.projecteuclid.domain

interface GeoPositionSearchStrategy {
    fun search(fixedGeoPosition: GeoPosition): GeoPosition?
}