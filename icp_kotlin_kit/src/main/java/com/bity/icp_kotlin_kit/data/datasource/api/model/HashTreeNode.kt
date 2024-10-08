package com.bity.icp_kotlin_kit.data.datasource.api.model

import com.bity.icp_kotlin_kit.cryptography.SHA256
import com.bity.icp_kotlin_kit.domain.model.ICPDomainSeparator

/**
 * see https://internetcomputer.org/docs/current/references/ic-interface-spec/#certification
 * see https://internetcomputer.org/docs/current/references/ic-interface-spec/#certificate
 */
sealed class HashTreeNode {

    object Empty: HashTreeNode()

    class Fork(
        val left: HashTreeNode,
        val right: HashTreeNode
    ): HashTreeNode()

    class Labeled(
        val label: ByteArray,
        val child: HashTreeNode
    ): HashTreeNode()

    class Leaf(val data: ByteArray): HashTreeNode()

    class Pruned(val data: ByteArray): HashTreeNode()

    override fun equals(other: Any?): Boolean {
        if(other !is HashTreeNode) {
            return false
        }
        return when {
            this is Empty && other is Empty -> true
            this is Fork && other is Fork ->
                left == other.left && right == other.right
            this is Labeled && other is Labeled ->
                label.contentEquals(other.label) && child == other.child
            this is Leaf && other is Leaf ->
                data.contentEquals(other.data)
            this is Pruned && other is Pruned ->
                data.contentEquals(other.data)
            else -> false
        }
    }

    override fun hashCode(): Int {
        return javaClass.hashCode()
    }

    fun getNode(path: ICPStateTreePathApiModel): HashTreeNode? {
        if(path.isEmpty) return this
        return when(this) {
            is Fork -> {
                left.getNode(path) ?: right.getNode(path)
            }
            is Labeled -> {
                if (!label.contentEquals(path.firstComponent?.encoded())) {
                    return null
                }
                child.getNode(path.removingFirstComponent)
            }
            is Empty,
            is Leaf,
            is Pruned -> null
        }
    }

    fun getValue(path: ICPStateTreePathApiModel): ByteArray? {
        return when (val node = getNode(path)) {
            is Leaf -> node.data
            else -> null
        }
    }

    fun hash(): ByteArray {
        val dataToHash = when (this) {
            Empty -> ICPDomainSeparator("ic-hashtree-empty").data
            is Fork -> ICPDomainSeparator("ic-hashtree-fork").data + left.hash() + right.hash()
            is Labeled -> ICPDomainSeparator("ic-hashtree-labeled").data + label + child.hash()
            is Leaf -> ICPDomainSeparator("ic-hashtree-leaf").data + data
            is Pruned -> return data // already hashed, just return it

        }
        return SHA256.sha256(dataToHash)
    }

    companion object {
        fun labeled(string: String, node: HashTreeNode): HashTreeNode =
            Labeled(string.toByteArray(Charsets.UTF_8), node)
    }
}