package com.bity.icp_kotlin_kit.data.datasource.api.request

import com.bity.icp_kotlin_kit.data.datasource.api.enum.ContentRequestType
import com.bity.icp_kotlin_kit.data.datasource.api.model.ICPPrincipalApiModel
import com.bity.icp_kotlin_kit.data.datasource.api.model.ICPRequestApiModel
import com.bity.icp_kotlin_kit.domain.model.ICPSigningPrincipal
import com.bity.icp_kotlin_kit.util.ICPRequestUtil

internal class ICPRequest private constructor(
    requestId: ByteArray,
    val urlPath: String,
    val envelope: ICPRequestEnvelope,
) {

    companion object {
        suspend fun init(
            requestType: ICPRequestApiModel,
            canister: ICPPrincipalApiModel,
            sender: ICPSigningPrincipal?= null
        ): ICPRequest {
            val contentRequestType = ContentRequestType.fromICPRequestApiModel(requestType)
            val content = ICPRequestUtil.buildContent(
                request = requestType,
                sender = sender?.principal
            )
            val requestId = content.calculateRequestId()
            val urlPath = "${canister.string}/${contentRequestType.type}"
            val envelope = ICPRequestUtil.buildEnvelope(
                content = content,
                sender = sender
            )

            return ICPRequest(
                requestId = requestId,
                urlPath = urlPath,
                envelope = envelope,
            )
        }
    }
}