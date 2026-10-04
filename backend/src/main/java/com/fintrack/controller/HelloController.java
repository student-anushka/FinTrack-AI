package com.fintrack.controller;

import com.fintrack.service.HelloService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class HelloController {

    private final HelloService helloService;

    public HelloController(HelloService helloService) {
        this.helloService = helloService;
    }

    @GetMapping("/hello/{name}")
    public String sayHello(@PathVariable String name) {

        return helloService.sayHello(name);

    }
}