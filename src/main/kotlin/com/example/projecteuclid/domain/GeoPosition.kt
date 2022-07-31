package com.example.projecteuclid.domain

class GeoPosition(id: Long?, latitude: Double, longitude: Double) {
    val id = id;
    val latitude = latitude;
    val longitude = longitude;

    constructor(latitude: Double, longitude: Double) : this(null, latitude, longitude)

    fun distanceSquaredFrom(position: GeoPosition): Double {
        return Math.pow(latitude - position.latitude, 2.0) +
                Math.pow(longitude - position.longitude, 2.0)
    }

    fun compareLatitude(other: GeoPosition) = latitude - other.latitude

    fun compareLongitude(other: GeoPosition) = longitude - other.longitude

    override fun toString(): String {
        return "($latitude, $longitude)"
    }
}
