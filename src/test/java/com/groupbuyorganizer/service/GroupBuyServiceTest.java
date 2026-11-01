package com.groupbuyorganizer.service;

import com.groupbuyorganizer.model.GroupBuy;
import com.groupbuyorganizer.model.User;
import com.groupbuyorganizer.repository.GroupBuyMembershipRepository;
import com.groupbuyorganizer.repository.GroupBuyRepository;
import com.groupbuyorganizer.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GroupBuyServiceTest {
    @Mock private GroupBuyRepository groupBuyRepository;
    @Mock private GroupBuyMembershipRepository membershipRepository;
    @Mock private UserRepository userRepository;
    @InjectMocks private GroupBuyService groupBuyService;

    private GroupBuy deal;
    private User member;

    @BeforeEach
    void setUp() {
        deal = new GroupBuy("Team coffee", "Coffee bundle", "owner", "owner",
                2, 1, 10.0, "Open");
        deal.setId(7L);
        member = new User("member", "member@example.com", "encoded", User.Role.USER);
        member.setId(3L);
    }

    @Test
    void joiningFinalSlotRecordsMembershipAndMovesDealToReady() {
        when(groupBuyRepository.findByIdForUpdate(7L)).thenReturn(Optional.of(deal));
        when(userRepository.findByUsername("member")).thenReturn(Optional.of(member));
        when(membershipRepository.existsByGroupBuy_IdAndUser_Id(7L, 3L)).thenReturn(false);
        when(groupBuyRepository.save(any(GroupBuy.class))).thenAnswer(invocation -> invocation.getArgument(0));

        GroupBuy result = groupBuyService.joinGroup(7L, "member");

        assertEquals(2, result.getCurrentMembers());
        assertEquals("Ready to Order", result.getStatus());
        verify(membershipRepository).save(any());
        verify(groupBuyRepository).save(deal);
    }

    @Test
    void joiningTwiceIsRejected() {
        when(groupBuyRepository.findByIdForUpdate(7L)).thenReturn(Optional.of(deal));
        when(userRepository.findByUsername("member")).thenReturn(Optional.of(member));
        when(membershipRepository.existsByGroupBuy_IdAndUser_Id(7L, 3L)).thenReturn(true);

        assertThrows(IllegalStateException.class, () -> groupBuyService.joinGroup(7L, "member"));
        verify(membershipRepository, never()).save(any());
        verify(groupBuyRepository, never()).save(any());
    }

    @Test
    void closedDealCannotBeJoined() {
        deal.setStatus("Closed");
        when(groupBuyRepository.findByIdForUpdate(7L)).thenReturn(Optional.of(deal));
        when(userRepository.findByUsername("member")).thenReturn(Optional.of(member));
        when(membershipRepository.existsByGroupBuy_IdAndUser_Id(7L, 3L)).thenReturn(false);

        assertThrows(IllegalStateException.class, () -> groupBuyService.joinGroup(7L, "member"));
        verify(membershipRepository, never()).save(any());
    }

    @Test
    void missingDealIsReported() {
        when(groupBuyRepository.findByIdForUpdate(404L)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> groupBuyService.joinGroup(404L, "member"));
    }
}
