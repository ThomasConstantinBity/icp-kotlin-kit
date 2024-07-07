package com.bity.icp_cryptography.util
import java.nio.ByteBuffer
import java.util.zip.CRC32

object CRC32 {

    /**
     * com.bity.icp_cryptography.model.error.CRC32 is a four byte check sequence, calculated as defined by ISO 3309, ITU-T V.42,
     * and elsewhere, and stored as big-endian, i.e., the most significant byte comes first and
     * then the less significant bytes come in descending order of significance (MSB B2 B1 LSB).
     */
    private const val CRC_32_LENGTH = 4

    fun crc32(data: ByteArray): ByteArray {
        val crc32 = CRC32()
        crc32.update(data)
        val checksum = crc32.value

        val buffer = ByteBuffer.allocate(Long.SIZE_BYTES)
        buffer.putLong(checksum)

        return buffer.array().takeLast(CRC_32_LENGTH).toByteArray()
    }
}