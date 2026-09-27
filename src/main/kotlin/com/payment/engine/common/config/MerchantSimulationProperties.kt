package com.payment.engine.com.payment.engine.common.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(
        prefix = "simulation.merchant"
                        )
data class MerchantSimulationProperties(

        /**
         * Enables/disables automatic merchant simulation.
         */
        val enabled: Boolean = true,

        /**
         * Number of merchants to create per scheduler execution.
         */
        val batchSize: Int = 20,

        /**
         * Scheduler delay between executions.
         */
        val fixedDelayMs: Long = 60_000,

        /**
         * Percentage of simulation requests that should
         * intentionally generate a failure scenario.
         */
        val failureRatePercent: Int = 10)