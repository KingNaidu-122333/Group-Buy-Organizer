package com.groupbuyorganizer.controller;

import com.groupbuyorganizer.model.GroupBuy;
import com.groupbuyorganizer.service.GroupBuyService;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class CheckoutController {
    private final GroupBuyService groupBuyService;

    @Value("${stripe.secret.key:}")
    private String stripeSecretKey;

    @Value("${stripe.currency:usd}")
    private String stripeCurrency;

    @Value("${app.url:http://localhost:8080}")
    private String appUrl;

    public CheckoutController(GroupBuyService groupBuyService) {
        this.groupBuyService = groupBuyService;
    }

    @PostMapping("/checkout/{id}")
    public String createCheckout(@PathVariable Long id) {
        GroupBuy deal = groupBuyService.getById(id);
        if (!"Ready to Order".equalsIgnoreCase(deal.getStatus())) return "redirect:/#deals";
        if (stripeSecretKey == null || stripeSecretKey.isBlank()) return "checkout-error";
        if (deal.getPricePerPerson() == null || deal.getPricePerPerson() <= 0) return "checkout-error";

        Stripe.apiKey = stripeSecretKey;
        try {
            SessionCreateParams params = SessionCreateParams.builder()
                    .setMode(SessionCreateParams.Mode.PAYMENT)
                    .setSuccessUrl(appUrl + "/checkout/success?session_id={CHECKOUT_SESSION_ID}")
                    .setCancelUrl(appUrl + "/#deals")
                    .putMetadata("groupBuyId", String.valueOf(deal.getId()))
                    .addLineItem(SessionCreateParams.LineItem.builder().setQuantity(1L)
                            .setPriceData(SessionCreateParams.LineItem.PriceData.builder()
                                    .setCurrency(stripeCurrency.toLowerCase())
                                    .setUnitAmount(Math.round(deal.getPricePerPerson() * 100))
                                    .setProductData(SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                            .setName(deal.getTitle()).build())
                                    .build()).build())
                    .build();
            Session session = Session.create(params);
            if (session.getUrl() == null || session.getUrl().isBlank()) return "checkout-error";
            return "redirect:" + session.getUrl();
        } catch (StripeException ex) {
            return "checkout-error";
        }
    }

    @GetMapping("/checkout/success")
    public String checkoutSuccess(@RequestParam(name = "session_id", required = false) String sessionId,
                                  Model model) {
        if (sessionId == null || sessionId.isBlank()
                || stripeSecretKey == null || stripeSecretKey.isBlank()) {
            return "checkout-error";
        }
        Stripe.apiKey = stripeSecretKey;
        try {
            Session session = Session.retrieve(sessionId);
            if (!"paid".equalsIgnoreCase(session.getPaymentStatus())
                    || session.getMetadata() == null
                    || session.getMetadata().get("groupBuyId") == null) {
                return "checkout-error";
            }

            Long groupBuyId = Long.valueOf(session.getMetadata().get("groupBuyId"));
            GroupBuy deal = groupBuyService.getById(groupBuyId);
            long expectedAmount = Math.round(deal.getPricePerPerson() * 100);
            if (session.getAmountTotal() == null || session.getAmountTotal() != expectedAmount
                    || !stripeCurrency.equalsIgnoreCase(session.getCurrency())) {
                return "checkout-error";
            }

            model.addAttribute("dealTitle", deal.getTitle());
            return "checkout-success";
        } catch (StripeException | IllegalArgumentException ex) {
            return "checkout-error";
        }
    }
}
