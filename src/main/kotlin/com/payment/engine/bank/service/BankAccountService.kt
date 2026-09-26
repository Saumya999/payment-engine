package com.payment.engine.com.payment.engine.bank.service

import com.payment.engine.com.payment.engine.bank.domain.BankAccount
import com.payment.engine.com.payment.engine.bank.domain.BankAccountStatus
import com.payment.engine.com.payment.engine.bank.model.BankAccountResponse
import com.payment.engine.com.payment.engine.bank.model.CreateBankAccountRequest
import com.payment.engine.com.payment.engine.bank.repository.BankAccountRepository
import com.payment.engine.com.payment.engine.customer.domain.CustomerStatus
import com.payment.engine.com.payment.engine.customer.repository.CustomerRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID
import kotlin.random.Random

@Service
class BankAccountService(
    private val bankAccountRepository: BankAccountRepository,
    private val customerRepository: CustomerRepository
) {

    companion object {
        const val MAX_ACCOUNTS_PER_CUSTOMER = 5
    }

    private val log = LoggerFactory.getLogger(BankAccountService::class.java)

    @Transactional
    fun createBankAccount(
        request: CreateBankAccountRequest
    ): BankAccountResponse {

        log.info(
            "Starting bank account creation for customerId={}",
            request.customerId
        )

        val customer = customerRepository.findById(request.customerId)
            .orElseThrow {
                IllegalArgumentException(
                    "Customer not found: ${request.customerId}"
                )
            }

        if (customer.status != CustomerStatus.ACTIVE) {
            log.warn(
                "Bank account creation rejected because customer is not active, customerId={}, status={}",
                customer.customerId,
                customer.status
            )

            throw IllegalArgumentException(
                "Bank account cannot be created for inactive customer"
            )
        }

        val existingAccountCount =
            bankAccountRepository.countByCustomer_CustomerId(
                request.customerId
            )

        if (existingAccountCount >= MAX_ACCOUNTS_PER_CUSTOMER) {

            log.warn(
                "Bank account creation rejected. customerId={}, existingAccountCount={}, maximumAllowed={}",
                request.customerId,
                existingAccountCount,
                MAX_ACCOUNTS_PER_CUSTOMER
            )

            throw IllegalArgumentException(
                "Customer can have maximum $MAX_ACCOUNTS_PER_CUSTOMER bank accounts"
            )
        }

        val accountNumber = generateAccountNumber()

        val now = java.time.Instant.now()

        val bankCodes = listOf("HDFC", "ICICI", "SBI", "AXIS", "SC", "AMEX", "JPMC", "PNB", "BOI", "BOM", "BOB", "UBI", "UCO", "BAND")
        val bankCode = bankCodes.random()

        val account = BankAccount(
            accountId = UUID.randomUUID(),
            customer = customer,
            accountNumber = accountNumber,
            bankCode = bankCode,
            ifscCode = generateIfscCode(),
            accountType = request.accountType,
            currency = "INR",
            ledgerBalance = request.openingBalance,
            availableBalance = request.openingBalance,
            status = BankAccountStatus.ACTIVE,
            createdAt = now,
            updatedAt = now
        )

        val savedAccount = bankAccountRepository.save(account)

        log.info(
            "Bank account created successfully, accountId={}, customerId={}, accountType={}, openingBalance={}",
            savedAccount.accountId,
            customer.customerId,
            savedAccount.accountType,
            savedAccount.availableBalance
        )

        return BankAccountResponse.from(savedAccount)
    }

    @Transactional(readOnly = true)
    fun getBankAccount(
        accountId: UUID
    ): BankAccountResponse {

        log.info(
            "Fetching bank account, accountId={}",
            accountId
        )

        val account = bankAccountRepository.findById(accountId)
            .orElseThrow {
                IllegalArgumentException(
                    "Bank account not found: $accountId"
                )
            }

        return BankAccountResponse.from(account)
    }

    @Transactional(readOnly = true)
    fun getCustomerBankAccounts(
        customerId: UUID
    ): List<BankAccountResponse>? {

        log.info(
            "Fetching bank accounts for customerId={}",
            customerId
        )

        if (!customerRepository.existsById(customerId)) {
            throw IllegalArgumentException(
                "Customer not found: $customerId"
            )
        }

        return bankAccountRepository
            .findByCustomer_CustomerId(customerId)
            ?.map(BankAccountResponse::from)
    }

    @Transactional(readOnly = true)
    fun countAccounts(): Long {

        log.debug("Fetching total bank account count")

        return bankAccountRepository.count()
    }

    private fun generateAccountNumber(): String {

        var accountNumber: String

        do {
            accountNumber =
                (100000000000L..999999999999L)
                    .random()
                    .toString()

        } while (
            bankAccountRepository.existsByAccountNumber(accountNumber)
        )

        return accountNumber
    }

    private fun generateIfscCode(): String {

        val branchCode =
            (1000..9999).random()

        return "IFSC000$branchCode"
    }
}