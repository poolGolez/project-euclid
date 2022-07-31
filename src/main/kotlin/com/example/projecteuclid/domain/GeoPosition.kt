package com.example.projecteuclid.domain

import java.math.BigDecimal
import javax.validation.constraints.DecimalMax
import javax.validation.constraints.DecimalMin
import javax.validation.constraints.NotNull

class GeoPosition(id: Long?, latitude: BigDecimal, longitude: BigDecimal) {

    val id = id;

    @NotNull
    @DecimalMin("-90")
    @DecimalMax("90")
    val latitude = latitude;

    @NotNull
    @DecimalMin("-180")
    @DecimalMax("180")
    val longitude = longitude;

    constructor(latitude: BigDecimal, longitude: BigDecimal) : this(null, latitude, longitude)

    constructor(latitude: Double, longitude: Double) : this(null, latitude, longitude)
    constructor(id: Long?, latitude: Double, longitude: Double) : this(
        id,
        latitude.toBigDecimal(),
        longitude.toBigDecimal()
    )

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
