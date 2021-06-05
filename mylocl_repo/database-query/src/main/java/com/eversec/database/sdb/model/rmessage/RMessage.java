
package com.eversec.database.sdb.model.rmessage;

public abstract class RMessage {
    public String cmd = "";
    public int code = 0;
    public int total = 0;
    public String exception = "";

    public abstract String toJson();

}
