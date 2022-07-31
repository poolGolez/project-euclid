package com.example.projecteuclid.domain

import kotlin.math.max
import kotlin.math.pow

class GeoPositionTree(var root: TreeNode?) {

    constructor() : this(null)

    val height = root?.height ?: 0

    fun insert(position: GeoPosition) {
        if (root == null) {
            root = TreeNode(position)
            return
        }

        insert(position, root!!)
    }

    private fun insert(position: GeoPosition, parentNode: TreeNode) {
        if (parentNode.compare(position) > 0) {
            if (parentNode.left == null) {
                parentNode.left = TreeNode(position, parentNode)
            } else {
                insert(position, parentNode.left!!)
            }
        } else {
            if (parentNode.right == null) {
                parentNode.right = TreeNode(position, parentNode)
            } else {
                insert(position, parentNode.right!!)
            }
        }
    }

    data class TreeNode(val position: GeoPosition, var left: TreeNode?, var right: TreeNode?, var parent: TreeNode?) {

        init {
            left?.let { left!!.parent = this }
            right?.let { right!!.parent = this }
        }

        constructor(position: GeoPosition) : this(position, null)

        constructor(position: GeoPosition, left: TreeNode?, right: TreeNode?) : this(position, left, right, null)

        constructor(position: GeoPosition, parent: TreeNode?) : this(position, null, null, parent)

        val isLeaf: Boolean
            get() = (left == null && right == null)

        val height: Int
            get() {
                return max(left?.height ?: 0, right?.height ?: 0) + 1
            }

        private val level: Int
            get() {
                return if (parent == null) 1 else parent!!.level + 1
            }

        fun compare(other: GeoPosition): Double {
            return if (level % 2 == 1) {
                position.compareLatitude(other)
            } else {
                position.compareLongitude(other)
            }
        }

        fun calculateHyperPlaneDistance(other: GeoPosition): Double {
            return compare(other).pow(2.0)
        }

        override fun toString(): String {
            return "($position) level ${level}"
        }
    }
}

