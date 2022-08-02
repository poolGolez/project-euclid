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
        if (geoPositionTree.root == null) {
            return null
        }

        val leafNode = search(fixedGeoPosition, geoPositionTree.root!!)
        return leafNode.position
    }

    private fun search(target: GeoPosition, startNode: GeoPositionTree.TreeNode): GeoPositionTree.TreeNode {
        var leafNode = findLeafNode(target, startNode)
        if (startNode != leafNode) {
            val searchResult = SearchResult(target, leafNode)
            return backtrackSearch(startNode, leafNode.parent!!, target, searchResult).node
        }

        return leafNode
    }

    private fun backtrackSearch(
        startNode: GeoPositionTree.TreeNode,
        node: GeoPositionTree.TreeNode?,
        target: GeoPosition,
        searchResult: SearchResult
    ): SearchResult {
        if (node == null || searchResult.distance.compareTo(BigDecimal.ZERO) == 0) {
            return searchResult
        }

        var newSearchResult = checkNodeDistance(node, target, searchResult)
        newSearchResult = checkOtherSubTreeBestNode(node, target, newSearchResult)

        return if (startNode == node) {
            newSearchResult
        } else {
            backtrackSearch(startNode, node.parent, target, newSearchResult)
        }
    }

    private fun checkNodeDistance(
        node: GeoPositionTree.TreeNode,
        target: GeoPosition,
        searchResult: SearchResult
    ): SearchResult {
        val nodeDistance = node.position.distanceSquaredFrom(target)
        if (nodeDistance < searchResult.distance) {
            return SearchResult(target, node)
        }
        return searchResult
    }

    private fun checkOtherSubTreeBestNode(
        node: GeoPositionTree.TreeNode,
        target: GeoPosition,
        searchResult: SearchResult
    ): SearchResult {
        if (candidatePointsExistOnOtherSide(node, target, searchResult)) {
            var bestNodeFromOtherSide: GeoPositionTree.TreeNode? = searchSubTreeOnOtherSide(node, target)
            bestNodeFromOtherSide?.let {
                if (bestNodeFromOtherSide.position.distanceSquaredFrom(target) < searchResult.distance) {
                    return SearchResult(target, bestNodeFromOtherSide)
                }
            }
        }
        return searchResult
    }

    private fun candidatePointsExistOnOtherSide(
        node: GeoPositionTree.TreeNode,
        target: GeoPosition,
        newSearchResult: SearchResult
    ): Boolean {
        val hyperPlaneDistance = node.calculateHyperPlaneDistance(target)
        return hyperPlaneDistance < newSearchResult.distance
    }

    private fun searchSubTreeOnOtherSide(
        node: GeoPositionTree.TreeNode,
        target: GeoPosition
    ): GeoPositionTree.TreeNode? {
        if (node.compare(target) > BigDecimal.ZERO && node.right != null) {
            return search(target, node.right!!)
        } else if (node.compare(target) <= BigDecimal.ZERO && node.left != null) {
            return search(target, node.left!!)
        }
        return null
    }

    private fun findLeafNode(target: GeoPosition, node: GeoPositionTree.TreeNode): GeoPositionTree.TreeNode {
        if (node.isLeaf) {
            return node
        }

        return if (node.compare(target) > BigDecimal.ZERO) {
            if (node.left != null) {
                findLeafNode(target, node.left!!)
            } else {
                findLeafNode(target, node.right!!)
            }
        } else {
            if (node.right != null) {
                findLeafNode(target, node.right!!)
            } else {
                findLeafNode(target, node.left!!)
            }
        }
    }

    data class SearchResult(val target: GeoPosition, val node: GeoPositionTree.TreeNode) {
        val distance
            get() = node.position.distanceSquaredFrom(target)
    }

}