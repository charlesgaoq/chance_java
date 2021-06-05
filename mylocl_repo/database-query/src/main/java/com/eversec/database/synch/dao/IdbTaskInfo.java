
package com.eversec.database.synch.dao;

import org.springframework.data.annotation.Id;

import java.io.Serializable;

/**
 * @Date 2018/9/19 18:10
 * @Created by hao.chen
 */
// @Document(indexName = "idbtaskinfo_all",type = "idbtaskinfo")
public class IdbTaskInfo implements Serializable {

    private static final long serialVersionUID = -4036768175645456445L;

    @Id
    private String taskid;

    private String resultcs;
    private String level;
    private String taskdate;
    private String count;
    private String resultcl;
    private String state;
    private String command;
    private String usetime;

    public String getTaskid() {
        return taskid;
    }

    public void setTaskid(String taskid) {
        this.taskid = taskid;
    }

    public String getResultcs() {
        return resultcs;
    }

    public void setResultcs(String resultcs) {
        this.resultcs = resultcs;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getTaskdate() {
        return taskdate;
    }

    public void setTaskdate(String taskdate) {
        this.taskdate = taskdate;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public String getResultcl() {
        return resultcl;
    }

    public void setResultcl(String resultcl) {
        this.resultcl = resultcl;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public String getUsetime() {
        return usetime;
    }

    public void setUsetime(String usetime) {
        this.usetime = usetime;
    }
}
