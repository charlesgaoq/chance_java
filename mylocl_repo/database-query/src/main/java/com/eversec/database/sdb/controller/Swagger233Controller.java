
package com.eversec.database.sdb.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class Swagger233Controller {

    @RequestMapping("/swagger")
    public String index() {
        return "/swagger";
    }
}
