package com.payment.engine.com.payment.engine.merchant.utility

import com.payment.engine.com.payment.engine.common.config.MerchantSimulationProperties
import com.payment.engine.com.payment.engine.merchant.entity.MerchantType
import com.payment.engine.com.payment.engine.merchant.model.CreateMerchantRequest
import com.payment.engine.com.payment.engine.merchant.service.MerchantService
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.math.BigDecimal
import kotlin.random.Random

@Component
class MerchantSimulator(
        private val merchantService: MerchantService,
        private val properties: MerchantSimulationProperties
                       ) {

    private val log =
        LoggerFactory.getLogger(
                MerchantSimulator::class.java
                               )

    private val merchantPrefixes =
        listOf(
                "ABC",
                "City",
                "New",
                "Smart",
                "Daily",
                "Fresh",
                "Metro",
                "Royal",
                "National",
                "Super"
              )

    private val merchantTypes =
        MerchantType.entries

    private val bankCodes =
        listOf(
                "HDFC",
                "SBIN",
                "ICICI",
                "AXIS",
                "KOTAK"
              )

    fun simulate(
            count: Int = properties.batchSize
                ): MerchantSimulationResult {

        require(count > 0) {
            "Simulation count must be greater than zero"
        }

        require(count <= 10_000) {
            "Simulation count cannot exceed 10,000"
        }

        val startTime =
            System.currentTimeMillis()

        log.info(
                "Starting merchant simulation, requestedCount={}, failureRatePercent={}",
                count,
                properties.failureRatePercent
                )

        var merchantsCreated = 0
        var accountsCreated = 0
        var skipped = 0
        var failed = 0

        repeat(count) { index ->

            try {

                /*
                 * Generate a merchant.
                 */
                val request =
                    generateMerchantRequest()

                /*
                 * Occasionally generate an intentional
                 * failure scenario.
                 */
                val failureScenario =
                    shouldGenerateFailure()

                if (failureScenario) {

                    log.debug(
                            "Generating merchant failure scenario, iteration={}",
                            index + 1
                             )

                    simulateFailureScenario(
                            request = request
                                           )

                    failed++

                    return@repeat
                }

                /*
                 * Step 1:
                 * Create merchant.
                 */
                val merchant =
                    merchantService.createMerchant(
                            request
                                                  )

                merchantsCreated++

                log.debug(
                        "Merchant created by simulator, merchantId={}, merchantName={}",
                        merchant.merchantId,
                        merchant.merchantName
                         )

                /*
                 * Step 2:
                 * Create settlement bank account.
                 */
                val bankCode =
                    bankCodes.random()

                val openingBalance =
                    generateOpeningBalance()

                val account =
                    merchantService.createMerchantBankAccount(
                            merchantId =
                                merchant.merchantId,

                            bankCode =
                                bankCode,

                            openingBalance =
                                openingBalance
                                                             )

                accountsCreated++

                log.info(
                        "Merchant onboarding simulation successful, merchantId={}, merchantAccountId={}, bankCode={}, openingBalance={}",
                        merchant.merchantId,
                        account.merchantAccountId,
                        account.bankCode,
                        account.availableBalance
                        )

            } catch (ex: IllegalArgumentException) {

                /*
                 * Business failures are expected in a simulator.
                 */
                failed++

                log.warn(
                        "Merchant simulation business failure, iteration={}, reason={}",
                        index + 1,
                        ex.message
                        )

            } catch (ex: Exception) {

                /*
                 * Unexpected technical failure.
                 */
                failed++

                log.error(
                        "Unexpected merchant simulation failure, iteration={}",
                        index + 1,
                        ex
                         )
            }
        }

        val duration =
            System.currentTimeMillis() - startTime

        val result =
            MerchantSimulationResult(
                    requested = count,
                    merchantsCreated = merchantsCreated,
                    accountsCreated = accountsCreated,
                    skipped = skipped,
                    failed = failed,
                    durationMs = duration
                                    )

        log.info(
                "Merchant simulation completed, requested={}, merchantsCreated={}, accountsCreated={}, skipped={}, failed={}, durationMs={}",
                result.requested,
                result.merchantsCreated,
                result.accountsCreated,
                result.skipped,
                result.failed,
                result.durationMs
                )

        return result
    }

    private fun generateMerchantRequest():
            CreateMerchantRequest {

        val merchantName =
            generateMerchantName()

        val mobileNumber =
            generateMobileNumber()

        val merchantType =
            merchantTypes.random()

        return CreateMerchantRequest(
                merchantName = merchantName,
                mobileNumber = mobileNumber,
                email =
                    generateEmail(
                            merchantName
                                 ),
                merchantType = merchantType
                                    )
    }

    private fun generateMerchantName(): String {

        val prefix =
            merchantPrefixes.random()

        val number =
            Random.nextInt(
                    from = 100,
                    until = 9999
                          )

        val suffix =
            listOf(
                    "Store",
                    "Mart",
                    "Shop",
                    "Traders",
                    "Foods",
                    "Electronics",
                    "Supermarket"
                  ).random()

        return "$prefix $suffix $number"
    }

    private fun generateMobileNumber(): String {

        val firstDigit =
            Random.nextInt(6, 10)

        val remaining =
            Random.nextInt(
                    from = 100_000_000,
                    until = 1_000_000_000
                          )

        return "$firstDigit$remaining"
    }

    private fun generateEmail(
            merchantName: String
                             ): String {

        val normalized =
            merchantName
                    .lowercase()
                    .replace(
                            Regex("[^a-z0-9]"),
                            ""
                            )

        return "$normalized@merchant.simulator"
    }

    private fun generateOpeningBalance():
            BigDecimal {

        val amount =
            Random.nextInt(
                    from = 10_000,
                    until = 1_000_000
                          )

        return BigDecimal(amount)
    }

    private fun shouldGenerateFailure():
            Boolean {

        if (properties.failureRatePercent <= 0) {
            return false
        }

        if (properties.failureRatePercent >= 100) {
            return true
        }

        return Random.nextInt(1, 101) <=
               properties.failureRatePercent
    }

    private fun simulateFailureScenario(
            request: CreateMerchantRequest
                                       ) {

        /*
         * Randomly choose one of the supported
         * failure scenarios.
         */
        when (Random.nextInt(0, 3)) {

            0 -> {

                /*
                 * Negative opening balance.
                 */
                val merchant =
                    merchantService.createMerchant(
                            request
                                                  )

                merchantService.createMerchantBankAccount(
                        merchantId =
                            merchant.merchantId,

                        bankCode =
                            bankCodes.random(),

                        openingBalance =
                            BigDecimal("-100.00")
                                                         )
            }

            1 -> {

                /*
                 * Invalid merchant ID.
                 */
                merchantService.createMerchantBankAccount(
                        merchantId =
                            java.util.UUID.randomUUID(),

                        bankCode =
                            bankCodes.random(),

                        openingBalance =
                            BigDecimal("50000.00")
                                                         )
            }

            else -> {

                /*
                 * Create the merchant first and attempt
                 * to create a second account.
                 *
                 * Our business rule currently allows
                 * one settlement account per merchant.
                 */
                val merchant =
                    merchantService.createMerchant(
                            request
                                                  )

                merchantService.createMerchantBankAccount(
                        merchantId =
                            merchant.merchantId,

                        bankCode =
                            bankCodes.random(),

                        openingBalance =
                            BigDecimal("50000.00")
                                                         )

                merchantService.createMerchantBankAccount(
                        merchantId =
                            merchant.merchantId,

                        bankCode =
                            bankCodes.random(),

                        openingBalance =
                            BigDecimal("75000.00")
                                                         )
            }
        }
    }
}