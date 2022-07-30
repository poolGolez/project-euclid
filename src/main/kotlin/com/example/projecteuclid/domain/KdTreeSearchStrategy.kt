package com.example.projecteuclid.domain

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component

@Component
@Qualifier("k-d tree search")
class KdTreeSearchStrategy : GeoPositionSearchStrategy {

    override fun search(targetGeoPosition: GeoPosition): GeoPosition? {
        val tree = generateKdTree()
        if (tree.root == null) {
            return null
        }

        val maxDistanceSquared = Math.pow(2 * 90.0, 2.0) + Math.pow(2 * 180.0, 2.0)
        val initialSearchResult = SearchResult(tree.root!!, maxDistanceSquared)
        var searchResult = searchTreeNode(targetGeoPosition, tree.root!!, initialSearchResult, 1)

        // backtrack
        searchResult =
            backTrackSearch(targetGeoPosition, searchResult.node.parent, searchResult, searchResult.level - 1)

        return searchResult.geoPosition
    }

    private fun searchTreeNode(
        targetGeoPosition: GeoPosition,
        node: GeoPositionTree.TreeNode,
        searchResult: SearchResult,
        level: Int
    ): SearchResult {

        val newSearchResult = updateSearchResult(searchResult, targetGeoPosition, node, level)

        if (node.position.latitude == targetGeoPosition.latitude &&
            node.position.longitude == targetGeoPosition.longitude
        ) {
            return newSearchResult
        }

        val comparator = GeoPositionTree.getComparator(level)
        if (comparator.invoke(targetGeoPosition, node.position) < 0) {
            if (node.left != null) {
                return searchTreeNode(targetGeoPosition, node.left!!, newSearchResult, level + 1)
            } else if (node.right != null) {
                return searchTreeNode(targetGeoPosition, node.right!!, newSearchResult, level + 1)
            } else {
                return newSearchResult
            }
        } else {
            if (node.right != null) {
                return searchTreeNode(targetGeoPosition, node.right!!, newSearchResult, level + 1)
            } else if (node.left != null) {
                return searchTreeNode(targetGeoPosition, node.left!!, newSearchResult, level + 1)
            } else {
                return newSearchResult
            }
        }
    }

    private fun backTrackSearch(
        targetGeoPosition: GeoPosition,
        node: GeoPositionTree.TreeNode?,
        searchResult: SearchResult,
        level: Int
    ): SearchResult {
        if (level == 0) {
            return SearchResult(searchResult.node, searchResult.distanceSquared)
        }
        var newSearchResult = searchResult
        val hyperPlaneDistanceMeasure = getHyperPlaneDistanceMeasure(level)
        val hyperPlaneDistanceSquared = hyperPlaneDistanceMeasure.invoke(targetGeoPosition, node!!.position)
        if (hyperPlaneDistanceSquared < searchResult.distanceSquared) {
            // explore subtree
            val comparator = GeoPositionTree.getComparator(level)
            if (comparator.invoke(targetGeoPosition, node.position) < 0) {
                // explore right subtree
                if (node.right != null) {
                    newSearchResult = searchTreeNode(targetGeoPosition, node.right!!, searchResult, level + 1)
                } else {
                    // nothing to explore!
                }
            } else {
                if (node.left != null) {
                    newSearchResult = searchTreeNode(targetGeoPosition, node.left!!, searchResult, level + 1)
                }
            }
        }

        return backTrackSearch(targetGeoPosition, node.parent, newSearchResult, level - 1)
    }

    private fun getHyperPlaneDistanceMeasure(height: Int): (GeoPosition, GeoPosition) -> Double {
        val comparator: (GeoPosition, GeoPosition) -> Double = GeoPositionTree.getComparator(height)
        return { a: GeoPosition, b: GeoPosition -> Math.pow(comparator.invoke(a, b), 2.0) }
    }

    private fun updateSearchResult(
        searchResult: SearchResult,
        targetGeoPosition: GeoPosition,
        node: GeoPositionTree.TreeNode,
        height: Int
    ): SearchResult {
        val bestDistance = searchResult.distanceSquared
        val nodeDistance = node.position.distanceSquaredFrom(targetGeoPosition)
        return if (nodeDistance < bestDistance)
            SearchResult(node, nodeDistance, height)
        else
            SearchResult(searchResult.node, searchResult.distanceSquared, searchResult.level)
    }

    private fun generateKdTree(): GeoPositionTree {
        val tree = GeoPositionTree()
        tree.insert(GeoPosition(8.0, 1.0))

        tree.insert(GeoPosition(0.0, 8.0))
        tree.insert(GeoPosition(16.0, 8.0))

        tree.insert(GeoPosition(4.0, 16.0))
        tree.insert(GeoPosition(12.0, 4.0))

        tree.insert(GeoPosition(5.0, 12.0))

        tree.insert(GeoPosition(7.0, 9.0))

        return tree
    }

    data class SearchResult(val node: GeoPositionTree.TreeNode, val distanceSquared: Double, val level: Int) {

        constructor(node: GeoPositionTree.TreeNode, distanceSquared: Double) : this(node, distanceSquared, 0)

        val geoPosition: GeoPosition
            get() = node.position
    }
}