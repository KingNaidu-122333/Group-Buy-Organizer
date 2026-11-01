package com.groupbuyorganizer.controller;

import com.groupbuyorganizer.model.GroupBuy;
import com.groupbuyorganizer.service.GroupBuyService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class HomeController {

    private final GroupBuyService groupBuyService;

    public HomeController(GroupBuyService groupBuyService) {
        this.groupBuyService = groupBuyService;
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
                              Authentication authentication,
                              RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("groupBuys", groupBuyService.getAllGroupBuys());
            redirectAttributes.addFlashAttribute("message", "Please fix the form errors before creating the deal.");
            return "index";
        }

        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getName())) {
            groupBuy.setOrganizer("Guest");
            groupBuy.setCreatedBy("Guest");
        } else {
            String username = authentication.getName();
            groupBuy.setOrganizer(username);
            groupBuy.setCreatedBy(username);
        }

        groupBuyService.save(groupBuy);
        redirectAttributes.addFlashAttribute("message", "Deal created successfully and added to current deals.");
        return "redirect:/";
    }

    @PostMapping("/groups/{id}/join")
    public String joinGroup(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        GroupBuy groupBuy = groupBuyService.getById(id);
        groupBuyService.joinGroup(id);
        redirectAttributes.addFlashAttribute("message", "You joined the deal: " + groupBuy.getTitle() + ".");
        return "redirect:/";
    }
}
