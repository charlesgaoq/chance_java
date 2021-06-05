
package com.eversec.database.sdb.bo;

import java.sql.Connection;
import java.util.Date;

import org.apache.commons.lang3.time.DateFormatUtils;

/**
 * impala查询任务及Connnetion关系.
 * 
 * @author Ken.Zheng
 * @since 2018年12月17日
 */
public class IdbConnectionBo {

    private Date executeTime;

    private Connection hiveCon;

    public IdbConnectionBo() {
        super();
    }

    public IdbConnectionBo(Connection hiveCon) {
        super();
        this.executeTime = new Date();
        this.hiveCon = hiveCon;
    }

    public Date getExecuteTime() {
        return executeTime;
    }

    public void setExecuteTime(Date executeTime) {
        this.executeTime = executeTime;
    }

    public Connection getHiveCon() {
        return hiveCon;
    }

    public void setHiveCon(Connection hiveCon) {
        this.hiveCon = hiveCon;
    }

    @Override
    public String toString() {
        return "IdbConnection [executeTime=" + DateFormatUtils.format(executeTime, "yyyyMMddHHmmss")
                + ", hiveCon=" + hiveCon + "]";
    }

}
