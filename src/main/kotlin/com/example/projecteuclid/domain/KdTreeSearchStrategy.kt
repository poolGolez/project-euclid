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

        val leafNode = searchHelper(fixedGeoPosition, geoPositionTree.root!!)

        return leafNode.position
    }

    private fun searchHelper(
        target: GeoPosition,
        startNode: GeoPositionTree.TreeNode
    ): GeoPositionTree.TreeNode {
        var leafNode = findLeafNode(target, startNode)

        var searchResult = SearchResult(target, leafNode)
        if (startNode != leafNode) {
            //backtrack only when there is a chain from leaf to start
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

        var newSearchResult = searchResult
        val nodeDistance = node.position.distanceSquaredFrom(target)
        if (nodeDistance < searchResult.distance) {
            // search other subtree
            newSearchResult = SearchResult(target, node)
        }


        val hyperPlaneDistance = node.calculateHyperPlaneDistance(target)
        if (hyperPlaneDistance < newSearchResult.distance) {
            var bestNodeFromOtherSide: GeoPositionTree.TreeNode? = searchOnOppositeSubTree(node, target)

            if (bestNodeFromOtherSide != null &&
                bestNodeFromOtherSide.position.distanceSquaredFrom(target) < newSearchResult.distance
            ) {
                newSearchResult = SearchResult(target, bestNodeFromOtherSide)
            }
        }

        return if (startNode == node) {
            newSearchResult
        } else {
            backtrackSearch(startNode, node.parent, target, newSearchResult)
        }
    }

    private fun searchOnOppositeSubTree(
        node: GeoPositionTree.TreeNode,
        target: GeoPosition
    ): GeoPositionTree.TreeNode? {
        if (node.compare(target) > BigDecimal.ZERO && node.right != null) {
            return searchHelper(target, node.right!!)
        } else if (node.compare(target) <= BigDecimal.ZERO && node.left != null) {
            return searchHelper(target, node.left!!)
        }
        return null
    }


    data class SearchResult(val target: GeoPosition, val node: GeoPositionTree.TreeNode) {
        val distance
            get() = node.position.distanceSquaredFrom(target)
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

}