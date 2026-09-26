package com.payment.engine.com.payment.engine.common.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(
    prefix = "simulation.customer"
)
data class CustomerSimulationProperties(

    val enabled: Boolean = true,

    val batchSize: Int = 10,

    val fixedDelayMs: Long = 60_000
)