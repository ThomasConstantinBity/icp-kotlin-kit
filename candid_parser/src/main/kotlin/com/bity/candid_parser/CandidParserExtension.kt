package com.bity.candid_parser

import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import javax.inject.Inject

open class CandidParserExtension @Inject constructor(objects: ObjectFactory) {
    val inputPath: Property<String> = objects.property(String::class.java).convention("default/folder/path")
}