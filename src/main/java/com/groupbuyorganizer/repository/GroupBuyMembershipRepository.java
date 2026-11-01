package com.groupbuyorganizer.repository;

import com.groupbuyorganizer.model.GroupBuyMembership;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface GroupBuyMembershipRepository extends JpaRepository<GroupBuyMembership, Long> {
    boolean existsByGroupBuy_IdAndUser_Id(Long groupBuyId, Long userId);
    long countByGroupBuy_Id(Long groupBuyId);
    List<GroupBuyMembership> findAllByUser_UsernameOrderByJoinedAtDesc(String username);
}
