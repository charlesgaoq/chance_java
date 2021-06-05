
package com.eversec.database.sdb.dao.els;

//import java.util.LinkedHashMap;
//import java.util.Map;
//
//import net.sf.json.JSONArray;

import net.sf.json.JSONObject;

import com.eversec.database.sdb.model.mdb.NoSqlCommand;
import com.eversec.database.sdb.model.request.ReqParamterElsManage;
import com.eversec.database.sdb.model.rmessage.QueryRMessage;
import com.eversec.database.sdb.model.rmessage.RMessage;

public class UrlExecuter extends BaseExecuter {

    @Override
    public RMessage execute(NoSqlCommand command) throws Exception {
        return null;
    }

    public RMessage execute(ReqParamterElsManage req) throws Exception {
        QueryRMessage rMessage = new QueryRMessage();
        rMessage.cmd = "elsManage";
        setBodyData(req.getData());
        setURI(req.getUrl());
        String reJson = sendHttpCommand(createHttpMethod(req.getType()));
        if (reJson != null && !"".equals(reJson)) {
            JSONObject json = JSONObject.fromObject(reJson);
            if (json.containsKey("error")) {
                rMessage.exception = json.getString("error");
                rMessage.code = code - 500;
            }
        }
        return rMessage;
    }
}
