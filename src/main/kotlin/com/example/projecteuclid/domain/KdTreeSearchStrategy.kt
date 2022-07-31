package com.example.projecteuclid.domain

import com.example.projecteuclid.repository.GeoPositionRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component
import java.math.BigDecimal
import javax.annotation.PostConstruct

@Component
@Qualifier("k-d tree search")
class KdTreeSearchStrategy : GeoPositionSearchStrategy {

    @Autowired
    private lateinit var treeBuilder: GeoPositionTreeBuilder

    @Autowired
    private lateinit var repository: GeoPositionRepository

    private lateinit var geoPositionTree: GeoPositionTree

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    @PostConstruct
    fun initKdTree() {
        logger.info("Initializing kd-tree...")
        val points = repository.findAll()
        geoPositionTree = treeBuilder.build(points)
        logger.info("Initialized kd-tree")
    }

    override fun search(fixedGeoPosition: GeoPosition): GeoPosition? {
        val tree = geoPositionTree
        if (tree.root == null) {
            return null
        }

        val initialSearchResult = initSearchResult(tree)
        var searchResult = searchClosestLeafNode(fixedGeoPosition, tree.root!!, initialSearchResult)

        // backtrack
        if (searchResult.distanceSquared.compareTo(BigDecimal.ZERO) != 0) {
            searchResult =
                backTrackSearch(fixedGeoPosition, searchResult.leafNode!!.parent, searchResult)
        }
        return searchResult.geoPosition
    }

    private fun initSearchResult(tree: GeoPositionTree): SearchResult {
        val extremeLowerLeftValue = GeoPosition(-90.0, -180.0)
        val extremeUpperRightValue = GeoPosition(90.0, 180.0)
        val maxDistanceSquared = extremeLowerLeftValue.distanceSquaredFrom(extremeUpperRightValue)

        return SearchResult(tree.root!!, maxDistanceSquared)
    }

    private fun searchClosestLeafNode(
        targetGeoPosition: GeoPosition,
        node: GeoPositionTree.TreeNode,
        searchResult: SearchResult
    ): SearchResult {
        val newSearchResult = updateSearchResult(searchResult, targetGeoPosition, node)

        val nodeAndTargetTheSame = node.position.distanceSquaredFrom(targetGeoPosition).compareTo(BigDecimal.ZERO) == 0
        if (nodeAndTargetTheSame) {
            return newSearchResult
        }

        if (node.isLeaf) {
            return newSearchResult.apply { leafNode = node }
        }

        return if (node.compare(targetGeoPosition) > BigDecimal.ZERO) {
            if (node.left != null) {
                searchClosestLeafNode(targetGeoPosition, node.left!!, newSearchResult)
            } else {
                searchClosestLeafNode(targetGeoPosition, node.right!!, newSearchResult)
            }
        } else {
            if (node.right != null) {
                searchClosestLeafNode(targetGeoPosition, node.right!!, newSearchResult)
            } else {
                searchClosestLeafNode(targetGeoPosition, node.left!!, newSearchResult)
            }
        }
    }

    private fun backTrackSearch(
        targetGeoPosition: GeoPosition,
        node: GeoPositionTree.TreeNode?,
        searchResult: SearchResult
    ): SearchResult {
        if (node == null) {
            return SearchResult(searchResult.node, searchResult.distanceSquared)
        }

        val hyperPlaneIntersectsHyperSphere =
            (node.calculateHyperPlaneDistance(targetGeoPosition) < searchResult.distanceSquared)
        if (!hyperPlaneIntersectsHyperSphere) {
            return backTrackSearch(targetGeoPosition, node.parent, searchResult)
        }

        var newSearchResult = searchResult
        // search for closest point on opposite side of hyperplane if it exists
        if (node.compare(targetGeoPosition) > BigDecimal.ZERO) {
            if (node.right != null) {
                newSearchResult = searchClosestLeafNode(targetGeoPosition, node.right!!, searchResult)
            }
        } else {
            if (node.left != null) {
                newSearchResult = searchClosestLeafNode(targetGeoPosition, node.left!!, searchResult)
            }
        }

        return backTrackSearch(targetGeoPosition, node.parent, newSearchResult)
    }

    private fun updateSearchResult(
        searchResult: SearchResult,
        targetGeoPosition: GeoPosition,
        node: GeoPositionTree.TreeNode
    ): SearchResult {
        val bestDistance = searchResult.distanceSquared
        val nodeDistance = node.position.distanceSquaredFrom(targetGeoPosition)
        return if (nodeDistance < bestDistance)
            SearchResult(node, nodeDistance, searchResult.leafNode)
        else
            SearchResult(searchResult.node, searchResult.distanceSquared, searchResult.leafNode)
    }

    data class SearchResult(
        val node: GeoPositionTree.TreeNode,
        val distanceSquared: BigDecimal,
        var leafNode: GeoPositionTree.TreeNode?
    ) {

        constructor(node: GeoPositionTree.TreeNode, distanceSquared: BigDecimal) : this(node, distanceSquared, null)

        val geoPosition: GeoPosition
            get() = node.position
    }
}