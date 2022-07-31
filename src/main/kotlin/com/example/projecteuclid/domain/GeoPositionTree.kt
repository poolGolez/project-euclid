package com.example.projecteuclid.domain

class GeoPositionTree(var root: TreeNode?) {

    constructor(): this(null)

    fun insert(position: GeoPosition) {
        if (root == null) {
            root = TreeNode(position)
            return
        }

        insert(position, root!!)
    }

    private fun insert(position: GeoPosition, parentNode: TreeNode) {
        if (parentNode.comparator.invoke(position, parentNode.position) < 0) {
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

        val level: Int
            get() { return if(parent == null) 1 else parent!!.level + 1}

        val comparator: (GeoPosition, GeoPosition) -> Double
            get() {
                if (level % 2 == 1) {
                    // TODO: Reuse GeoPositionComparator
                    return { a: GeoPosition, b: GeoPosition -> a.latitude - b.latitude }
                } else {
                    return { a: GeoPosition, b: GeoPosition -> a.longitude - b.longitude }
                }
            }

        override fun toString(): String {
            return "(${position.latitude}, ${position.longitude})"
        }
    }

    companion object {
        @Deprecated("Use TreeNode.comparator")
        fun getComparator(level: Int): (GeoPosition, GeoPosition) -> Double {
            val comparator: (GeoPosition, GeoPosition) -> Double
            if (level % 2 == 1) {
                // TODO: Reuse GeoPositionComparator
                comparator = { a: GeoPosition, b: GeoPosition -> a.latitude - b.latitude }
            } else {
                comparator = { a: GeoPosition, b: GeoPosition -> a.longitude - b.longitude }
            }
            return comparator
        }
    }
}

