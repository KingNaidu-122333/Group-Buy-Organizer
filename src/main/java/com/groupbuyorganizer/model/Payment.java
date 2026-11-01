package com.groupbuyorganizer.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments", uniqueConstraints = {
        @UniqueConstraint(name = "uk_payment_stripe_session", columnNames = "stripe_session_id"),
        @UniqueConstraint(name = "uk_payment_deal_user", columnNames = {"group_buy_id", "user_id"})
})
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "stripe_session_id", nullable = false, unique = true, length = 255)
    private String stripeSessionId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "group_buy_id", nullable = false)
    private GroupBuy groupBuy;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private Long amountTotal;

    @Column(nullable = false, length = 3)
    private String currency;

    @Column(nullable = false, length = 30)
    private String status;

    @Column(nullable = false, updatable = false)
    private LocalDateTime paidAt;

    protected Payment() {}

    public Payment(String stripeSessionId, GroupBuy groupBuy, User user,
                   Long amountTotal, String currency, String status) {
        this.stripeSessionId = stripeSessionId;
        this.groupBuy = groupBuy;
        this.user = user;
        this.amountTotal = amountTotal;
        this.currency = currency;
        this.status = status;
        this.paidAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public String getStripeSessionId() { return stripeSessionId; }
    public GroupBuy getGroupBuy() { return groupBuy; }
    public User getUser() { return user; }
    public Long getAmountTotal() { return amountTotal; }
    public String getCurrency() { return currency; }
    public String getStatus() { return status; }
    public LocalDateTime getPaidAt() { return paidAt; }
}
