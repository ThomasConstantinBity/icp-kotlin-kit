package com.bity.icp_kotlin_kit.data.datasource.api.model

import com.bity.icp_kotlin_kit.domain.model.error.ApiModelError

sealed class ICPRequestApiModel {
    class Call(val icpMethod: ICPMethodApiModel): ICPRequestApiModel()
    class Query(val icpMethod: ICPMethodApiModel): ICPRequestApiModel()
    class ReadState(val paths: List<ICPStateTreePathApiModel>): ICPRequestApiModel()

    val method: ICPMethodApiModel
        get() = when(this) {
            is Call -> icpMethod
            is Query -> icpMethod
            is ReadState -> throw ApiModelError.NoMethodForReadState()
        }
}