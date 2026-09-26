package com.payment.engine.com.payment.engine.customer.service

import com.payment.engine.com.payment.engine.customer.domain.Customer
import com.payment.engine.com.payment.engine.customer.domain.CustomerStatus
import com.payment.engine.com.payment.engine.customer.model.CreateCustomerRequest
import com.payment.engine.com.payment.engine.customer.repository.CustomerRepository

import org.slf4j.LoggerFactory
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import java.time.Instant
import java.util.UUID

@Service
class CustomerService(
    private val customerRepository: CustomerRepository
) {

    private val log =
        LoggerFactory.getLogger(
            CustomerService::class.java
        )

    fun createCustomer(
        request: CreateCustomerRequest
    ): Customer {

        log.info(
            "Starting customer creation. mobile={}, firstName={}",
            request.mobileNumber,
            request.firstName
        )

        if (
            customerRepository.existsByMobileNumber(
                request.mobileNumber
            )
        ) {

            log.warn(
                "Customer creation rejected. Mobile already exists. mobile={}",
                request.mobileNumber
            )

            throw IllegalArgumentException(
                "Customer already exists for mobile number"
            )
        }

        val now = Instant.now()

        val customer = Customer(
            customerId = UUID.randomUUID(),

            firstName = request.firstName.trim(),

            lastName = request.lastName?.trim(),

            mobileNumber = request.mobileNumber,

            email = request.email?.trim(),

            status = CustomerStatus.ACTIVE,

            createdAt = now,

            updatedAt = now
        )

        log.debug(
            "Customer entity created. customerId={}",
            customer.customerId
        )

        val savedCustomer =
            customerRepository.save(customer)

        log.info(
            "Customer successfully created. customerId={}, mobile={}",
            savedCustomer.customerId,
            savedCustomer.mobileNumber
        )

        return savedCustomer
    }

    fun getCustomer(
        customerId: UUID
    ): Customer {

        log.info(
            "Fetching customer. customerId={}",
            customerId
        )

        return customerRepository
            .findById(customerId)
            .orElseThrow {

                log.warn(
                    "Customer not found. customerId={}",
                    customerId
                )

                NoSuchElementException(
                    "Customer not found: $customerId"
                )
            }
    }

    fun getCustomers(
        page: Int,
        size: Int
    ): List<Customer> {

        require(page >= 0) {
            "Page cannot be negative"
        }

        require(size in 1..100) {
            "Size must be between 1 and 100"
        }

        log.info(
            "Fetching customers. page={}, size={}",
            page,
            size
        )

        val result =
            customerRepository
                .findAll(
                    PageRequest.of(
                        page,
                        size
                    )
                )
                .content

        log.info(
            "Customers fetched successfully. count={}",
            result.size
        )

        return result
    }

    fun countCustomers(): Long {

        log.debug(
            "Counting customers"
        )

        return customerRepository.count()
    }
}