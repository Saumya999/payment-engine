package com.payment.engine.com.payment.engine.customer.repository

import com.payment.engine.com.payment.engine.customer.domain.Customer
import com.payment.engine.com.payment.engine.customer.domain.CustomerStatus
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface CustomerRepository :
    JpaRepository<Customer, UUID> {

    fun findByMobileNumber(
        mobileNumber: String
    ): Customer?

    fun findByCustomerId(customerId: UUID): Customer?

    fun existsByMobileNumber(
        mobileNumber: String
    ): Boolean

    fun findByStatus(
        status: CustomerStatus,
        pageable: Pageable
    ): Page<Customer>
}