
package com.eversec.database.synch.dao;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import java.io.Serializable;

/**
 * @Classname IdbTaskResult
 * @Description
 * @Date 2018/9/19 18:17
 * @Created by hao.chen
 */
@Document(indexName = "idbtaskresult_all", type = "idbtaskresult")
public class IdbTaskResult implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -3331654196011584230L;
    @Id
    private String id;
    private String taskid;
    private String sort_id;
    private String result;
    private String taskdate;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSort_id() {
        return sort_id;
    }

    public void setSort_id(String sort_id) {
        this.sort_id = sort_id;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getTaskid() {
        return taskid;
    }

    public void setTaskid(String taskid) {
        this.taskid = taskid;
    }

    public String getTaskdate() {
        return taskdate;
    }

    public void setTaskdate(String taskdate) {
        this.taskdate = taskdate;
    }
}
