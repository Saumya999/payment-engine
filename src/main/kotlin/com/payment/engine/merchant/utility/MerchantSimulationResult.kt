package com.payment.engine.com.payment.engine.merchant.utility

data class MerchantSimulationResult(

        val requested: Int,

        val merchantsCreated: Int,

        val accountsCreated: Int,

        val skipped: Int,

        val failed: Int,

        val durationMs: Long)