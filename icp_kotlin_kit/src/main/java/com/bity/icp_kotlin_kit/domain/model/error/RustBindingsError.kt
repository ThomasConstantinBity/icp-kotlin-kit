package com.bity.icp_kotlin_kit.domain.model.error

sealed class RustBindingsError(errorMessage: String? = null): Error(errorMessage) {
    class LibraryNotInstantiated(libraryName: String): RustBindingsError("$libraryName not found")
    class WrongSignature: RustBindingsError()
}