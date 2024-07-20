package com.bity.icp_kotlin_kit.domain.model.enum

import com.bity.icp_kotlin_kit.domain.model.ICPPrincipal

enum class ICPSystemCanisters(val icpPrincipal: ICPPrincipal) {
    Root(ICPPrincipal.init("r7inp-6aaaa-aaaaa-aaabq-cai")),
    Management(ICPPrincipal.init("aaaaa-aa")),
    Ledger(ICPPrincipal.init("ryjl3-tyaaa-aaaaa-aaaba-cai")),
    Governance(ICPPrincipal.init("rrkah-fqaaa-aaaaa-aaaaq-cai")),
    CyclesMint(ICPPrincipal.init("rkp4c-7iaaa-aaaaa-aaaca-cai")),
    Ii(ICPPrincipal.init("rdmx6-jaaaa-aaaaa-aaadq-cai")),
}