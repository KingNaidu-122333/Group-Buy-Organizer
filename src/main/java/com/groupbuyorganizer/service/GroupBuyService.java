package com.groupbuyorganizer.service;

import com.groupbuyorganizer.model.GroupBuy;
import com.groupbuyorganizer.model.GroupBuyMembership;
import com.groupbuyorganizer.model.User;
import com.groupbuyorganizer.repository.GroupBuyMembershipRepository;
import com.groupbuyorganizer.repository.GroupBuyRepository;
import com.groupbuyorganizer.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
public class GroupBuyService {
    private final GroupBuyRepository repository;
    private final GroupBuyMembershipRepository membershipRepository;
    private final UserRepository userRepository;

    public GroupBuyService(GroupBuyRepository repository,
                           GroupBuyMembershipRepository membershipRepository,
                           UserRepository userRepository) {
        this.repository = repository;
        this.membershipRepository = membershipRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public List<GroupBuy> getAllGroupBuys() {
        return repository.findAllByOrderByCreatedAtDesc();
    }

    @Transactional(readOnly = true)
    public List<GroupBuy> searchDeals(String query, String status) {
        String term = query == null ? "" : query.trim().toLowerCase(Locale.ROOT);
        String selectedStatus = status == null ? "" : status.trim();
        return getAllGroupBuys().stream()
                .filter(deal -> term.isEmpty()
                        || deal.getTitle().toLowerCase(Locale.ROOT).contains(term)
                        || deal.getDescription().toLowerCase(Locale.ROOT).contains(term)
                        || deal.getOrganizer().toLowerCase(Locale.ROOT).contains(term))
                .filter(deal -> selectedStatus.isEmpty()
                        || "ALL".equalsIgnoreCase(selectedStatus)
                        || deal.getStatus().equalsIgnoreCase(selectedStatus))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<GroupBuy> getDealHistory() {
        return repository.findAllByOrderByCreatedAtDesc();
    }

    @Transactional(readOnly = true)
    public GroupBuy getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Group buy not found: " + id));
    }

    @Transactional
    public GroupBuy save(GroupBuy groupBuy) {
        groupBuy.setCurrentMembers(1);
        groupBuy.setStatus("Open");
        return repository.save(groupBuy);
    }

    @Transactional
    public GroupBuy joinGroup(Long id, String username) {
        GroupBuy deal = repository.findByIdForUpdate(id)
                .orElseThrow(() -> new IllegalArgumentException("Group buy not found: " + id));
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User account not found."));
        if (membershipRepository.existsByGroupBuy_IdAndUser_Id(id, user.getId())) {
            throw new IllegalStateException("You have already joined this group buy.");
        }
        if (!"Open".equalsIgnoreCase(deal.getStatus())) {
            throw new IllegalStateException("This group buy is not open for new members.");
        }
        int current = deal.getCurrentMembers() == null ? 0 : deal.getCurrentMembers();
        int target = deal.getTargetMembers() == null ? 0 : deal.getTargetMembers();
        if (target < 2 || current >= target) {
            throw new IllegalStateException("This group buy has already reached its member target.");
        }
        membershipRepository.save(new GroupBuyMembership(deal, user));
        deal.setCurrentMembers(current + 1);
        if (current + 1 >= target) deal.setStatus("Ready to Order");
        return repository.save(deal);
    }

    @Transactional
    public GroupBuy closeGroup(Long id) {
        GroupBuy deal = repository.findByIdForUpdate(id)
                .orElseThrow(() -> new IllegalArgumentException("Group buy not found: " + id));
        deal.setStatus("Closed");
        return repository.save(deal);
    }
}
