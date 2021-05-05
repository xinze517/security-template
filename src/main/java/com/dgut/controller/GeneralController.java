package com.dgut.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GeneralController {

    @PostMapping("/csrf")
    public void csrf() {
        //用于获取带有 csrf 值的 cookie
    }

}
