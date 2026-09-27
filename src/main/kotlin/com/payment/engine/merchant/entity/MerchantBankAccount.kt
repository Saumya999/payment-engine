package com.payment.engine.com.payment.engine.merchant.entity

import jakarta.persistence.*
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

@Entity
@Table(
    name = "merchant_bank_account",
    schema = "core"
)
class MerchantBankAccount(

    @Id
    @Column(
        name = "merchant_account_id",
        nullable = false
    )
    var merchantAccountId: UUID,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
        name = "merchant_id",
        nullable = false,
        foreignKey = ForeignKey(
            name = "fk_merchant_bank_account_merchant"
        )
    )
    var merchant: Merchant,

    @Column(
        name = "account_number",
        nullable = false,
        unique = true,
        length = 20
    )
    var accountNumber: String,

    @Column(
        name = "bank_code",
        nullable = false,
        length = 20
    )
    var bankCode: String,

    @Column(
        name = "ifsc_code",
        nullable = false,
        length = 20
    )
    var ifscCode: String,

    @Column(
        name = "currency",
        nullable = false,
        length = 3
    )
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
    @Column(
        name = "status",
        nullable = false,
        length = 30
    )
    var status: MerchantBankAccountStatus,

    @Column(
        name = "created_at",
        nullable = false
    )
    var createdAt: Instant,

    @Column(
        name = "updated_at",
        nullable = false
    )
    var updatedAt: Instant,

    @Version
    @Column(
        name = "version",
        nullable = false
    )
    var version: Long = 0
)