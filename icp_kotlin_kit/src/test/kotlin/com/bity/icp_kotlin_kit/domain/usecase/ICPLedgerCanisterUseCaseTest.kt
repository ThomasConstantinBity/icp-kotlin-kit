package com.bity.icp_kotlin_kit.domain.usecase

import com.bity.icp_cryptography.model.ICPDomainSeparator
import com.bity.icp_cryptography.util.EllipticSign
import com.bity.icp_kotlin_kit.data.datasource.api.enum.ContentRequestType
import com.bity.icp_kotlin_kit.data.datasource.api.model.CallApiModel
import com.bity.icp_kotlin_kit.data.datasource.api.model.ContentApiModel
import com.bity.icp_kotlin_kit.domain.model.ICPAccount
import com.bity.icp_kotlin_kit.domain.model.ICPPrincipal
import com.bity.icp_kotlin_kit.domain.model.ICPSigningPrincipal
import com.bity.icp_kotlin_kit.domain.model.enum.ICPRequestCertification
import com.bity.icp_kotlin_kit.domain.request.TransferRequest
import com.bity.icp_kotlin_kit.provideICPLedgerCanisterUseCase
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import java.math.BigInteger
import kotlin.test.assertEquals

@OptIn(ExperimentalStdlibApi::class)
class ICPLedgerCanisterUseCaseTest {

    private val key = BigInteger("")

    private fun icpSignature(
        domain: ICPDomainSeparator,
        message: ByteArray,
        key: BigInteger
    ): ByteArray =
        EllipticSign(
            domainSeparatorData = ICPDomainSeparator(domain = domain.domain).domainSeparatedData(message),
            key = key
        )
            .dropLast(1)
            .toByteArray()


    @Test
    fun testEnvelope() {
        val content = CallApiModel(
            requestType = ContentRequestType.Call,
            sender = "226483ffd5d0094cf127381218a25a8eb3ea207cbc5f4b9be31e082102".hexToByteArray(),
            nonce = "f40583d7e3ca1101b307ca7c16d51120684eb5cfc8e7e51a2b24e05864896571".hexToByteArray(),
            ingressExpiry = 1721669072550225000,
            method_name = "transfer",
            canister_id = "00000000000000020101".hexToByteArray(),
            arg = "4449444c056d7b6c01e0a9b302786e006c01d6f68e8001786c06fbca0100c6fcb60201ba89e5c20478a2de94eb060282f3f3910c03d8a38ca80d01010420cafd0a2c27f41a851837b00f019b93e741f76e4147fe74435fb7efb836826a1c102700000000000000000000000000000120000000000000000000000000000000000000000000000000000000000000000000c03850ae98e417a0bb0d0000000000".hexToByteArray()
        )
        val requestId = content.calculateRequestId()
        assertEquals(
            "35c93860fda7af2c67f7fa8db76aa4d136cea0caaa299b96c5b3da8c15763427",
            requestId.toHexString()
        )

        assertEquals(
            icpSignature(
                domain = ICPDomainSeparator("ic-request"),
                message = requestId,
                key = key
            ).toHexString(),
            "e9e4a8c13374589ba6b6d8e13fca7e7e499984d7702bfb1b1083ef12cca493f40cb720825ca46da387cd6c6c31435495977b936f09945c726e3962a4bd8ec05e"
        )
    }

    @Test
    fun testBalance() = runTest {
        provideICPLedgerCanisterUseCase(true).accountBalance(
            account = ICPAccount.mainAccount(
                principal = ICPPrincipal.selfAuthenticatingPrincipal("046acf4c93dd993cd736420302eb70da254532ec3179250a21eec4ce823ff289aaa382cb19576b2c6447db09cb45926ebd69ce288b1804580fe62c343d3252ec6e".hexToByteArray())
            ),
            certification = ICPRequestCertification.Uncertified
        )
    }

    @OptIn(ExperimentalStdlibApi::class)
    @Test
    fun test() = runTest {
        val sendingAccount = ICPAccount.mainAccount(
            principal = ICPPrincipal.selfAuthenticatingPrincipal("046acf4c93dd993cd736420302eb70da254532ec3179250a21eec4ce823ff289aaa382cb19576b2c6447db09cb45926ebd69ce288b1804580fe62c343d3252ec6e".hexToByteArray())
        )
        val request = TransferRequest(
            sendingAccount = sendingAccount,
            receivingAddress = sendingAccount.address,
            amount = 10000U,
            signingPrincipal = object: ICPSigningPrincipal {

                override val principal: ICPPrincipal = ICPPrincipal.init("mi5lp-tjcms-b77vo-qbfgp-cjzyc-imkew-uowpv-ca7f4-l5fzx-yy6ba-qqe")
                override val rawPublicKey: ByteArray = "046acf4c93dd993cd736420302eb70da254532ec3179250a21eec4ce823ff289aaa382cb19576b2c6447db09cb45926ebd69ce288b1804580fe62c343d3252ec6e".hexToByteArray()

                override suspend fun sign(
                    message: ByteArray,
                    domain: ICPDomainSeparator
                ): ByteArray = icpSignature(domain, message, key)

            },
            memo = 118.toULong()
        )
        provideICPLedgerCanisterUseCase(true).transfer(request)
    }
}