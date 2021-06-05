
package com.eversec.database.sdb.controller;

import java.util.List;
import java.util.UUID;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.alibaba.fastjson.JSONObject;
import com.eversec.database.sdb.model.request.ReqParamterElsManage;
import com.eversec.database.sdb.model.rmessage.InsertRMessage;
import com.eversec.database.sdb.model.rmessage.RMessage;
import com.eversec.database.sdb.service.ElsService;
import com.eversec.database.synch.dao.IdbTaskResult;

@Controller
@RequestMapping(value = "database-web")
public class ElsAction {

    private static Logger logger = Logger.getLogger("els");

    @Resource
    private ElsService elsService;

    @RequestMapping(value = "/_eversec/_els", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String elsSql(String command) {
        String uuid = UUID.randomUUID().toString();
        logger.info(uuid + ":" + command);
        RMessage rMessage = elsService.service(command);
        logger.info(uuid + ":" + rMessage.code);
        return rMessage.toJson();
    }

    @RequestMapping(value = "/_manage", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String elsUrl(ReqParamterElsManage req) {
        RMessage rMessage = elsService.service(req);
        return rMessage.toJson();
    }

    @RequestMapping(value = "/_eversec/_batchels", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String elsBatch(String datajson) {
        RMessage rMessage = null;
        List<IdbTaskResult> list = JSONObject.parseArray(datajson, IdbTaskResult.class);
        if (list == null || list.isEmpty() || list.size() <= 0) {
            rMessage = new InsertRMessage();
            rMessage.code = -9000;
            return rMessage.toJson();
        }
        logger.info("list size is : " + list.size());
        rMessage = elsService.batchSaveIdbResult(list);
        return rMessage.toJson();
    }

}
