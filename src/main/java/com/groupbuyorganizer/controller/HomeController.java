package com.groupbuyorganizer.controller;

import com.groupbuyorganizer.model.GroupBuy;
import com.groupbuyorganizer.model.User;
import com.groupbuyorganizer.service.GroupBuyService;
import com.groupbuyorganizer.service.UserService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
public class HomeController {

    private final GroupBuyService groupBuyService;
    private final UserService userService;

    public HomeController(GroupBuyService groupBuyService, UserService userService) {
        this.groupBuyService = groupBuyService;
        this.userService = userService;
    }

    @GetMapping("/")
    public String index(Model model, Authentication authentication) {
        model.addAttribute("groupBuys", groupBuyService.getAllGroupBuys());
        model.addAttribute("groupBuy", new GroupBuy());
        if (authentication != null && authentication.isAuthenticated()) {
            model.addAttribute("currentUser", authentication.getName());
        }
        return "index";
    }

    @PostMapping("/groups")
    public String createGroup(@Valid @ModelAttribute("groupBuy") GroupBuy groupBuy,
                              BindingResult bindingResult,
                              Model model,
                              Authentication authentication) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("groupBuys", groupBuyService.getAllGroupBuys());
            return "index";
        }

        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();
            groupBuy.setOrganizer(username);
            groupBuy.setCreatedBy(username);
        }

        groupBuyService.save(groupBuy);
        return "redirect:/";
    }

    @PostMapping("/groups/{id}/join")
    public String joinGroup(@PathVariable Long id) {
        groupBuyService.joinGroup(id);
        return "redirect:/";
    }
}
