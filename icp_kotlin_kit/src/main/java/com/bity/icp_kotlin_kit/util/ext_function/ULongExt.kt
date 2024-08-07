package com.bity.icp_kotlin_kit.util.ext_function

import com.bity.icp_kotlin_kit.candid.model.CandidDictionary
import com.bity.icp_kotlin_kit.candid.model.CandidValue
import java.io.InputStream

// Little-endian = Least significant byte first
val ULong.bytes: ByteArray
    get() = this.toLong().bytes

fun ULong.Companion.readFrom(stream: InputStream): ULong {
    val byteArray = ByteArray(SIZE_BYTES)
    stream.read(byteArray, 0, SIZE_BYTES)
    byteArray.reverse()
    return byteArray.toLong().toULong()
}

internal fun ULong.icpAmount(): CandidValue =
    CandidValue.Record(
        CandidDictionary(
            hashMapOf("e8s" to CandidValue.Natural64(this))
        )
    )

internal fun ULong.icpTimestamp(): CandidValue =
    CandidValue.Record(
        CandidDictionary(
            hashMapOf(
                "timestamp_nanos" to CandidValue.Natural64(this)
            )
        )
    )