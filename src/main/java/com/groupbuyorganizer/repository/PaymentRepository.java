package com.groupbuyorganizer.repository;

import com.groupbuyorganizer.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByStripeSessionId(String stripeSessionId);
    boolean existsByGroupBuy_IdAndUser_Id(Long groupBuyId, Long userId);
    List<Payment> findAllByOrderByPaidAtDesc();
}
