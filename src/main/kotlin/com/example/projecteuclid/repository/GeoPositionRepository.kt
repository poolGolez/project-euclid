package com.example.projecteuclid.repository

import com.example.projecteuclid.domain.GeoPosition
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.util.ResourceUtils
import java.nio.file.Files

@Component
class GeoPositionRepository {

    @Value("\${repository.points.fileLocation}")
    private lateinit var pointsFileLocation: String

    fun findAll(): List<GeoPosition> {
        val pointsFile = ResourceUtils.getFile(pointsFileLocation)
        val contents = String(Files.readAllBytes(pointsFile.toPath()))

        val mapper = jacksonObjectMapper()
        val typeReference: TypeReference<List<GeoPosition>> = object : TypeReference<List<GeoPosition>>() {}
        return mapper.readValue(contents, typeReference)
    }
}