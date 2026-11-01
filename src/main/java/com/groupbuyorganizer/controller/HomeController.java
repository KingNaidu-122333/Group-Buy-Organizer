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
        populateHomeModel(model);
        if (authentication != null && authentication.isAuthenticated()
                && !"anonymousUser".equals(authentication.getName())) {
            model.addAttribute("currentUser", authentication.getName());
        }
        return "index";
    }

    @PostMapping("/groups")
    public String createGroup(@Valid @ModelAttribute("groupBuy") GroupBuy groupBuy,
                              BindingResult bindingResult, Model model,
                              Authentication authentication, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            populateHomeModel(model);
            return "index";
        }
        String username = authentication.getName();
        groupBuy.setOrganizer(username);
        groupBuy.setCreatedBy(username);
        groupBuy.setCurrentMembers(1);
        groupBuy.setStatus("Open");
        groupBuyService.save(groupBuy);
        redirectAttributes.addFlashAttribute("message", "Deal created successfully.");
        return "redirect:/#deals";
    }

    @PostMapping("/groups/{id}/join")
    public String joinGroup(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            GroupBuy joined = groupBuyService.joinGroup(id);
            redirectAttributes.addFlashAttribute("message", "You joined the deal: " + joined.getTitle() + ".");
        } catch (IllegalArgumentException | IllegalStateException ex) {
            redirectAttributes.addFlashAttribute("message", ex.getMessage());
        }
        return "redirect:/#deals";
    }

    private void populateHomeModel(Model model) {
        model.addAttribute("groupBuys", groupBuyService.getAllGroupBuys());
        if (!model.containsAttribute("groupBuy")) {
            model.addAttribute("groupBuy", new GroupBuy());
        }
    }
}
