package com.bonheur.portfolio.controllers;

import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/welcome")
@RequiredArgsConstructor
public class HelloController {

    @GetMapping("/")
    public String sayHello() {
        return "Hello, World!";
    }

    @GetMapping("/hello")
    public String getMethodName(@RequestParam String param) {
        return "Hello " + param + "!";
    }

}
