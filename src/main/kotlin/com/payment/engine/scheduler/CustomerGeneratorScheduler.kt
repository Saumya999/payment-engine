package com.payment.engine.com.payment.engine.scheduler

import com.payment.engine.com.payment.engine.common.config.CustomerSimulationProperties
import com.payment.engine.com.payment.engine.customer.utility.CustomerSimulator
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class CustomerGeneratorScheduler(
    private val customerSimulator: CustomerSimulator,
    private val properties: CustomerSimulationProperties
) {

    private val log =
        LoggerFactory.getLogger(
            CustomerGeneratorScheduler::class.java
        )

    @Scheduled(
        fixedDelayString =
            "\${simulation.customer.fixed-delay-ms}",
        initialDelayString =
            "\${simulation.customer.initial-delay-ms}"
    )
    fun generateCustomers() {

        if (!properties.enabled) {

            log.debug(
                "Customer scheduler triggered but simulation is disabled"
            )

            return
        }

        log.info(
            "Customer scheduler triggered. batchSize={}, fixedDelayMs={}",
            properties.batchSize,
            properties.fixedDelayMs
        )

        try {

            val result =
                customerSimulator.generateCustomers(
                    properties.batchSize
                )

            log.info(
                "Customer scheduler completed. requested={}, created={}, rejected={}, failed={}, durationMs={}",
                result.requested,
                result.created,
                result.rejected,
                result.failed,
                result.durationMs
            )

        } catch (exception: Exception) {

            log.error(
                "Customer scheduler execution failed",
                exception
            )
        }
    }
}