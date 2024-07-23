package com.bity.icp_kotlin_kit.util.annotation.retrofit

import kotlin.annotation.AnnotationRetention.RUNTIME
import kotlin.annotation.AnnotationTarget.FUNCTION

@Retention(RUNTIME)
@Target(FUNCTION)
annotation class UseReadStateConverter