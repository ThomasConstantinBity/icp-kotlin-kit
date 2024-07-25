package com.bity.icp_kotlin_kit.domain.model.block_query_candid_response

import com.bity.icp_candid.domain.model.CandidValue
import com.bity.icp_kotlin_kit.domain.model.error.ICPLedgerCanisterError

internal class BlockQueryCandidResponse(
    candidValue: CandidValue
) {
    val chainLength: ULong
    val firstBlockIndex: ULong
    val certificate: ByteArray?
    val blocks: List<ICPBlock>
    val archivedBlocks: List<ArchivedBlock>

    init {
        val queryBlockResponse = candidValue.recordValue
            ?: throw ICPLedgerCanisterError.InvalidResponse()
        chainLength = queryBlockResponse["chain_length"]?.natural64Value
            ?: throw ICPLedgerCanisterError.InvalidResponse()
        firstBlockIndex = queryBlockResponse["first_block_index"]?.natural64Value
            ?: throw ICPLedgerCanisterError.InvalidResponse()
        val blockValues = queryBlockResponse["blocks"]?.vectorValue?.values
            ?: throw ICPLedgerCanisterError.InvalidResponse()
        val archivedBlockValues = queryBlockResponse["archived_blocks"]?.vectorValue?.values
            ?: throw ICPLedgerCanisterError.InvalidResponse()
        val optionalCertificate = queryBlockResponse["certificate"]?.optionValue
            ?: throw ICPLedgerCanisterError.InvalidResponse()
        certificate = optionalCertificate.value?.blobValue
        blocks = blockValues.map { ICPBlock(it) }
        archivedBlocks = archivedBlockValues.map { ArchivedBlock(it) }
    }
}