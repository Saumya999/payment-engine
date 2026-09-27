package com.payment.engine.com.payment.engine.merchant.repository

import com.payment.engine.com.payment.engine.merchant.entity.Merchant
import com.payment.engine.com.payment.engine.merchant.entity.MerchantStatus
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface MerchantRepository :
    JpaRepository<Merchant, UUID> {

    fun findByMobileNumber(
        mobileNumber: String
    ): Merchant?

    fun existsByMobileNumber(
        mobileNumber: String
    ): Boolean

    fun findByStatus(
        status: MerchantStatus,
        pageable: Pageable
    ): Page<Merchant>

    fun countByStatus(
        status: MerchantStatus
    ): Long
}