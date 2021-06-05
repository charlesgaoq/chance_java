
package com.eversec.database.sdb.util.idbtask;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IdbTaskThreadUtil implements Runnable {
    private static final Logger LOG = LoggerFactory.getLogger(IdbTaskThreadUtil.class);
    private List<Map<String, Object>> list;
    private String cs;
    private String cl;
    private int errorCode = -21000;

    public IdbTaskThreadUtil(List<Map<String, Object>> list, String cs, String cl) {
        this.list = list;
        this.cs = cs;
        this.cl = cl;
    }

    // @Override
    public void run() {
        try {
            IdbTaskUtil util = new IdbTaskUtil();
            for (Map<String, Object> map : list) {
                String id = map.get("_id").toString();
                String resultcs = map.get("resultcs").toString();
                String resultcl = map.get("resultcl").toString();
                String command = map.get("command").toString();
                String taskid = map.get("taskid").toString();
                String taskdate = map.get("taskdate").toString();
                util.setTaskState(cs, cl, 1, id);
                try {
                    long st = new Date().getTime();
                    util.queryTask(resultcs, resultcl, command, taskid, taskdate); // 查询及保存记录
                    long et = new Date().getTime();
                    long tol = et - st;
                    util.setTaskState(cs, cl, 2, id, tol);
                } catch (Exception e) {
                    util.setTaskState(cs, cl, 3, id, -1);
                    util.setExceptionMess(errorCode - 3, e.getMessage(), taskid, resultcs,
                            resultcl);
                    LOG.error("异常", e);
                }
            }
        } catch (Exception e) {
            LOG.error("异常", e);
        }
    }

}
