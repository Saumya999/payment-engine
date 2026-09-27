package com.payment.engine.com.payment.engine.upi_reg.utility

import com.payment.engine.com.payment.engine.bank.domain.BankAccount
import com.payment.engine.com.payment.engine.bank.domain.BankAccountStatus
import com.payment.engine.com.payment.engine.bank.repository.BankAccountRepository
import com.payment.engine.com.payment.engine.customer.domain.CustomerStatus
import com.payment.engine.com.payment.engine.customer.repository.CustomerRepository
import com.payment.engine.com.payment.engine.upi_reg.model.CreateUpiRegistrationRequest
import com.payment.engine.com.payment.engine.upi_reg.repository.UpiRegistrationRepository
import com.payment.engine.com.payment.engine.upi_reg.service.UpiRegistrationService
import org.slf4j.LoggerFactory
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Component
import java.util.UUID
import kotlin.random.Random

@Component
class UpiRegistrationSimulator(
    private val customerRepository: CustomerRepository,
    private val bankAccountRepository: BankAccountRepository,
    private val upiRegistrationService: UpiRegistrationService,
    private val upiRegistrationRepository: UpiRegistrationRepository
) {

    private val log =
        LoggerFactory.getLogger(
            UpiRegistrationSimulator::class.java
        )

    companion object {

        private const val MAX_FAILURE_SCENARIOS = 5

        private const val SCENARIO_NO_BANK_ACCOUNT = 1
        private const val SCENARIO_WRONG_CUSTOMER_ACCOUNT = 2
        private const val SCENARIO_INACTIVE_ACCOUNT = 3
        private const val SCENARIO_DUPLICATE_VPA = 4
        private const val SCENARIO_INVALID_CUSTOMER = 5
    }

    fun generateRegistrations(
        batchSize: Int,
        failureRatePercent: Int
    ): UpiRegistrationSimulationResult {

        require(batchSize in 1..10_000) {
            "Batch size must be between 1 and 10000"
        }

        require(failureRatePercent in 0..100) {
            "Failure rate must be between 0 and 100"
        }

        val startTime =
            System.currentTimeMillis()

        log.info(
            "Starting UPI registration simulation, batchSize={}, failureRatePercent={}",
            batchSize,
            failureRatePercent
        )

        val customers =
            customerRepository.findByStatus(
                CustomerStatus.ACTIVE,
                PageRequest.of(0, batchSize)
            ).content

        var successful = 0
        var failed = 0
        var skipped = 0

        for (customer in customers) {

            try {

                val accounts =
                    bankAccountRepository
                        .findByCustomer_CustomerId(
                            customer.customerId
                        )

                /*
                 * Customer without a bank account.
                 *
                 * This is a genuine failure scenario.
                 */
                if (accounts?.isEmpty() == true) {

                    failed++

                    log.warn(
                        "UPI registration failed during simulation. Customer has no bank account, customerId={}",
                        customer.customerId
                    )

                    continue
                }

                /*
                 * Sometimes intentionally create failure.
                 */
                val shouldCreateFailure =
                    Random.nextInt(100) <
                            failureRatePercent

                if (shouldCreateFailure) {

                    val scenario =
                        Random.nextInt(
                            1,
                            MAX_FAILURE_SCENARIOS + 1
                        )

                    try {

                        executeFailureScenario(
                            scenario = scenario,
                            customerId = customer.customerId,
                            accounts = accounts
                        )

                        /*
                         * If a failure scenario unexpectedly
                         * succeeds, count it as successful.
                         */
                        successful++

                    } catch (ex: IllegalArgumentException) {

                        failed++

                        log.warn(
                            "Expected UPI registration failure, customerId={}, scenario={}, reason={}",
                            customer.customerId,
                            scenario,
                            ex.message
                        )
                    }

                    continue
                }

                /*
                 * Normal successful registration.
                 */
                val account =
                    accounts?.random()

                val request =
                    CreateUpiRegistrationRequest(
                        customerId =
                            customer.customerId,

                        accountId =
                            account?.accountId
                    )

                upiRegistrationService.registerVpa(
                    request
                )

                successful++

                log.info(
                    "UPI registration simulation successful, customerId={}, accountId={}",
                    customer.customerId,
                    account?.accountId
                )

            } catch (ex: Exception) {

                failed++

                log.error(
                    "Unexpected error during UPI registration simulation, customerId={}",
                    customer.customerId,
                    ex
                )
            }
        }

        val duration =
            System.currentTimeMillis() - startTime

        val result =
            UpiRegistrationSimulationResult(
                requested = customers.size,
                successful = successful,
                failed = failed,
                skipped = skipped,
                durationMs = duration
            )

        log.info(
            "UPI registration simulation completed, requested={}, successful={}, failed={}, skipped={}, durationMs={}",
            result.requested,
            result.successful,
            result.failed,
            result.skipped,
            result.durationMs
        )

        return result
    }

    private fun executeFailureScenario(
        scenario: Int,
        customerId: UUID,
        accounts: List<BankAccount>?
    ) {

        when (scenario) {

            SCENARIO_NO_BANK_ACCOUNT -> {

                log.debug(
                    "Executing failure scenario NO_BANK_ACCOUNT, customerId={}",
                    customerId
                )

                /*
                 * Use a random UUID that does not represent
                 * any real bank account.
                 */
                val fakeAccountId =
                    UUID.randomUUID()

                upiRegistrationService.registerVpa(
                    CreateUpiRegistrationRequest(
                        customerId = customerId,
                        accountId = fakeAccountId
                    )
                )
            }

            SCENARIO_WRONG_CUSTOMER_ACCOUNT -> {

                log.debug(
                    "Executing failure scenario WRONG_CUSTOMER_ACCOUNT, customerId={}",
                    customerId
                )

                val otherCustomer =
                    customerRepository
                        .findAll()
                        .firstOrNull {
                            it.customerId != customerId &&
                                    it.status ==
                                    CustomerStatus.ACTIVE
                        }

                if (otherCustomer == null) {

                    log.debug(
                        "Unable to execute wrong customer account scenario because another customer was not found"
                    )

                    return
                }

                val otherAccounts =
                    bankAccountRepository
                        .findByCustomer_CustomerId(
                            otherCustomer.customerId
                        )

                if (otherAccounts?.isEmpty() == true) {

                    log.debug(
                        "Unable to execute wrong customer account scenario because other customer has no account, otherCustomerId={}",
                        otherCustomer.customerId
                    )

                    return
                }

                upiRegistrationService.registerVpa(
                    CreateUpiRegistrationRequest(
                        customerId = customerId,
                        accountId =
                            otherAccounts?.random()?.accountId
                    )
                )
            }

            SCENARIO_INACTIVE_ACCOUNT -> {

                log.debug(
                    "Executing failure scenario INACTIVE_ACCOUNT, customerId={}",
                    customerId
                )

                /*
                 * We cannot safely mutate an existing account
                 * here just for a simulation.
                 *
                 * Instead, look for an already inactive account.
                 */
                val inactiveAccount =
                    bankAccountRepository
                        .findByCustomer_CustomerId(
                            customerId
                        )
                        ?.firstOrNull {
                            it.status !=
                                    BankAccountStatus.ACTIVE
                        }

                if (inactiveAccount == null) {

                    log.debug(
                        "No inactive account available for customerId={}, skipping inactive-account failure scenario",
                        customerId
                    )

                    return
                }

                upiRegistrationService.registerVpa(
                    CreateUpiRegistrationRequest(
                        customerId = customerId,
                        accountId =
                            inactiveAccount.accountId
                    )
                )
            }

            SCENARIO_DUPLICATE_VPA -> {

                log.debug(
                    "Executing failure scenario DUPLICATE_VPA, customerId={}",
                    customerId
                )

                val existingRegistration =
                    findExistingRegistrationVpa()

                if (existingRegistration == null) {

                    log.debug(
                        "No existing VPA available for duplicate VPA scenario"
                    )

                    return
                }

                val account =
                    accounts?.random()

                /*
                 * Extract the prefix from the existing VPA.
                 *
                 * Example:
                 * rahul123@okhdfc
                 *
                 * becomes:
                 * rahul123
                 */
                val prefix =
                    existingRegistration
                        .substringBefore("@")

                upiRegistrationService.registerVpa(
                    CreateUpiRegistrationRequest(
                        customerId = customerId,
                        accountId = account?.accountId,
                        preferredVpaPrefix = prefix
                    )
                )
            }

            SCENARIO_INVALID_CUSTOMER -> {

                log.debug(
                    "Executing failure scenario INVALID_CUSTOMER, customerId={}",
                    customerId
                )

                val account =
                    accounts?.random()

                val fakeCustomerId =
                    UUID.randomUUID()

                upiRegistrationService.registerVpa(
                    CreateUpiRegistrationRequest(
                        customerId = fakeCustomerId,
                        accountId = account?.accountId
                    )
                )
            }
        }
    }

    private fun findExistingRegistrationVpa(): String? {

        /*
         * We intentionally don't inject the registration
         * repository here. Instead, this method can be
         * added once we expose a lookup method from the
         * registration service.
         *
         * For now this returns null.
         */
        return upiRegistrationRepository
            .findAll()
            .randomOrNull()
            ?.vpa
    }
}