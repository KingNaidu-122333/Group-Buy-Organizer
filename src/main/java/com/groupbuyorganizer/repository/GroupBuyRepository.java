package com.groupbuyorganizer.repository;

import com.groupbuyorganizer.model.GroupBuy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GroupBuyRepository extends JpaRepository<GroupBuy, Long> {
    List<GroupBuy> findAllByOrderByCreatedAtDesc();
}
