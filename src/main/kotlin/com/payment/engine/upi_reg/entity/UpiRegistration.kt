package com.payment.engine.com.payment.engine.upi_reg.entity

import com.payment.engine.com.payment.engine.bank.domain.BankAccount
import com.payment.engine.com.payment.engine.customer.domain.Customer
import jakarta.persistence.*
import java.time.Instant
import java.util.UUID

@Entity
@Table(
    name = "upi_registration",
    schema = "core"
)
class UpiRegistration(

    @Id
    @Column(name = "upi_registration_id", nullable = false)
    var upiRegistrationId: UUID,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
        name = "customer_id",
        nullable = false,
        foreignKey = ForeignKey(name = "fk_upi_registration_customer")
    )
    var customer: Customer,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
        name = "account_id",
        nullable = false,
        foreignKey = ForeignKey(name = "fk_upi_registration_account")
    )
    var bankAccount: BankAccount,

    @Column(
        name = "vpa",
        nullable = false,
        unique = true,
        length = 255
    )
    var vpa: String,

    @Column(
        name = "upi_handle",
        nullable = false,
        length = 50
    )
    var upiHandle: String,

    @Enumerated(EnumType.STRING)
    @Column(
        name = "status",
        nullable = false,
        length = 30
    )
    var status: UpiRegistrationStatus,

    @Column(name = "created_at", nullable = false)
    var createdAt: Instant,

    @Column(name = "updated_at", nullable = false)
    var updatedAt: Instant,

    @Version
    @Column(name = "version", nullable = false)
    var version: Long = 0
)