package com.haihaycode.techvibesservice.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/public")
public class publicController {
    @GetMapping
    @PreAuthorize("permitAll()")
    public String publicApi(){
        System.out.println("publicApi");
        return "public - Api";
    }

}
