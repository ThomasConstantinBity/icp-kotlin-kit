package com.bity.icp_kotlin_kit.data.datasource.api.model

class ICPStateTreePathApiModel(
    val components: List<ICPStateTreePathComponentApiModel>
) {
    var firstComponent: ICPStateTreePathComponentApiModel? = components.firstOrNull()
    val removingFirstComponent: ICPStateTreePathApiModel
        get() = ICPStateTreePathApiModel(components.subList(1, components.size))
    val isEmpty: Boolean = components.isEmpty()

    constructor(path: String): this(
        // Use .filter { it != "" } because /a/x returns 3 elements: ["", "a", "x"]
        path.split("/")
            .filter { it != "" }
            .map { ICPStateTreePathComponentApiModel.StringApiModel(it) }
    )

    fun encodedComponents(): List<ByteArray> =
        components.map { it.encoded() }
}