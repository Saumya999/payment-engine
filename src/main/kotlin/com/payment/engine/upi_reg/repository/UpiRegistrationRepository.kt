package com.payment.engine.com.payment.engine.upi_reg.repository

import com.payment.engine.com.payment.engine.upi_reg.entity.UpiRegistration
import com.payment.engine.com.payment.engine.upi_reg.entity.UpiRegistrationStatus
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface UpiRegistrationRepository :
    JpaRepository<UpiRegistration, UUID> {

    fun findByVpa(vpa: String): UpiRegistration?

    fun existsByVpa(vpa: String): Boolean

    fun findByCustomer_CustomerId(
        customerId: UUID
    ): List<UpiRegistration>

    fun findByBankAccount_AccountId(
        accountId: UUID
    ): List<UpiRegistration>

    fun countByCustomer_CustomerId(
        customerId: UUID
    ): Long

    fun countByStatus(
        status: UpiRegistrationStatus
    ): Long
}