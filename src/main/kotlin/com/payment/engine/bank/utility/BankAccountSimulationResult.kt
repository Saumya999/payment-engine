package com.payment.engine.com.payment.engine.bank.utility

data class BankAccountSimulationResult(
    val requested: Int,
    val created: Int,
    val skipped: Int,
    val rejected: Int,
    val failed: Int,
    val durationMs: Long
)