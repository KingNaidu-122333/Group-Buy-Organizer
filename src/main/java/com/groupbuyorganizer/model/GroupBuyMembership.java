package com.groupbuyorganizer.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "group_buy_memberships",
       uniqueConstraints = @UniqueConstraint(name = "uk_group_buy_member", columnNames = {"group_buy_id", "user_id"}))
public class GroupBuyMembership {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "group_buy_id", nullable = false)
    private GroupBuy groupBuy;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, updatable = false)
    private LocalDateTime joinedAt;

    protected GroupBuyMembership() {}

    public GroupBuyMembership(GroupBuy groupBuy, User user) {
        this.groupBuy = groupBuy;
        this.user = user;
        this.joinedAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public GroupBuy getGroupBuy() { return groupBuy; }
    public User getUser() { return user; }
    public LocalDateTime getJoinedAt() { return joinedAt; }
}
