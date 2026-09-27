package com.payment.engine.com.payment.engine.merchant.model

import com.payment.engine.com.payment.engine.merchant.entity.Merchant
import com.payment.engine.com.payment.engine.merchant.entity.MerchantStatus
import com.payment.engine.com.payment.engine.merchant.entity.MerchantType
import java.time.Instant
import java.util.UUID

data class MerchantResponse(

    val merchantId: UUID,

    val merchantName: String,

    val mobileNumber: String,

    val email: String?,

    val merchantType: MerchantType,

    val status: MerchantStatus,

    val createdAt: Instant
) {

    companion object {

        fun from(
            merchant: Merchant
        ): MerchantResponse =
            MerchantResponse(
                merchantId = merchant.merchantId,
                merchantName = merchant.merchantName,
                mobileNumber = merchant.mobileNumber,
                email = merchant.email,
                merchantType = merchant.merchantType,
                status = merchant.status,
                createdAt = merchant.createdAt
            )
    }
}