package com.payment.engine.com.payment.engine.bank.model

import com.payment.engine.com.payment.engine.bank.domain.BankAccountType
import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.NotNull
import java.math.BigDecimal
import java.util.UUID

data class CreateBankAccountRequest(

    @field:NotNull
    val customerId: UUID,

    @field:NotNull
    val accountType: BankAccountType,

    @field:DecimalMin(
        value = "0.00",
        inclusive = true,
        message = "Opening balance cannot be negative"
    )
    val openingBalance: BigDecimal = BigDecimal.ZERO
)