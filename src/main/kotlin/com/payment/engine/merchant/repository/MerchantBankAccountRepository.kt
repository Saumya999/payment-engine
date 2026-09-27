package com.payment.engine.com.payment.engine.merchant.repository

import com.payment.engine.com.payment.engine.merchant.entity.MerchantBankAccount
import com.payment.engine.com.payment.engine.merchant.entity.MerchantBankAccountStatus
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface MerchantBankAccountRepository :
    JpaRepository<MerchantBankAccount, UUID> {

    fun findByMerchant_MerchantId(
        merchantId: UUID
    ): List<MerchantBankAccount>

    fun findByAccountNumber(
        accountNumber: String
    ): MerchantBankAccount?

    fun existsByAccountNumber(
        accountNumber: String
    ): Boolean

    fun countByMerchant_MerchantId(
        merchantId: UUID
    ): Long

    fun countByStatus(
        status: MerchantBankAccountStatus
    ): Long
}