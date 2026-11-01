package com.groupbuyorganizer.controller;

import com.groupbuyorganizer.service.GroupBuyService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MyDealsController {
    private final GroupBuyService groupBuyService;

    public MyDealsController(GroupBuyService groupBuyService) {
        this.groupBuyService = groupBuyService;
    }

    @GetMapping("/my-deals")
    public String myDeals(Authentication authentication, Model model) {
        model.addAttribute("groupBuys", groupBuyService.getDealsCreatedBy(authentication.getName()));
        return "my-deals";
    }
}
