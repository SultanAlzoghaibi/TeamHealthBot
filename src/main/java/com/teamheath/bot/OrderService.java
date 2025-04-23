package com.teamheath.bot;

public class OrderService {



    private PaymentService paymentService;

    public OrderService(StripePaymentService stripePaymentService) {
        this.paymentService = paymentService;
    }

    public void setPaymentService(PaymentService paymentService) {
        this.paymentService = paymentService;
    }


    public void placeOrder() {
        var paymentService = new StripePaymentService();
        paymentService.procesPayment(10.00);

    }
}
