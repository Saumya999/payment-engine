package com.payment.engine.com.payment.engine.upi_reg.service


import com.payment.engine.com.payment.engine.bank.domain.BankAccountStatus
import com.payment.engine.com.payment.engine.bank.repository.BankAccountRepository
import com.payment.engine.com.payment.engine.customer.domain.Customer
import com.payment.engine.com.payment.engine.customer.domain.CustomerStatus
import com.payment.engine.com.payment.engine.customer.repository.CustomerRepository
import com.payment.engine.com.payment.engine.upi_reg.entity.UpiRegistration
import com.payment.engine.com.payment.engine.upi_reg.entity.UpiRegistrationStatus
import com.payment.engine.com.payment.engine.upi_reg.model.CreateUpiRegistrationRequest
import com.payment.engine.com.payment.engine.upi_reg.model.UpiRegistrationResponse
import com.payment.engine.com.payment.engine.upi_reg.repository.UpiRegistrationRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.util.UUID

@Service
class UpiRegistrationService(
    private val customerRepository: CustomerRepository,
    private val bankAccountRepository: BankAccountRepository,
    private val upiRegistrationRepository: UpiRegistrationRepository
) {

    private val log =
        LoggerFactory.getLogger(
            UpiRegistrationService::class.java
        )

    @Transactional
    fun registerVpa(
        request: CreateUpiRegistrationRequest
    ): UpiRegistrationResponse {

        log.info(
            "Starting UPI registration, customerId={}, accountId={}",
            request.customerId,
            request.accountId
        )

        /*
         * 1. Customer must exist.
         */
        val customer =
            customerRepository.findById(request.customerId)
                .orElseThrow {

                    log.warn(
                        "UPI registration failed. Customer not found, customerId={}",
                        request.customerId
                    )

                    IllegalArgumentException(
                        "Customer not found: ${request.customerId}"
                    )
                }

        /*
         * 2. Customer must be ACTIVE.
         */
        if (customer.status != CustomerStatus.ACTIVE) {

            log.warn(
                "UPI registration failed. Customer is not active, customerId={}, status={}",
                customer.customerId,
                customer.status
            )

            throw IllegalArgumentException(
                "Customer is not active"
            )
        }

        /*
         * 3. Bank account must exist.
         */
        val bankAccount =
            bankAccountRepository.findById(request.accountId!!)
                .orElseThrow {

                    log.warn(
                        "UPI registration failed. Bank account not found, customerId={}, accountId={}",
                        request.customerId,
                        request.accountId
                    )

                    IllegalArgumentException(
                        "Bank account not found: ${request.accountId}"
                    )
                }

        /*
         * 4. Bank account must belong to this customer.
         */
        if (
            bankAccount.customer.customerId !=
            customer.customerId
        ) {

            log.warn(
                "UPI registration failed. Bank account does not belong to customer, customerId={}, accountId={}, actualOwner={}",
                customer.customerId,
                bankAccount.accountId,
                bankAccount.customer.customerId
            )

            throw IllegalArgumentException(
                "Bank account does not belong to customer"
            )
        }

        /*
         * 5. Bank account must be ACTIVE.
         */
        if (
            bankAccount.status !=
            BankAccountStatus.ACTIVE
        ) {

            log.warn(
                "UPI registration failed. Bank account is not active, customerId={}, accountId={}, status={}",
                customer.customerId,
                bankAccount.accountId,
                bankAccount.status
            )

            throw IllegalArgumentException(
                "Bank account is not active"
            )
        }

        /*
         * 6. Generate bank-specific UPI handle.
         *
         * HDFC -> @okhdfc
         * SBIN -> @oksbin
         * ICICI -> @okicici
         */
        val upiHandle =
            "@ok${bankAccount.bankCode.lowercase()}"

        /*
         * 7. Generate VPA prefix.
         */
        val prefix =
            request.preferredVpaPrefix
                ?.trim()
                ?.lowercase()
                ?.takeIf { it.isNotBlank() }
                ?: generateVpaPrefix(customer)

        /*
         * 8. Build complete VPA.
         */
        val vpa =
            "$prefix$upiHandle"

        /*
         * 9. VPA must be globally unique.
         */
        if (
            upiRegistrationRepository.existsByVpa(vpa)
        ) {

            log.warn(
                "UPI registration failed. VPA already exists, customerId={}, accountId={}, vpa={}",
                customer.customerId,
                bankAccount.accountId,
                vpa
            )

            throw IllegalArgumentException(
                "VPA already exists: $vpa"
            )
        }

        val now = Instant.now()

        val registration =
            UpiRegistration(
                upiRegistrationId =
                    UUID.randomUUID(),

                customer = customer,

                bankAccount = bankAccount,

                vpa = vpa,

                upiHandle = upiHandle,

                status =
                    UpiRegistrationStatus.ACTIVE,

                createdAt = now,

                updatedAt = now
            )

        val savedRegistration =
            upiRegistrationRepository.save(
                registration
            )

        log.info(
            "UPI registration successful, registrationId={}, customerId={}, accountId={}, vpa={}",
            savedRegistration.upiRegistrationId,
            customer.customerId,
            bankAccount.accountId,
            savedRegistration.vpa
        )

        return UpiRegistrationResponse.from(
            savedRegistration
        )
    }

    @Transactional(readOnly = true)
    fun getRegistration(
        registrationId: UUID
    ): UpiRegistrationResponse {

        log.info(
            "Fetching UPI registration, registrationId={}",
            registrationId
        )

        val registration =
            upiRegistrationRepository.findById(
                registrationId
            ).orElseThrow {

                IllegalArgumentException(
                    "UPI registration not found: $registrationId"
                )
            }

        return UpiRegistrationResponse.from(
            registration
        )
    }

    @Transactional(readOnly = true)
    fun getCustomerRegistrations(
        customerId: UUID
    ): List<UpiRegistrationResponse> {

        log.info(
            "Fetching UPI registrations for customerId={}",
            customerId
        )

        return upiRegistrationRepository
            .findByCustomer_CustomerId(customerId)
            .map(UpiRegistrationResponse::from)
    }

    private fun generateVpaPrefix(
        customer: Customer
    ): String {

        val firstName =
            customer.firstName
                .lowercase()
                .replace(Regex("[^a-z0-9]"), "")

        val suffix =
            UUID.randomUUID()
                .toString()
                .replace("-", "")
                .take(6)

        return "$firstName$suffix"
    }
}