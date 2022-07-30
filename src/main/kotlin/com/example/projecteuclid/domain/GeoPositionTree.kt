package com.example.projecteuclid.domain

class GeoPositionTree {

    var root: TreeNode? = null

    fun insert(position: GeoPosition) {
        if (root == null) {
            root = TreeNode(position)
            return
        }

        insert(position, root!!, 1)
    }

    private fun insert(position: GeoPosition, parentNode: TreeNode, height: Int) {
        val comparator = getComparator(height)

        if (comparator.invoke(position, parentNode.position) < 0) {
            if (parentNode.left == null) {
                parentNode.left = TreeNode(position, parentNode)
            } else {
                insert(position, parentNode.left!!, height + 1)
            }
        } else {
            if (parentNode.right == null) {
                parentNode.right = TreeNode(position, parentNode)
            } else {
                insert(position, parentNode.right!!, height + 1)
            }
        }
    }

    data class TreeNode(val position: GeoPosition, var left: TreeNode?, var right: TreeNode?, val parent: TreeNode?) {
        constructor(position: GeoPosition) : this(position, null)

        constructor(position: GeoPosition, parent: TreeNode?) : this(position, null, null, parent)

        override fun toString(): String {
            return "(${position.latitude}, ${position.longitude})"
        }
    }

    companion object {
        fun getComparator(level: Int): (GeoPosition, GeoPosition) -> Double {
            val comparator: (GeoPosition, GeoPosition) -> Double
            if (level % 2 == 1) {
                comparator = { a: GeoPosition, b: GeoPosition -> a.latitude - b.latitude }
            } else {
                comparator = { a: GeoPosition, b: GeoPosition -> a.longitude - b.longitude }
            }
            return comparator
        }
    }
}

