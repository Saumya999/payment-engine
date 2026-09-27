package com.payment.engine.com.payment.engine.merchant.model

import com.payment.engine.com.payment.engine.merchant.entity.MerchantType
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Pattern

data class CreateMerchantRequest(

    @field:NotBlank
    val merchantName: String,

    @field:NotBlank
    @field:Pattern(
        regexp = "^[6-9][0-9]{9}$",
        message = "Invalid Indian mobile number"
    )
    val mobileNumber: String,

    @field:Email
    val email: String? = null,

    @field:NotNull
    val merchantType: MerchantType
)