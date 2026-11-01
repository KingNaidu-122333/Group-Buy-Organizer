package com.groupbuyorganizer.controller;

import com.groupbuyorganizer.model.GroupBuy;
import com.groupbuyorganizer.service.GroupBuyService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AdminController {

    private final GroupBuyService groupBuyService;

    public AdminController(GroupBuyService groupBuyService) {
        this.groupBuyService = groupBuyService;
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public String adminDashboard(Model model) {
        model.addAttribute("groupBuys", groupBuyService.getAllGroupBuys());
        return "admin";
    }

    @PostMapping("/admin/groups/{id}/close")
    @PreAuthorize("hasRole('ADMIN')")
    public String closeGroup(@PathVariable Long id) {
        groupBuyService.closeGroup(id);
        return "redirect:/admin";
    }
}
