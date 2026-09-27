package com.payment.engine.com.payment.engine.upi_reg.controller

import com.payment.engine.com.payment.engine.upi_reg.model.CreateUpiRegistrationRequest
import com.payment.engine.com.payment.engine.upi_reg.model.UpiRegistrationResponse
import com.payment.engine.com.payment.engine.upi_reg.service.UpiRegistrationService
import jakarta.validation.Valid
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import java.util.UUID

@RestController
@RequestMapping("/api/v1/upi-registrations")
class UpiRegistrationController(
    private val upiRegistrationService: UpiRegistrationService
) {

    private val log =
        LoggerFactory.getLogger(
            UpiRegistrationController::class.java
        )

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun registerVpa(
        @Valid
        @RequestBody request: CreateUpiRegistrationRequest
    ): UpiRegistrationResponse {

        log.info(
            "Received UPI registration request, customerId={}, accountId={}",
            request.customerId,
            request.accountId
        )

        return upiRegistrationService.registerVpa(
            request
        )
    }

    @GetMapping("/{registrationId}")
    fun getRegistration(
        @PathVariable registrationId: UUID
    ): UpiRegistrationResponse {

        log.info(
            "Received request for UPI registration, registrationId={}",
            registrationId
        )

        return upiRegistrationService.getRegistration(
            registrationId
        )
    }

    @GetMapping("/customer/{customerId}")
    fun getCustomerRegistrations(
        @PathVariable customerId: UUID
    ): List<UpiRegistrationResponse> {

        log.info(
            "Received request for customer UPI registrations, customerId={}",
            customerId
        )

        return upiRegistrationService
            .getCustomerRegistrations(customerId)
    }
}