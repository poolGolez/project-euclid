package com.example.projecteuclid.domain

import java.math.BigDecimal

class GeoPosition(id: Long?, latitude: BigDecimal, longitude: BigDecimal) {

    val id = id;
    val latitude = latitude;
    val longitude = longitude;

    constructor(latitude: BigDecimal, longitude: BigDecimal) : this(null, latitude, longitude)

    constructor(latitude: Double, longitude: Double) : this(null, latitude.toBigDecimal(), longitude.toBigDecimal())

    fun distanceSquaredFrom(position: GeoPosition): BigDecimal {
        return (latitude - position.latitude).pow(2) +
                (longitude - position.longitude).pow(2)
    }

    fun compareLatitude(other: GeoPosition) = latitude - other.latitude

    fun compareLongitude(other: GeoPosition) = longitude - other.longitude

    override fun toString(): String {
        return "($latitude, $longitude)"
    }
}
