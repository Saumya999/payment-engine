package com.payment.engine.com.payment.engine.upi_reg.model

import jakarta.validation.constraints.NotNull
import java.util.UUID

data class CreateUpiRegistrationRequest(

    @field:NotNull
    val customerId: UUID,

    @field:NotNull
    val accountId: UUID? = null,

    val preferredVpaPrefix: String? = null
)