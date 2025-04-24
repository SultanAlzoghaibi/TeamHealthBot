package com.teamheath.bot.Achieve;

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
