package com.bity.icp_kotlin_kit.data.datasource.api.model

sealed class ICPStateTreePathComponentApiModel(
    val stringValue: String?,
    val dataValue: ByteArray?
) {
    class DataApiModel(
        val data: ByteArray
    ): ICPStateTreePathComponentApiModel(null, data)
    class StringApiModel(
        val string: String
    ): ICPStateTreePathComponentApiModel(string, null)

    fun encoded(): ByteArray =
        when(this) {
            is DataApiModel -> this.data
            is StringApiModel -> this.string.toByteArray(Charsets.UTF_8)
        }

    override fun equals(other: Any?): Boolean {
        if(other !is ICPStateTreePathComponentApiModel) return false
        return when {
            this is DataApiModel && other is DataApiModel ->
                data.contentEquals(other.data)
            this is StringApiModel && other is StringApiModel ->
                string == other.stringValue
            else -> false
        }
    }

    override fun hashCode(): Int {
        var result = stringValue?.hashCode() ?: 0
        result = 31 * result + (dataValue?.contentHashCode() ?: 0)
        return result
    }
}