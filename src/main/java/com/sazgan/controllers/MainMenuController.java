package com.sazgan.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
/**
 * Manage transferring to main page.
 * @author Maziar
 */
@Controller
public class MainMenuController {
    /**
     * Transfer us to main page of the App.
     * @return String
     */
    @RequestMapping("/")
    final public String showPage() {
        return "main-menu";
    }
}
