package com.payment.engine.com.payment.engine.bank.utility

import com.payment.engine.com.payment.engine.bank.domain.BankAccountType
import com.payment.engine.com.payment.engine.bank.model.CreateBankAccountRequest
import com.payment.engine.com.payment.engine.bank.repository.BankAccountRepository
import com.payment.engine.com.payment.engine.bank.service.BankAccountService
import com.payment.engine.com.payment.engine.customer.domain.CustomerStatus
import com.payment.engine.com.payment.engine.customer.repository.CustomerRepository

import org.slf4j.LoggerFactory
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Component
import java.math.BigDecimal
import java.util.UUID
import kotlin.random.Random

@Component
class BankAccountSimulator(
    private val customerRepository: CustomerRepository,
    private val bankAccountRepository: BankAccountRepository,
    private val bankAccountService: BankAccountService
) {

    private val log =
        LoggerFactory.getLogger(BankAccountSimulator::class.java)

    companion object {
        private const val MAX_ACCOUNTS_PER_CUSTOMER = 5
    }

    fun generateBankAccounts(
        batchSize: Int
    ): BankAccountSimulationResult {

        require(batchSize in 1..10_000) {
            "Batch size must be between 1 and 10000"
        }

        val startTime = System.currentTimeMillis()

        log.info(
            "Starting bank account simulation, requestedBatchSize={}",
            batchSize
        )

        val customers = customerRepository
            .findByStatus(
                CustomerStatus.ACTIVE,
                PageRequest.of(0, batchSize)
            )
            .content

        var created = 0
        var skipped = 0
        var rejected = 0
        var failed = 0

        for (customer in customers) {

            try {

                val existingAccountCount =
                    bankAccountRepository
                        .countByCustomer_CustomerId(customer.customerId)

                if (existingAccountCount >= MAX_ACCOUNTS_PER_CUSTOMER) {

                    skipped++

                    log.debug(
                        "Skipping customer because maximum account limit reached, customerId={}, accountCount={}",
                        customer.customerId,
                        existingAccountCount
                    )

                    continue
                }

                val accountType = randomAccountType()

                val openingBalance = randomOpeningBalance()

                val request = CreateBankAccountRequest(
                    customerId = customer.customerId,
                    accountType = accountType,
                    openingBalance = openingBalance
                )

                bankAccountService.createBankAccount(request)

                created++

                log.info(
                    "Bank account created by simulator, customerId={}, accountType={}, openingBalance={}",
                    customer.customerId,
                    accountType,
                    openingBalance
                )

            } catch (ex: IllegalArgumentException) {

                rejected++

                log.warn(
                    "Bank account simulation rejected for customerId={}, reason={}",
                    customer.customerId,
                    ex.message
                )

            } catch (ex: Exception) {

                failed++

                log.error(
                    "Unexpected error while creating bank account for customerId={}",
                    customer.customerId,
                    ex
                )
            }
        }

        val duration =
            System.currentTimeMillis() - startTime

        val result = BankAccountSimulationResult(
            requested = customers.size,
            created = created,
            skipped = skipped,
            rejected = rejected,
            failed = failed,
            durationMs = duration
        )

        log.info(
            "Bank account simulation completed, requested={}, created={}, skipped={}, rejected={}, failed={}, durationMs={}",
            result.requested,
            result.created,
            result.skipped,
            result.rejected,
            result.failed,
            result.durationMs
        )

        return result
    }

    private fun randomAccountType(): BankAccountType {

        return if (Random.nextInt(100) < 90) {
            BankAccountType.SAVINGS
        } else {
            BankAccountType.CURRENT
        }
    }

    private fun randomOpeningBalance(): BigDecimal {

        val amount =
            Random.nextInt(
                from = 5_000,
                until = 100_001
            )

        return BigDecimal(amount)
    }
}