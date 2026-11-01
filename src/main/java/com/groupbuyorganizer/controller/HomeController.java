package com.groupbuyorganizer.controller;

import com.groupbuyorganizer.model.GroupBuy;
import com.groupbuyorganizer.service.GroupBuyService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class HomeController {

    private final GroupBuyService groupBuyService;

    public HomeController(GroupBuyService groupBuyService) {
        this.groupBuyService = groupBuyService;
    }

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("groupBuys", groupBuyService.getAllGroupBuys());
        model.addAttribute("groupBuy", new GroupBuy());
        return "index";
    }

    @PostMapping("/groups")
    public String createGroup(@Valid @ModelAttribute("groupBuy") GroupBuy groupBuy,
                              BindingResult bindingResult,
                              Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("groupBuys", groupBuyService.getAllGroupBuys());
            return "index";
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
