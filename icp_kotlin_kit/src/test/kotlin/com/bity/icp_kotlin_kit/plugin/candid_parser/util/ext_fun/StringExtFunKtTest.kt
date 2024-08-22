package com.bity.icp_kotlin_kit.plugin.candid_parser.util.ext_fun

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class StringExtFunKtTest {

    @Test
    fun `empty comment`() {
        val string = "//"
        assertEquals("", string.trimCommentLine())
    }
}