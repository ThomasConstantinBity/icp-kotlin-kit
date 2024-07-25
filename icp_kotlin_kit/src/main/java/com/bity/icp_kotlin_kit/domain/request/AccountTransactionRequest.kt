package com.bity.icp_kotlin_kit.domain.request

import com.bity.icp_kotlin_kit.domain.model.enum.ICPRequestCertification

class AccountTransactionRequest(
    val address: String,
    val certification: ICPRequestCertification = ICPRequestCertification.Certified,
    val pollingValues: PollingValues = PollingValues()
)