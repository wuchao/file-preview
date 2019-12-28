package com.github.wuchao.filepreview.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ErrorController {

    /**
     * 403 页面
     */
    @GetMapping("/403")
    public String error403() {
        return "403";
    }

    /**
     * 404 页面
     */
    @GetMapping("/404")
    public String error404() {
        return "404";
    }

    /**
     * 500 页面
     */
    @GetMapping("/500")
    public String error500() {
        return "500";
    }

}
