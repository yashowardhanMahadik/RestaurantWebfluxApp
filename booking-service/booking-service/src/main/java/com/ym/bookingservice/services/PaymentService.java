package com.ym.bookingservice.services;

import com.ym.bookingservice.model.Payment;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class PaymentService {
    public Mono<Boolean> processPayment(Payment payment) {

        return Mono.just(payment.getAmount() > 0);
    }
}
