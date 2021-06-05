
package com.eversec.database.sdb.controller;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.eversec.database.sdb.model.rmessage.ExceptionRMessage;
import com.eversec.database.sdb.model.rmessage.RMessage;
import com.eversec.database.sdb.service.MdbService;

@Controller
@RequestMapping(value = "database-web/_eversec/_mdb")
public class MdbAction {
    @Resource
    private MdbService mdbService;

    @RequestMapping(value = "", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String mdb(String command) {
        // RMessage rMessage = mdbService.service(command);
        RMessage rMessage = new ExceptionRMessage();
        rMessage.cmd = "mongodb";
        rMessage.code = 500;
        rMessage.exception = "mdb接口服务未开启";
        return rMessage.toJson();
    }

    @RequestMapping(value = "/cli")
    public String mdbCli() {
        return "cli/mdb";
    }
}
