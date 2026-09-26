package com.payment.engine.com.payment.engine.customer.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.Instant
import java.util.UUID

@Entity
@Table(
    name = "customer",
    schema = "core"
)
class Customer(

    @Id
    @Column(name = "customer_id")
    var customerId: UUID,

    @Column(
        name = "first_name",
        nullable = false,
        length = 100
    )
    var firstName: String,

    @Column(
        name = "last_name",
        length = 100
    )
    var lastName: String?,

    @Column(
        name = "mobile_number",
        nullable = false,
        unique = true,
        length = 20
    )
    var mobileNumber: String,

    @Column(
        name = "email",
        length = 255
    )
    var email: String?,

    @Enumerated(EnumType.STRING)
    @Column(
        name = "status",
        nullable = false,
        length = 30
    )
    var status: CustomerStatus,

    @Column(name = "created_at", nullable = false)
    var createdAt: Instant,

    @Column(name = "updated_at", nullable = false)
    var updatedAt: Instant
)