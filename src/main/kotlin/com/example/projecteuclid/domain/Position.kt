package com.example.projecteuclid.domain

class Position(id: Long?, latitude: Double, longitude: Double) {

    val id = id;
    val latitude = latitude;
    val longitude = longitude;

    constructor(latitude: Double, longitude: Double) : this(null, latitude, longitude)
}