package com.groupbuyorganizer.controller;

import com.groupbuyorganizer.model.GroupBuy;
import com.groupbuyorganizer.service.GroupBuyService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.List;

@Controller
public class AdminController {
    private final GroupBuyService groupBuyService;

    public AdminController(GroupBuyService groupBuyService) {
        this.groupBuyService = groupBuyService;
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public String adminDashboard(Model model) {
        List<GroupBuy> deals = groupBuyService.getAllGroupBuys();
        model.addAttribute("groupBuys", deals);
        model.addAttribute("totalDeals", deals.size());
        model.addAttribute("openDeals", deals.stream().filter(d -> "Open".equalsIgnoreCase(d.getStatus())).count());
        model.addAttribute("readyDeals", deals.stream().filter(d -> "Ready to Order".equalsIgnoreCase(d.getStatus())).count());
        model.addAttribute("closedDeals", deals.stream().filter(d -> "Closed".equalsIgnoreCase(d.getStatus())).count());
        return "admin";
    }

    @PostMapping("/admin/groups/{id}/close")
    @PreAuthorize("hasRole('ADMIN')")
    public String closeGroup(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            groupBuyService.closeGroup(id);
            redirectAttributes.addFlashAttribute("message", "Deal closed successfully.");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("message", ex.getMessage());
        }
        return "redirect:/admin";
    }
}
