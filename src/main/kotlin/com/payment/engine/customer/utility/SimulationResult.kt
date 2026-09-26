package com.payment.engine.com.payment.engine.customer.utility

data class SimulationResult(

    val requested: Int,

    val created: Int,

    val rejected: Int,

    val failed: Int,

    val durationMs: Long
)