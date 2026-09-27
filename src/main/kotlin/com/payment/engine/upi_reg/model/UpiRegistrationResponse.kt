package com.payment.engine.com.payment.engine.upi_reg.model

import com.payment.engine.com.payment.engine.upi_reg.entity.UpiRegistration
import com.payment.engine.com.payment.engine.upi_reg.entity.UpiRegistrationStatus
import java.time.Instant
import java.util.UUID

data class UpiRegistrationResponse(

    val upiRegistrationId: UUID,

    val customerId: UUID,

    val accountId: UUID,

    val vpa: String,

    val upiHandle: String,

    val status: UpiRegistrationStatus,

    val createdAt: Instant
) {

    companion object {

        fun from(
            registration: UpiRegistration
        ): UpiRegistrationResponse {

            return UpiRegistrationResponse(
                upiRegistrationId =
                    registration.upiRegistrationId,

                customerId =
                    registration.customer.customerId,

                accountId =
                    registration.bankAccount.accountId,

                vpa =
                    registration.vpa,

                upiHandle =
                    registration.upiHandle,

                status =
                    registration.status,

                createdAt =
                    registration.createdAt
            )
        }
    }
}