package com.bity.icp_kotlin_kit.plugin.file_generator.helper

object UnnamedClassHelper {

    private var index = 0

    fun getUnnamedClassName() = "UnnamedClass${index++}"
}