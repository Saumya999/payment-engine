package com.payment.engine.com.payment.engine.scheduler

import com.payment.engine.com.payment.engine.common.config.MerchantSimulationProperties
import com.payment.engine.com.payment.engine.merchant.utility.MerchantSimulator
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class MerchantScheduler(
        private val merchantSimulator: MerchantSimulator,
        private val properties: MerchantSimulationProperties
                       ) {

    private val log =
        LoggerFactory.getLogger(
                MerchantScheduler::class.java
                               )

    @Scheduled(
            fixedDelayString =
                "\${simulation.merchant.fixed-delay-ms}",
            initialDelayString =
                "\${simulation.merchant.initial-delay-ms}"
              )
    fun runSimulation() {

        if (!properties.enabled) {

            log.debug(
                    "Merchant simulation is disabled"
                     )

            return
        }

        log.info(
                "Triggering scheduled merchant simulation, batchSize={}",
                properties.batchSize
                )

        try {

            val result =
                merchantSimulator.simulate(
                        properties.batchSize
                                          )

            log.info(
                    "Scheduled merchant simulation completed, merchantsCreated={}, accountsCreated={}, failed={}, durationMs={}",
                    result.merchantsCreated,
                    result.accountsCreated,
                    result.failed,
                    result.durationMs
                    )

        } catch (ex: Exception) {

            log.error(
                    "Scheduled merchant simulation failed",
                    ex
                     )
        }
    }
}