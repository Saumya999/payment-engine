package com.payment.engine.com.payment.engine.merchant.model

import com.payment.engine.com.payment.engine.merchant.entity.MerchantBankAccount
import com.payment.engine.com.payment.engine.merchant.entity.MerchantBankAccountStatus
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

data class MerchantBankAccountResponse(

    val merchantAccountId: UUID,

    val merchantId: UUID,

    val accountNumber: String,

    val bankCode: String,

    val ifscCode: String,

    val currency: String,

    val ledgerBalance: BigDecimal,

    val availableBalance: BigDecimal,

    val status: MerchantBankAccountStatus,

    val createdAt: Instant
) {

    companion object {

        fun from(
            account: MerchantBankAccount
        ): MerchantBankAccountResponse =
            MerchantBankAccountResponse(
                merchantAccountId =
                    account.merchantAccountId,

                merchantId =
                    account.merchant.merchantId,

                accountNumber =
                    account.accountNumber,

                bankCode =
                    account.bankCode,

                ifscCode =
                    account.ifscCode,

                currency =
                    account.currency,

                ledgerBalance =
                    account.ledgerBalance,

                availableBalance =
                    account.availableBalance,

                status =
                    account.status,

                createdAt =
                    account.createdAt
            )
    }
}