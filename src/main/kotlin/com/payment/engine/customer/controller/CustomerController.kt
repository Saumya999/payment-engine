package com.payment.engine.com.payment.engine.customer.controller

import com.payment.engine.com.payment.engine.customer.model.CreateCustomerRequest
import com.payment.engine.com.payment.engine.customer.model.CustomerResponse
import com.payment.engine.com.payment.engine.customer.service.CustomerService
import jakarta.validation.Valid
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import java.util.UUID

@RestController
@RequestMapping("/api/v1/customers")
class CustomerController(
    private val customerService: CustomerService
) {

    private val log =
        LoggerFactory.getLogger(
            CustomerController::class.java
        )

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createCustomer(
        @Valid
        @RequestBody
        request: CreateCustomerRequest
    ): CustomerResponse {

        log.info(
            "POST /api/v1/customers received. mobile={}",
            request.mobileNumber
        )

        val customer =
            customerService.createCustomer(
                request
            )

        log.info(
            "POST /api/v1/customers completed. customerId={}",
            customer.customerId
        )

        return CustomerResponse.from(
            customer
        )
    }

    @GetMapping("/{customerId}")
    fun getCustomer(
        @PathVariable
        customerId: UUID
    ): CustomerResponse {

        log.info(
            "GET /api/v1/customers/{} received",
            customerId
        )

        val customer =
            customerService.getCustomer(
                customerId
            )

        return CustomerResponse.from(
            customer
        )
    }

    @GetMapping
    fun getCustomers(
        @RequestParam(defaultValue = "1")
        page: Int,

        @RequestParam(defaultValue = "20")
        size: Int
    ): List<CustomerResponse> {

        log.info(
            "GET /api/v1/customers received. page={}, size={}",
            page,
            size
        )

        return customerService
            .getCustomers(page, size)
            .map(CustomerResponse::from)
    }

    @GetMapping("/count")
    fun countCustomers(): Map<String, Long> {

        log.info(
            "GET /api/v1/customers/count received"
        )

        val count =
            customerService.countCustomers()

        return mapOf(
            "count" to count
        )
    }
}