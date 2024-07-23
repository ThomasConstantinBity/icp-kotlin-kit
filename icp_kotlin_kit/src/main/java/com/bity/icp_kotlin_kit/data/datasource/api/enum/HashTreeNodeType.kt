package com.bity.icp_kotlin_kit.data.datasource.api.enum

enum class HashTreeNodeType(val value: Int) {
    Empty(0),
    Fork(1),
    Labeled(2),
    Leaf(3),
    Pruned(4);

    companion object {
        fun fromValue(value: Int): HashTreeNodeType? {
            return entries.find { it.value == value }
        }
    }
}