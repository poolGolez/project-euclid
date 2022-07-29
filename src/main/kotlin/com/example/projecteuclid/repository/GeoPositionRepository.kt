package com.example.projecteuclid.repository

import com.example.projecteuclid.domain.GeoPosition
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.springframework.stereotype.Component
import org.springframework.util.ResourceUtils
import java.nio.file.Files

@Component
class GeoPositionRepository {

    fun findAll(): List<GeoPosition> {
        val pointsFile = ResourceUtils.getFile("classpath:points.json")
        val contents = String(Files.readAllBytes(pointsFile.toPath()))

        val mapper = jacksonObjectMapper()
        val typeReference: TypeReference<List<GeoPosition>> = object : TypeReference<List<GeoPosition>>() {}
        return mapper.readValue(contents, typeReference)
    }
}