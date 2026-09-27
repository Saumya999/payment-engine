package com.payment.engine.com.payment.engine.common.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "simulation.bank-account")
data class BankAccountSimulationProperties(
    val enabled: Boolean = true,
    val batchSize: Int = 50,
    val fixedDelayMs: Long = 60_000
)