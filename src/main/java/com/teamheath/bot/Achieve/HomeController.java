package com.teamheath.bot.Achieve;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class HomeController {
    @Value("${spring.application.name}")
    private String appName;


    @RequestMapping("/")
    public String index1(){

        System.out.println(appName);
        return getViewName();
 //
    }

    public String getViewName(){
        return "index.html";
    }


}
