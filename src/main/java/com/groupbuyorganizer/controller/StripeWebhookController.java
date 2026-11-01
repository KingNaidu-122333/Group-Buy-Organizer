package com.groupbuyorganizer.controller;

import com.groupbuyorganizer.service.PaymentService;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.StripeObject;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class StripeWebhookController {
    private final PaymentService paymentService;

    @Value("${stripe.webhook.secret:}")
    private String webhookSecret;

    public StripeWebhookController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping(value = "/webhooks/stripe", consumes = "application/json")
    public ResponseEntity<String> handleStripeWebhook(
            @RequestBody String payload,
            @RequestHeader(name = "Stripe-Signature", required = false) String signature) {
        if (webhookSecret == null || webhookSecret.isBlank()) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("Webhook is not configured");
        }
        if (signature == null || signature.isBlank()) {
            return ResponseEntity.badRequest().body("Missing Stripe signature");
        }
        try {
            Event event = Webhook.constructEvent(payload, signature, webhookSecret);
            if ("checkout.session.completed".equals(event.getType())
                    || "checkout.session.async_payment_succeeded".equals(event.getType())) {
                StripeObject stripeObject = event.getDataObjectDeserializer().getObject().orElse(null);
                if (!(stripeObject instanceof Session session)) {
                    return ResponseEntity.badRequest().body("Invalid checkout session payload");
                }
                paymentService.recordPaidSession(session);
            }
            return ResponseEntity.ok("received");
        } catch (SignatureVerificationException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid webhook signature");
        } catch (IllegalArgumentException | IllegalStateException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid payment event");
        }
    }
}
