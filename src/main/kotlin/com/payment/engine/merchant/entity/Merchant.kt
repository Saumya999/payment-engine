package com.payment.engine.com.payment.engine.merchant.entity

import jakarta.persistence.*
import java.time.Instant
import java.util.UUID

@Entity
@Table(
    name = "merchant",
    schema = "core"
)
class Merchant(

    @Id
    @Column(
        name = "merchant_id",
        nullable = false
    )
    var merchantId: UUID,

    @Column(
        name = "merchant_name",
        nullable = false,
        length = 200
    )
    var merchantName: String,

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
        name = "merchant_type",
        nullable = false,
        length = 40
    )
    var merchantType: MerchantType,

    @Enumerated(EnumType.STRING)
    @Column(
        name = "status",
        nullable = false,
        length = 30
    )
    var status: MerchantStatus,

    @Column(
        name = "created_at",
        nullable = false
    )
    var createdAt: Instant,

    @Column(
        name = "updated_at",
        nullable = false
    )
    var updatedAt: Instant
)