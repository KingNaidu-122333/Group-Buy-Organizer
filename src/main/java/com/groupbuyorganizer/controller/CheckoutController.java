package com.groupbuyorganizer.controller;

import com.groupbuyorganizer.model.GroupBuy;
import com.groupbuyorganizer.service.GroupBuyService;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class CheckoutController {

    private final GroupBuyService groupBuyService;

    @Value("${stripe.secret.key:}")
    private String stripeSecretKey;

    @Value("${app.url:http://localhost:8080}")
    private String appUrl;

    public CheckoutController(GroupBuyService groupBuyService) {
        this.groupBuyService = groupBuyService;
    }

    @PostMapping("/checkout/{id}")
    public String createCheckout(@PathVariable Long id) {
        GroupBuy groupBuy = groupBuyService.getById(id);

        if (stripeSecretKey == null || stripeSecretKey.isBlank()) {
            return "redirect:/checkout/success?group=" + groupBuy.getId();
        }

        Stripe.apiKey = stripeSecretKey;

        try {
            SessionCreateParams params = SessionCreateParams.builder()
                    .setMode(SessionCreateParams.Mode.PAYMENT)
                    .setSuccessUrl(appUrl + "/checkout/success?group=" + groupBuy.getId())
                    .setCancelUrl(appUrl + "/")
                    .addLineItem(
                            SessionCreateParams.LineItem.builder()
                                    .setQuantity(1L)
                                    .setPriceData(
                                            SessionCreateParams.LineItem.PriceData.builder()
                                                    .setCurrency("usd")
                                                    .setUnitAmount(Math.round(groupBuy.getPricePerPerson() * 100))
                                                    .setProductData(
                                                            SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                                    .setName(groupBuy.getTitle())
                                                                    .build()
                                                    )
                                                    .build()
                                    )
                                    .build()
                    )
                    .build();

            Session session = Session.create(params);
            return "redirect:" + session.getUrl();
        } catch (StripeException e) {
            return "redirect:/checkout/success?group=" + groupBuy.getId();
        }
    }

    @GetMapping("/checkout/success")
    public String checkoutSuccess() {
        return "checkout-success";
    }
}
