package com.payment.engine.com.payment.engine.bank.domain

import com.payment.engine.com.payment.engine.customer.domain.Customer
import jakarta.persistence.*
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

@Entity
@Table(
    name = "bank_account",
    schema = "core"
)
class BankAccount(

    @Id
    @Column(name = "account_id", nullable = false)
    var accountId: UUID,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
        name = "customer_id",
        nullable = false,
        foreignKey = ForeignKey(name = "fk_bank_account_customer")
    )
    var customer: Customer,

    @Column(name = "account_number", nullable = false, unique = true, length = 20)
    var accountNumber: String,

    @Column(name = "bank_code", nullable = false, length = 20)
    var bankCode: String,

    @Column(name = "ifsc_code", nullable = false, length = 20)
    var ifscCode: String,

    @Enumerated(EnumType.STRING)
    @Column(name = "account_type", nullable = false, length = 30)
    var accountType: BankAccountType,

    @Column(name = "currency", nullable = false, length = 3)
    var currency: String = "INR",

    @Column(
        name = "ledger_balance",
        nullable = false,
        precision = 19,
        scale = 2
    )
    var ledgerBalance: BigDecimal,

    @Column(
        name = "available_balance",
        nullable = false,
        precision = 19,
        scale = 2
    )
    var availableBalance: BigDecimal,

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    var status: BankAccountStatus,

    @Column(name = "created_at", nullable = false)
    var createdAt: Instant,

    @Column(name = "updated_at", nullable = false)
    var updatedAt: Instant,

    @Version
    @Column(name = "version", nullable = false)
    var version: Long = 0
)