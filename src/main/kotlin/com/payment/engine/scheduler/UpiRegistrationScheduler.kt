package com.payment.engine.com.payment.engine.scheduler

import com.payment.engine.com.payment.engine.common.config.UpiRegistrationSimulationProperties
import com.payment.engine.com.payment.engine.upi_reg.utility.UpiRegistrationSimulator
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class UpiRegistrationScheduler(
    private val simulator: UpiRegistrationSimulator,
    private val properties: UpiRegistrationSimulationProperties
) {

    private val log =
        LoggerFactory.getLogger(
            UpiRegistrationScheduler::class.java
        )

    @Scheduled(
        fixedDelayString =
            "\${simulation.upi-registration.fixed-delay-ms}",
        initialDelayString =
            "\${simulation.upi-registration.initial-delay-ms}"
    )
    fun runSimulation() {

        if (!properties.enabled) {

            log.debug(
                "UPI registration simulation is disabled"
            )

            return
        }

        log.info(
            "Starting scheduled UPI registration simulation, batchSize={}, failureRatePercent={}",
            properties.batchSize,
            properties.failureRatePercent
        )

        try {

            val result =
                simulator.generateRegistrations(
                    batchSize =
                        properties.batchSize,

                    failureRatePercent =
                        properties.failureRatePercent
                )

            log.info(
                "Scheduled UPI registration simulation completed: {}",
                result
            )

        } catch (ex: Exception) {

            log.error(
                "UPI registration scheduled simulation failed",
                ex
            )
        }
    }
}