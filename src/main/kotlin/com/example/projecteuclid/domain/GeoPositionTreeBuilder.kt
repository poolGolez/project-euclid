package com.example.projecteuclid.domain

import org.springframework.stereotype.Component

@Component
class GeoPositionTreeBuilder {

    fun build(geoPositions: List<GeoPosition>): GeoPositionTree {
        val root = build(geoPositions, 1)
        return GeoPositionTree(root)
    }

    private fun build(geoPositions: List<GeoPosition>, level: Int): GeoPositionTree.TreeNode? {
        if (geoPositions.isEmpty()) {
            return null
        }

        val sortedGeoPositions = if (level % 2 == 1)
            geoPositions.sortedWith(compareBy { it.latitude })
        else
            geoPositions.sortedWith(compareBy { it.longitude })

        val medianIndex = sortedGeoPositions.size / 2
        val median = sortedGeoPositions[medianIndex]
        val beforeMedian = sortedGeoPositions.subList(0, medianIndex)
        val afterMedian = sortedGeoPositions.subList(medianIndex + 1, sortedGeoPositions.size)
        return GeoPositionTree.TreeNode(median, build(beforeMedian, level + 1), build(afterMedian, level + 1))
    }
}