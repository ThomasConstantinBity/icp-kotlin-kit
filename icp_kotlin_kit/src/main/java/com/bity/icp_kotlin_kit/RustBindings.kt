package com.bity.icp_kotlin_kit

import com.bity.icp_kotlin_kit.domain.model.error.RustBindingsError

internal object RustBindings {

    init {
        try {
            System.loadLibrary("bls12381")
        } catch (error: UnsatisfiedLinkError) {
            throw RustBindingsError.LibraryNotInstantiated("bls12381")
        }
        require(blsInstantiate() == 1) {
            throw RustBindingsError.LibraryNotInstantiated("bls12381")
        }
    }

    private external fun blsInstantiate(): Int
    external fun blsVerify(
        autograph: ByteArray,
        message: ByteArray,
        key: ByteArray
    ): Int
}