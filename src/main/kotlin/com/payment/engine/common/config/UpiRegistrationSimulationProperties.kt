package com.payment.engine.com.payment.engine.common.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "simulation.upi-registration")
data class UpiRegistrationSimulationProperties(

    val enabled: Boolean = true,

    val batchSize: Int = 50,

    val fixedDelayMs: Long = 60_000,

    /**
     * Percentage of simulator attempts that intentionally
     * use an invalid scenario.
     */
    val failureRatePercent: Int = 20
)