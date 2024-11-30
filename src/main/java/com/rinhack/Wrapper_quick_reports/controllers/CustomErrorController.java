package com.rinhack.Wrapper_quick_reports.controllers;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class CustomErrorController implements ErrorController {

    @RequestMapping("/error")
    public String handleError() {
        // Provide a custom error page or response
        return "error page";
    }


    public String getErrorPath() {
        return "/error";
    }
}
