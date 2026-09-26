package com.payment.engine.com.payment.engine.bank.model

import com.payment.engine.com.payment.engine.bank.domain.BankAccount
import com.payment.engine.com.payment.engine.bank.domain.BankAccountStatus
import com.payment.engine.com.payment.engine.bank.domain.BankAccountType
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

data class BankAccountResponse(
    val accountId: UUID,
    val customerId: UUID,
    val accountNumber: String,
    val bankCode: String,
    val ifscCode: String,
    val accountType: BankAccountType,
    val currency: String,
    val ledgerBalance: BigDecimal,
    val availableBalance: BigDecimal,
    val status: BankAccountStatus,
    val createdAt: Instant
) {

    companion object {

        fun from(account: BankAccount): BankAccountResponse =
            BankAccountResponse(
                accountId = account.accountId,
                customerId = account.customer.customerId,
                accountNumber = account.accountNumber,
                bankCode = account.bankCode,
                ifscCode = account.ifscCode,
                accountType = account.accountType,
                currency = account.currency,
                ledgerBalance = account.ledgerBalance,
                availableBalance = account.availableBalance,
                status = account.status,
                createdAt = account.createdAt
            )
    }
}