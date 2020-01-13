package com.youthlin.example.boot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author : youthlin.chen @ 2019-09-18 22:08
 */
@SpringBootApplication
@Controller
public class App {
    public static void main(String[] args) {
        SpringApplication.run(App.class);
    }

    @ResponseBody
    @RequestMapping("/sayHello")
    public String sayHello(String name) {
        return "Hello, " + (StringUtils.isEmpty(name) ? "World" : name);
    }

}
