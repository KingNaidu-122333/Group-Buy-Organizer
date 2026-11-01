package com.groupbuyorganizer.service;

import com.groupbuyorganizer.model.GroupBuy;
import com.groupbuyorganizer.model.Payment;
import com.groupbuyorganizer.model.User;
import com.groupbuyorganizer.repository.GroupBuyMembershipRepository;
import com.groupbuyorganizer.repository.GroupBuyRepository;
import com.groupbuyorganizer.repository.PaymentRepository;
import com.groupbuyorganizer.repository.UserRepository;
import com.stripe.model.checkout.Session;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final GroupBuyRepository groupBuyRepository;
    private final GroupBuyMembershipRepository membershipRepository;
    private final UserRepository userRepository;

    @Value("${stripe.currency:usd}")
    private String stripeCurrency;

    public PaymentService(PaymentRepository paymentRepository,
                          GroupBuyRepository groupBuyRepository,
                          GroupBuyMembershipRepository membershipRepository,
                          UserRepository userRepository) {
        this.paymentRepository = paymentRepository;
        this.groupBuyRepository = groupBuyRepository;
        this.membershipRepository = membershipRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public Payment recordPaidSession(Session session) {
        if (session == null || !"paid".equalsIgnoreCase(session.getPaymentStatus())
                || session.getId() == null || session.getMetadata() == null) {
            throw new IllegalStateException("Stripe has not confirmed a completed payment.");
        }
        return paymentRepository.findByStripeSessionId(session.getId()).orElseGet(() -> {
            String groupIdValue = session.getMetadata().get("groupBuyId");
            String username = session.getMetadata().get("buyerUsername");
            if (groupIdValue == null || username == null || session.getAmountTotal() == null
                    || session.getCurrency() == null) {
                throw new IllegalStateException("Payment metadata is incomplete.");
            }
            Long groupId = Long.valueOf(groupIdValue);
            GroupBuy deal = groupBuyRepository.findById(groupId)
                    .orElseThrow(() -> new IllegalStateException("The associated deal no longer exists."));
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new IllegalStateException("The payment account no longer exists."));
            long expectedAmount = Math.round(deal.getPricePerPerson() * 100);
            if (session.getAmountTotal() != expectedAmount
                    || !stripeCurrency.equalsIgnoreCase(session.getCurrency())) {
                throw new IllegalStateException("The confirmed payment amount or currency does not match the deal.");
            }
            if (!membershipRepository.existsByGroupBuy_IdAndUser_Id(groupId, user.getId())) {
                throw new IllegalStateException("Only members of this deal can complete payment.");
            }
            if (paymentRepository.existsByGroupBuy_IdAndUser_Id(groupId, user.getId())) {
                throw new IllegalStateException("This member has already paid for this deal.");
            }
            return paymentRepository.save(new Payment(session.getId(), deal, user,
                    session.getAmountTotal(), session.getCurrency().toLowerCase(), "PAID"));
        });
    }
}
