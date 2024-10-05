package com.bity.icp_kotlin_kit.file_parser.file_generator.helper

object UnnamedClassHelper {

    private var index = 0

    fun getUnnamedClassName() = "UnnamedClass${index++}"
}