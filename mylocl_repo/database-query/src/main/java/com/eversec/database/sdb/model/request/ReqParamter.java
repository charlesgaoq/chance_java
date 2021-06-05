
package com.eversec.database.sdb.model.request;

public abstract class ReqParamter {
    protected String cmd;
    protected String command;

    public abstract String validate();
}
