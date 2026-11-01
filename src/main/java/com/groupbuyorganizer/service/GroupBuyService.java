package com.groupbuyorganizer.service;

import com.groupbuyorganizer.model.GroupBuy;
import com.groupbuyorganizer.repository.GroupBuyRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GroupBuyService {

    private final GroupBuyRepository repository;

    public GroupBuyService(GroupBuyRepository repository) {
        this.repository = repository;
    }

    public List<GroupBuy> getAllGroupBuys() {
        return repository.findAll();
    }

    public GroupBuy getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Group buy not found: " + id));
    }

    public GroupBuy save(GroupBuy groupBuy) {
        if (groupBuy.getCurrentMembers() == null) {
            groupBuy.setCurrentMembers(1);
        }
        if (groupBuy.getStatus() == null || groupBuy.getStatus().isBlank()) {
            groupBuy.setStatus("Open");
        }
        return repository.save(groupBuy);
    }

    public GroupBuy joinGroup(Long id) {
        GroupBuy groupBuy = getById(id);
        int current = groupBuy.getCurrentMembers();
        int target = groupBuy.getTargetMembers();

        if (current >= target) {
            groupBuy.setStatus("Closed");
        } else {
            groupBuy.setCurrentMembers(current + 1);
            if (current + 1 >= target) {
                groupBuy.setStatus("Ready to Order");
            }
        }

        return repository.save(groupBuy);
    }

    public GroupBuy closeGroup(Long id) {
        GroupBuy groupBuy = getById(id);
        groupBuy.setStatus("Closed");
        return repository.save(groupBuy);
    }
}
