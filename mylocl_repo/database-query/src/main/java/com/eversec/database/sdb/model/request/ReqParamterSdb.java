
package com.eversec.database.sdb.model.request;

import com.eversec.database.sdb.constant.Constant;

/**
 * sdb 命令接口 request 请求参数模型类
 *
 * @author Jony
 */
public class ReqParamterSdb extends ReqParamter {
    private String cmd;
    private String cs;
    private String cl;
    private String command;

    public ReqParamterSdb() {
    }

    public String getCmd() {
        return cmd;
    }

    public void setCmd(String cmd) {
        this.cmd = cmd;
    }

    public String getCs() {
        return cs;
    }

    public void setCs(String cs) {
        this.cs = cs;
    }

    public String getCl() {
        return cl;
    }

    public void setCl(String cl) {
        this.cl = cl;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    /**
     * 参数情况校验
     *
     * @return 空：校验通过
     */
    public String validate() {
        String mes = "";
        if (getCs() == null || "".equals(getCs())) {
            mes = "无集合空间名称参数cs";
        } else if (getCl() == null || "".equals(getCl())) {
            mes = "无集合名称参数cl";
        } else if (getCmd() == null || "".equals(getCmd())) {
            mes = "无操作类型参数cmd";
        } else if (!Constant.isCmdItem(getCmd())) {
            mes = "cmd 的值为非法参数值";
        }
        return mes;
    }
}
