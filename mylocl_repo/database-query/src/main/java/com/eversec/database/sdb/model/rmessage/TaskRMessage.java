
package com.eversec.database.sdb.model.rmessage;

import java.util.LinkedHashMap;
import java.util.Map;

import net.sf.json.JSONObject;

public class TaskRMessage extends RMessage {
    public Map<String, String> datas = new LinkedHashMap<String, String>();

    @Override
    public String toJson() {
        String res = String.format(
                "{\"cmd\":\"%s\",\"code\":%d,\"total\":%d,"
                        + "\"datas\":[%s],\"exceptions\":\"%s\"}",
                cmd, code, 1, JSONObject.fromObject(datas).toString(), exception);
        return res;
    }

}
