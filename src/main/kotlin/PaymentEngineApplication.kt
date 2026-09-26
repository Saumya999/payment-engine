package com.payment.engine

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableScheduling
@ConfigurationPropertiesScan
class PaymentEngineApplication {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            runApplication<PaymentEngineApplication>(*args)
        }
    }
}