package com.payment.engine.com.payment.engine.bank.repository

import com.payment.engine.com.payment.engine.bank.domain.BankAccount
import com.payment.engine.com.payment.engine.bank.domain.BankAccountStatus
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface BankAccountRepository : JpaRepository<BankAccount, UUID> {

    fun findByAccountNumber(accountNumber: String): BankAccount?

    fun findByCustomer_CustomerId(customerId: UUID): List<BankAccount>?

    fun existsByCustomer_CustomerId(customerId: UUID): Boolean

    fun existsByAccountNumber(accountNumber: String): Boolean

    fun countByCustomer_CustomerId(customerId: UUID): Long

    fun countByStatus(status: BankAccountStatus): Long
}