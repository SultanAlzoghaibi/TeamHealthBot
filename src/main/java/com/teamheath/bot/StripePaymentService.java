package com.teamheath.bot;

public class StripePaymentService implements PaymentService {
    @Override
    public void procesPayment(Double amount) {
        System.out.println("STRIPE: ");
        System.out.println(amount);
    }
}
