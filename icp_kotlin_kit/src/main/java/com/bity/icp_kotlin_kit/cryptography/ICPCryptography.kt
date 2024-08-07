package com.bity.icp_kotlin_kit.cryptography

import com.bity.icp_kotlin_kit.domain.model.error.ICPCryptographyError
import com.bity.icp_kotlin_kit.util.ext_function.fromHex
import com.bity.icp_kotlin_kit.util.ext_function.grouped
import org.apache.commons.codec.binary.Base32

internal object ICPCryptography {

    private const val CANONICAL_TEXT_SEPARATOR = "-"
    private val base32 = Base32()

    /**
     * The canonical textual representation of a blob b isGrouped(Base32(CRC32(b) · b)) where:
     * - CRC32 is a four byte check sequence, calculated as defined by ISO 3309, ITU-T V.42,
     *   and elsewhere, and stored as big-endian, i.e., the most significant byte comes first
     *   and then the less significant bytes come in descending order of significance (MSB B2 B1 LSB).
     * - Base32 is the Base32 encoding as defined in RFC 4648, with no padding character added.
     * - The middle dot denotes concatenation.
     * - Grouped takes an ASCII string and inserts the separator - (dash) every 5 characters.
     *   The last group may contain less than 5 characters. A separator never appears at the beginning or end.
     **/
    fun encodeCanonicalText(data: ByteArray): String {
        val checksum = CRC32(data)
        val dataWithChecksum = checksum + data
        val base32Encoded = base32.encodeAsString(dataWithChecksum)
            .lowercase()
            .filter { it != '=' }
        return base32Encoded.grouped(CANONICAL_TEXT_SEPARATOR, 5)
    }

    fun decodeCanonicalText(text: String): ByteArray {
        val degrouped = text.replace(CANONICAL_TEXT_SEPARATOR, "")
        val base32Encoded = if(degrouped.length % 2 != 0) {
            "$degrouped="
        } else {
            degrouped
        }
        val decoded = base32.decode(base32Encoded)
        val checksum = decoded.take(CRC32.CRC_32_LENGTH).toByteArray()
        val data = decoded.copyOfRange(CRC32.CRC_32_LENGTH, decoded.size)
        val expectedChecksum = CRC32(data)
        require(expectedChecksum.contentEquals(checksum)) {
            throw ICPCryptographyError.ICPCRC32Error.InvalidChecksum()
        }
        return decoded.copyOfRange(CRC32.CRC_32_LENGTH, decoded.size)
    }

    fun isValidAccountId(accountId: String): Boolean {
        val data = accountId.fromHex() ?: return false
        require(data.size == 32) {
            return false
        }
        val checksum = data.take(CRC32.CRC_32_LENGTH)
        val hashed = data.takeLast(data.size - CRC32.CRC_32_LENGTH)
        val expectedChecksum = CRC32(hashed.toByteArray())
        return expectedChecksum.contentEquals(checksum.toByteArray())
    }
}