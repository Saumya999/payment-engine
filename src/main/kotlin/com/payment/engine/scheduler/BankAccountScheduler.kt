package com.payment.engine.com.payment.engine.scheduler


import com.payment.engine.com.payment.engine.bank.utility.BankAccountSimulator
import com.payment.engine.com.payment.engine.common.config.BankAccountSimulationProperties
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class BankAccountScheduler(
    private val simulator: BankAccountSimulator,
    private val properties: BankAccountSimulationProperties
) {

    private val log =
        LoggerFactory.getLogger(BankAccountScheduler::class.java)

    @Scheduled(
        fixedDelayString = "\${simulation.bank-account.fixed-delay-ms}",
        initialDelayString = "\${simulation.bank-account.initial-delay-ms}"
    )
    fun runSimulation() {

        if (!properties.enabled) {

            log.info(
                "Bank account simulation is disabled"
            )

            return
        }

        log.info(
            "Starting scheduled bank account simulation, batchSize={}",
            properties.batchSize
        )

        try {

            val result =
                simulator.generateBankAccounts(
                    properties.batchSize
                )

            log.info(
                "Scheduled bank account simulation completed: {}",
                result
            )

        } catch (ex: Exception) {

            log.error(
                "Bank account scheduled simulation failed",
                ex
            )
        }
    }
}