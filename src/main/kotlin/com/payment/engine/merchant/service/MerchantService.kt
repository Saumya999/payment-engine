package com.payment.engine.com.payment.engine.merchant.service

import com.payment.engine.com.payment.engine.merchant.entity.Merchant
import com.payment.engine.com.payment.engine.merchant.entity.MerchantBankAccount
import com.payment.engine.com.payment.engine.merchant.entity.MerchantBankAccountStatus
import com.payment.engine.com.payment.engine.merchant.entity.MerchantStatus
import com.payment.engine.com.payment.engine.merchant.model.CreateMerchantRequest
import com.payment.engine.com.payment.engine.merchant.model.MerchantBankAccountResponse
import com.payment.engine.com.payment.engine.merchant.model.MerchantResponse
import com.payment.engine.com.payment.engine.merchant.repository.MerchantBankAccountRepository
import com.payment.engine.com.payment.engine.merchant.repository.MerchantRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.time.Instant
import java.util.*

@Service
class MerchantService(
    private val merchantRepository: MerchantRepository,
    private val merchantBankAccountRepository: MerchantBankAccountRepository
) {

    private val log =
        LoggerFactory.getLogger(
            MerchantService::class.java
        )

    @Transactional
    fun createMerchant(
        request: CreateMerchantRequest
    ): MerchantResponse {

        log.info(
            "Starting merchant creation, mobileNumber={}, merchantType={}",
            request.mobileNumber,
            request.merchantType
        )

        if (
            merchantRepository
                .existsByMobileNumber(request.mobileNumber)
        ) {

            log.warn(
                "Merchant creation rejected, mobile number already exists, mobileNumber={}",
                request.mobileNumber
            )

            throw IllegalArgumentException(
                "Merchant already exists for mobile number"
            )
        }

        val now = Instant.now()

        val merchant =
            Merchant(
                merchantId = UUID.randomUUID(),
                merchantName =
                    request.merchantName.trim(),
                mobileNumber =
                    request.mobileNumber,
                email =
                    request.email?.trim(),
                merchantType =
                    request.merchantType,
                status =
                    MerchantStatus.ACTIVE,
                createdAt = now,
                updatedAt = now
            )

        val saved =
            merchantRepository.save(merchant)

        log.info(
            "Merchant created successfully, merchantId={}, merchantType={}",
            saved.merchantId,
            saved.merchantType
        )

        return MerchantResponse.from(saved)
    }

    @Transactional
    fun createMerchantBankAccount(
        merchantId: UUID,
        bankCode: String,
        openingBalance: BigDecimal
    ): MerchantBankAccountResponse {

        log.info(
            "Starting merchant bank account creation, merchantId={}, bankCode={}",
            merchantId,
            bankCode
        )

        val merchant =
            merchantRepository
                .findById(merchantId)
                .orElseThrow {
                    IllegalArgumentException(
                        "Merchant not found: $merchantId"
                    )
                }

        if (merchant.status != MerchantStatus.ACTIVE) {

            log.warn(
                "Merchant bank account creation rejected, merchantId={}, status={}",
                merchantId,
                merchant.status
            )

            throw IllegalArgumentException(
                "Merchant is not ACTIVE"
            )
        }

        if (openingBalance < BigDecimal.ZERO) {

            throw IllegalArgumentException(
                "Opening balance cannot be negative"
            )
        }

        if (
            merchantBankAccountRepository
                .countByMerchant_MerchantId(merchantId) >= 1
        ) {

            log.warn(
                "Merchant bank account creation rejected, account already exists, merchantId={}",
                merchantId
            )

            throw IllegalArgumentException(
                "Merchant already has a bank account"
            )
        }

        val accountNumber =
            generateAccountNumber()

        val now = Instant.now()

        val account =
            MerchantBankAccount(
                merchantAccountId = UUID.randomUUID(),

                merchant =
                    merchant,

                accountNumber =
                    accountNumber,

                bankCode =
                    bankCode.uppercase(),

                ifscCode =
                    generateIfscCode(),

                currency =
                    "INR",

                ledgerBalance =
                    openingBalance,

                availableBalance =
                    openingBalance,

                status =
                    MerchantBankAccountStatus.ACTIVE,

                createdAt =
                    now,

                updatedAt =
                    now
            )

        val saved =
            merchantBankAccountRepository
                .save(account)

        log.info(
            "Merchant bank account created successfully, merchantId={}, merchantAccountId={}, bankCode={}",
            merchantId,
            saved.merchantAccountId,
            saved.bankCode
        )

        return MerchantBankAccountResponse.from(saved)
    }

    @Transactional(readOnly = true)
    fun getMerchant(
        merchantId: UUID
    ): MerchantResponse {

        log.info(
            "Fetching merchant, merchantId={}",
            merchantId
        )

        val merchant =
            merchantRepository
                .findById(merchantId)
                .orElseThrow {
                    IllegalArgumentException(
                        "Merchant not found: $merchantId"
                    )
                }

        return MerchantResponse.from(merchant)
    }

    private fun generateIfscCode(): String {

        val branchCode =
            (1000..9999).random()

        return "IFSC000$branchCode"
    }

    @Transactional(readOnly = true)
    fun getMerchantBankAccounts(
        merchantId: UUID
    ): List<MerchantBankAccountResponse> {

        log.info(
            "Fetching merchant bank accounts, merchantId={}",
            merchantId
        )

        return merchantBankAccountRepository
            .findByMerchant_MerchantId(merchantId)
            .map(
                MerchantBankAccountResponse::from
            )
    }

    private fun generateAccountNumber(): String {

        return "MER000" +
                UUID.randomUUID()
                    .toString()
                    .replace("-", "")
                    .filter { it.isDigit() }
                    .take(9)
    }
}