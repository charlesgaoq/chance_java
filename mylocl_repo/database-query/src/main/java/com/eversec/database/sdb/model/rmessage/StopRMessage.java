
package com.eversec.database.sdb.model.rmessage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;

/**
 * 停止message
 * 
 * @author Ken.Zheng
 * @since 2018年12月17日
 */
public class StopRMessage extends RMessage {
    public List<Map<String, Object>> datas = new ArrayList<Map<String, Object>>();

    public StopRMessage() {
    }

    @Override
    public String toJson() {
        String res = String.format(
                "{\"cmd\":\"%s\",\"code\":%d,\"total\":%d,\"datas\":%s,\"exceptions\":\"%s\"}", cmd,
                code, datas.size(), JSONArray.fromObject(datas), exception);
        return res;
    }
}
