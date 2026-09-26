package com.payment.engine.com.payment.engine.customer.model

import com.payment.engine.com.payment.engine.customer.domain.Customer
import com.payment.engine.com.payment.engine.customer.domain.CustomerStatus
import java.time.Instant
import java.util.UUID

data class CustomerResponse(

    val customerId: UUID,

    val firstName: String,

    val lastName: String?,

    val mobileNumber: String,

    val email: String?,

    val status: CustomerStatus,

    val createdAt: Instant
) {

    companion object {

        fun from(
            customer: Customer
        ): CustomerResponse {

            return CustomerResponse(
                customerId = customer.customerId,
                firstName = customer.firstName,
                lastName = customer.lastName,
                mobileNumber = customer.mobileNumber,
                email = customer.email,
                status = customer.status,
                createdAt = customer.createdAt
            )
        }
    }
}