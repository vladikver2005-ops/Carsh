package com.carsh.Carsh.controller;

import com.carsh.Carsh.model.entity.Damage;
import com.carsh.Carsh.model.service.DamageService;
import com.carsh.Carsh.model.service.PaymentService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;

@Controller
@RequestMapping("/damages")
public class DamageController {

    private final DamageService damageService;
    private final PaymentService paymentService;

    public DamageController(DamageService damageService, PaymentService paymentService) {
        this.damageService = damageService;
        this.paymentService = paymentService;
    }

    @GetMapping("/admin")
    public String adminDamages(Model model) {
        model.addAttribute("damages", damageService.getAllDamages());
        return "damages/admin-list";
    }

    @GetMapping("/new/{orderId}")
    public String newDamageForm(@PathVariable Long orderId, Model model) {
        model.addAttribute("orderId", orderId);
        return "damages/form";
    }

    @PostMapping("/create")
    public String createDamage(@RequestParam Long orderId,
                              @RequestParam String description,
                              @RequestParam BigDecimal repairCost,
                              RedirectAttributes redirectAttributes) {
        try {
            Damage damage = damageService.createDamage(orderId, description, repairCost);
            redirectAttributes.addFlashAttribute("success", "Повреждение зарегистрировано!");
            return "redirect:/damages/" + damage.getId();
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/damages/new/" + orderId;
        }
    }

    @GetMapping("/{id}")
    public String viewDamage(@PathVariable Long id, Model model) {
        Damage damage = damageService.getDamageById(id)
                .orElseThrow(() -> new RuntimeException("Повреждение не найдено"));
        model.addAttribute("damage", damage);
        return "damages/view";
    }

    @PostMapping("/pay/{id}")
    public String payDamage(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            damageService.payDamage(id);
            redirectAttributes.addFlashAttribute("success", "Ремонт оплачен!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/damages/admin";
    }
}
