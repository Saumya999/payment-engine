package com.payment.engine.com.payment.engine.customer.model

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size

data class CreateCustomerRequest(

    @field:NotBlank
    @field:Size(max = 50)
    val firstName: String,

    @field:Size(max = 50)
    val lastName: String?,

    @field:NotBlank
    @field:Pattern(
        regexp = "^\\+91[6-9][0-9]{9}$",
        message = "Invalid Indian mobile number"
    )
    val mobileNumber: String,

    @field:Email
    @field:Size(max = 100)
    val email: String?
)