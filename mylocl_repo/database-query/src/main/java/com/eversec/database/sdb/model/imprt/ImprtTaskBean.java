
package com.eversec.database.sdb.model.imprt;

import com.eversec.database.sdb.constant.Constant;

/**
 * 导入任务信息模型
 *
 * @author Jony
 */
public class ImprtTaskBean {
    public String cmd;
    public String _id;
    // public String name;
    public String cs; // 目标集合空间
    public String cl; // 目标集合
    public String pfile; // 文件名
    public String dir; // 文件所在路径
    // public String regex; //文件规则（正则表达式）
    // public String cycle; //扫描间隔时间（cron表达式）
    public String state; // 0：队列中；1：执行中；2：执行完毕
    public String message; // 反馈信息
    public long tasktime; // 任务时间ms

    public String taskCS; // 任务信息集合空间
    public String taskCL; // 导入任务信息集合

    public ImprtTaskBean() {
        _id = "";
        // name = "";
        cs = "";
        cl = "";
        pfile = "";
        dir = Constant.getConfigItem("imprtPath");
        // regex = "";
        // cycle = "";
        state = "0";
        message = "";
        tasktime = 0L;
    }

    public String getPath() {
        return dir + "" + pfile;
    }

    public String getMatcher() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("{");
        buffer.append("\"_id\":{\"$oid\":\"").append(_id.toString()).append("\"}");
        buffer.append("}");
        return buffer.toString();
    }

    public String getModifier() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("{");
        buffer.append("$set:{");
        buffer.append("\"message\":\"").append(message).append("\",");
        buffer.append("\"tasktime\":\"").append(tasktime).append("\",");
        buffer.append("\"state\":\"").append(state).append("\"");
        buffer.append("}");
        buffer.append("}");
        return buffer.toString();
    }

    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("{");
        // buffer.append("_id:\"").append(_id.toString()).append("\",");
        buffer.append("cs:\"").append(cs).append("\",");
        buffer.append("cl:\"").append(cl).append("\",");
        buffer.append("dir:\"").append(dir).append("\",");
        buffer.append("pfile:\"").append(pfile).append("\",");
        buffer.append("state:\"").append(state).append("\",");
        buffer.append("tasktime:\"").append(tasktime).append("\",");
        buffer.append("message:\"").append(message).append("\"");
        buffer.append("}");
        return buffer.toString();
    }

    public String validate() {
        String mes = "";
        if (cs == null || "".equals(cs)) {
            mes = "未发现导入数据的目标集合空间！";
        } else if (cl == null || "".equals(cl)) {
            mes = "未发现导入数据的目标集合！";
        } else if (pfile == null || "".equals(pfile)) {
            mes = "未发现导入数据的文件名！";
        }
        return mes;
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

    public String getPfile() {
        return pfile;
    }

    public void setPfile(String pfile) {
        this.pfile = pfile;
    }

    public String getCmd() {
        return cmd;
    }

    public void setCmd(String cmd) {
        this.cmd = cmd;
    }
}
