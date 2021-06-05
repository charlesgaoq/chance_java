
package com.eversec.database.sdb.model.rmessage;

public class ExceptionRMessage extends RMessage {

    @Override
    public String toJson() {
        String res = String.format(
                "{\"cmd\":\"%s\",\"code\":%d,\"total\":%d,\"datas\":[],\"exceptions\":\"%s\"}", cmd,
                code, 1, exception);
        return res;
    }

}
