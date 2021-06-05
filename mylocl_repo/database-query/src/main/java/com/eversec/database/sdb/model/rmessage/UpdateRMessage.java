
package com.eversec.database.sdb.model.rmessage;

public class UpdateRMessage extends RMessage {
    public long datas = 0L;

    @Override
    public String toJson() {
        String res = String.format(
                "{\"cmd\":\"%s\",\"code\":%d,"
                        + "\"total\":%d,\"datas\":[{\"num\":%d}],\"exceptions\":\"%s\"}",
                cmd, code, 1, datas, exception);
        return res;
    }
}
