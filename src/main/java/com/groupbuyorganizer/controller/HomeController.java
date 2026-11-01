package com.groupbuyorganizer.controller;

import com.groupbuyorganizer.model.GroupBuy;
import com.groupbuyorganizer.service.GroupBuyService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.List;

@Controller
public class HomeController {
    private final GroupBuyService groupBuyService;

    public HomeController(GroupBuyService groupBuyService) {
        this.groupBuyService = groupBuyService;
    }

    @InitBinder("groupBuy")
    void configureGroupBuyBinding(WebDataBinder binder) {
        binder.setAllowedFields("title", "description", "targetMembers", "pricePerPerson");
    }

    @GetMapping("/")
    public String index(@RequestParam(required = false) String q,
                        @RequestParam(required = false) String status,
                        Model model, Authentication authentication) {
        populateHomeModel(model);
        List<GroupBuy> allDeals = groupBuyService.getAllGroupBuys();
        List<GroupBuy> deals = groupBuyService.searchDeals(q, status);
        model.addAttribute("groupBuys", deals);
        model.addAttribute("totalDealCount", allDeals.size());
        model.addAttribute("openDealCount", allDeals.stream().filter(d -> "Open".equalsIgnoreCase(d.getStatus())).count());
        model.addAttribute("readyDealCount", allDeals.stream().filter(d -> "Ready to Order".equalsIgnoreCase(d.getStatus())).count());
        model.addAttribute("searchQuery", q == null ? "" : q);
        model.addAttribute("selectedStatus", status == null ? "" : status);
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
            model.addAttribute("groupBuys", groupBuyService.getAllGroupBuys());
            model.addAttribute("searchQuery", "");
            model.addAttribute("selectedStatus", "");
            return "index";
        }
        String username = authentication.getName();
        groupBuy.setOrganizer(username);
        groupBuy.setCreatedBy(username);
        groupBuyService.save(groupBuy);
        redirectAttributes.addFlashAttribute("message", "Deal created and added to deal history.");
        return "redirect:/#history";
    }

    @PostMapping("/groups/{id}/join")
    public String joinGroup(@PathVariable Long id, Authentication authentication,
                            RedirectAttributes redirectAttributes) {
        try {
            GroupBuy joined = groupBuyService.joinGroup(id, authentication.getName());
            redirectAttributes.addFlashAttribute("message", "You joined the deal: " + joined.getTitle() + ".");
        } catch (IllegalArgumentException | IllegalStateException ex) {
            redirectAttributes.addFlashAttribute("message", ex.getMessage());
        }
        return "redirect:/#deals";
    }

    private void populateHomeModel(Model model) {
        model.addAttribute("dealHistory", groupBuyService.getDealHistory());
        if (!model.containsAttribute("groupBuy")) model.addAttribute("groupBuy", new GroupBuy());
    }
}
