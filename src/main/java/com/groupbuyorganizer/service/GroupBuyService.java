package com.groupbuyorganizer.service;

import com.groupbuyorganizer.model.GroupBuy;
import com.groupbuyorganizer.repository.GroupBuyRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class GroupBuyService {
    private final GroupBuyRepository repository;

    public GroupBuyService(GroupBuyRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<GroupBuy> getAllGroupBuys() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public GroupBuy getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Group buy not found: " + id));
    }

    @Transactional
    public GroupBuy save(GroupBuy groupBuy) {
        if (groupBuy.getCurrentMembers() == null) groupBuy.setCurrentMembers(1);
        if (groupBuy.getStatus() == null || groupBuy.getStatus().isBlank()) groupBuy.setStatus("Open");
        return repository.save(groupBuy);
    }

    @Transactional
    public GroupBuy joinGroup(Long id) {
        GroupBuy groupBuy = getById(id);
        if (!"Open".equalsIgnoreCase(groupBuy.getStatus())) {
            throw new IllegalStateException("This group buy is not open for new members.");
        }
        int current = groupBuy.getCurrentMembers() == null ? 0 : groupBuy.getCurrentMembers();
        int target = groupBuy.getTargetMembers() == null ? 0 : groupBuy.getTargetMembers();
        if (target < 2 || current >= target) {
            throw new IllegalStateException("This group buy has already reached its member target.");
        }
        groupBuy.setCurrentMembers(current + 1);
        if (current + 1 >= target) groupBuy.setStatus("Ready to Order");
        return repository.save(groupBuy);
    }

    @Transactional
    public GroupBuy closeGroup(Long id) {
        GroupBuy groupBuy = getById(id);
        groupBuy.setStatus("Closed");
        return repository.save(groupBuy);
    }
}
