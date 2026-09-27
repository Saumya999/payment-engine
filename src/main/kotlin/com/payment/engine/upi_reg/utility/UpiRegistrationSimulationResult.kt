package com.payment.engine.com.payment.engine.upi_reg.utility

data class UpiRegistrationSimulationResult(
    val requested: Int,
    val successful: Int,
    val failed: Int,
    val skipped: Int,
    val durationMs: Long
)