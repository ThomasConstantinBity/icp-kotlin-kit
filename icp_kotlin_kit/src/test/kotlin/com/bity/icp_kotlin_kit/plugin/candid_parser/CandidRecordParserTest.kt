package com.bity.icp_kotlin_kit.plugin.candid_parser

import org.junit.jupiter.api.Test

class CandidRecordParserTest {

    @Test
    fun test() {
        val input = "record { e8s : nat64; }"
        CandidRecordParser.parse(input)
    }

    @Test
    fun multiDecTest() {
        val input = """
            record {
                from_subaccount: opt blob; // The subaccount to transfer the token from
                to : Account;
                token_id : nat;
                memo : opt blob;
                created_at_time : opt nat64;
            }
        """.trimIndent()
        CandidRecordParser.parse(input)
    }
}