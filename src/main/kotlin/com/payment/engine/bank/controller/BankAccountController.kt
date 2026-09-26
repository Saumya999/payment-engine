package com.payment.engine.com.payment.engine.bank.controller

import com.payment.engine.com.payment.engine.bank.model.BankAccountResponse
import com.payment.engine.com.payment.engine.bank.model.CreateBankAccountRequest
import com.payment.engine.com.payment.engine.bank.service.BankAccountService
import jakarta.validation.Valid
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import java.util.UUID

@RestController
@RequestMapping("/api/v1/accounts")
class BankAccountController(
    private val bankAccountService: BankAccountService
) {

    private val log =
        LoggerFactory.getLogger(BankAccountController::class.java)

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createBankAccount(
        @Valid @RequestBody request: CreateBankAccountRequest
    ): BankAccountResponse {

        log.info(
            "Received request to create bank account for customerId={}",
            request.customerId
        )

        return bankAccountService.createBankAccount(request)
    }

    @GetMapping("/{accountId}")
    fun getBankAccount(
        @PathVariable accountId: UUID
    ): BankAccountResponse {

        log.info(
            "Received request to fetch bank account, accountId={}",
            accountId
        )

        return bankAccountService.getBankAccount(accountId)
    }

    @GetMapping("/customer/{customerId}")
    fun getCustomerBankAccounts(
        @PathVariable customerId: UUID
    ): List<BankAccountResponse>? {

        log.info(
            "Received request to fetch bank accounts for customerId={}",
            customerId
        )

        return bankAccountService.getCustomerBankAccounts(customerId)
    }

    @GetMapping("/count")
    fun countAccounts(): Map<String, Long> {

        log.debug("Received request to count bank accounts")

        return mapOf(
            "count" to bankAccountService.countAccounts()
        )
    }
}