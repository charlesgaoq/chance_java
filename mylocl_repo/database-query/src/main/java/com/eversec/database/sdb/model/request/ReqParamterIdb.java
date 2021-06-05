
package com.eversec.database.sdb.model.request;

import com.eversec.database.sdb.constant.Constant;

public class ReqParamterIdb extends ReqParamter {
    public String cmd; // query;
    public String command; // sql;
    public String taskid; // 任务id;
    public int level; // 任务执行优先级（值越高级别越高）

    public ReqParamterIdb() {
        cmd = "";
        command = "";
        taskid = "";
        level = 0;
    }

    /**
     * 参数情况校验
     *
     * @return 空：校验通过
     */
    @Override
    public String validate() {
        String mes = "";
        if (getCmd() == null || "".equals(getCmd())) {
            mes = "无操作类型参数cmd";
        } else if (!Constant.isCmdItem(getCmd())) {
            mes = "cmd 的值为非法参数值";
        } else if (getCommand() == null || "".equals(getCommand())) {
            mes = "无执行命令参数 command";
        }
        return mes;
    }

    public String getCmd() {
        return cmd;
    }

    public void setCmd(String cmd) {
        this.cmd = cmd;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public String getTaskid() {
        return taskid;
    }

    public void setTaskid(String taskid) {
        this.taskid = taskid;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    @Override
    public String toString() {
        return "parms [cmd=" + cmd + ", taskid=" + taskid + ", level=" + level + ", command="
                + command + "]";
    }

}
