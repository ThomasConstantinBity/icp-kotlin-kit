package com.bity.icp_kotlin_kit.data.datasource.api.response

import com.bity.icp_kotlin_kit.data.datasource.api.model.HashTreeNode

class StateCertificateResponse(
    val signature: ByteArray,
    val tree: HashTreeNode
)