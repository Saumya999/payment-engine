package com.payment.engine.com.payment.engine.merchant.controller

import com.payment.engine.com.payment.engine.merchant.model.CreateMerchantRequest
import com.payment.engine.com.payment.engine.merchant.model.MerchantBankAccountResponse
import com.payment.engine.com.payment.engine.merchant.model.MerchantResponse
import com.payment.engine.com.payment.engine.merchant.service.MerchantService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import java.math.BigDecimal
import java.util.UUID

@RestController
@RequestMapping("/api/v1/merchants")
class MerchantController(
        private val merchantService: MerchantService
                        ) {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createMerchant(
            @Valid
            @RequestBody
            request: CreateMerchantRequest
                      ): MerchantResponse {

        return merchantService.createMerchant(
                request
                                             )
    }

    @PostMapping("/{merchantId}/bank-account")
    @ResponseStatus(HttpStatus.CREATED)
    fun createBankAccount(
            @PathVariable merchantId: UUID,

            @RequestParam
            bankCode: String,

            @RequestParam
            openingBalance: BigDecimal
                         ): MerchantBankAccountResponse {

        return merchantService.createMerchantBankAccount(
                merchantId = merchantId,
                bankCode = bankCode,
                openingBalance = openingBalance
                                                        )
    }

    @GetMapping("/{merchantId}")
    fun getMerchant(
            @PathVariable merchantId: UUID
                   ): MerchantResponse {

        return merchantService.getMerchant(
                merchantId
                                          )
    }

    @GetMapping("/{merchantId}/bank-accounts")
    fun getBankAccounts(
            @PathVariable merchantId: UUID
                       ): List<MerchantBankAccountResponse> {

        return merchantService.getMerchantBankAccounts(
                merchantId
                                                      )
    }
}