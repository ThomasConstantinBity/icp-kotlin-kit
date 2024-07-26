package com.bity.icp_kotlin_kit

object RustBindings {

    var blsInstance: Int? = null
        private set

    init {
        println("Loading library...")
        System.loadLibrary("bls12381")
        println("Result: ${blsInstantiate()}")
        if(blsInstantiate() == 1) {
            blsInstance = 1
        }
    }

    private external fun blsInstantiate(): Int
    external fun blsVerify(
        autograph: ByteArray,
        message: ByteArray,
        key: ByteArray
    ): Int
}