package com.codeloon.ems.controller;

import com.codeloon.ems.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/public")
public class HomeController {

    @Autowired
    private UserRepository userRepository;

    @RequestMapping("/")
    public String getHome(){
        return "Hello World";
    }

/*
    Navigation Data
    ----Event List
        ---- Dynamic event types (Wedding, Birthday )
    ----Package Types
        ---- Budget, Standard, Premium
             ---- Package List
                  ---- Package Details
    ----About Data
        ---- Description
 */
}
