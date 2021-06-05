
package com.eversec.database.sdb.util.idbtask;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.eversec.database.sdb.util.idbtask.IdbTaskUtilEs;

public class IdbTaskThreadUtilEs implements Runnable {
    private static final Logger LOG = LoggerFactory.getLogger(IdbTaskThreadUtilEs.class);
    private List<Map<String, Object>> list;
    private String cs;
    private String cl;
    private int errorCode = -21000;

    public IdbTaskThreadUtilEs(List<Map<String, Object>> list, String cs, String cl) {
        this.list = list;
        this.cs = cs;
        this.cl = cl;
    }

    @Override
    public void run() {
        try {
            LOG.debug("启动线程查询impala并写入ES开始");
            IdbTaskUtilEs util = new IdbTaskUtilEs();
            for (Map<String, Object> map : list) {
                String id = map.get("_id").toString();
                String resultcs = map.get("resultcs").toString();
                String resultcl = map.get("resultcl").toString();
                String command = map.get("command").toString();
                String taskid = map.get("taskid").toString();
                String taskdate = map.get("taskdate").toString();
                // util.setTaskState(cs, cl, 1, _id);
                try {
                    long st = new Date().getTime();
                    // 查询及保存记录
                    Long total = util.queryTask(resultcs, resultcl, command, taskid, taskdate);
                    long et = new Date().getTime();
                    long tol = et - st;
                    int state = 2;
                    if (total.equals(0L) || total == 0L) {
                        state = 4;
                    }
                    // System.out.print("xxxxxxxxxx"+total.longValue());
                    util.setTaskState(cs, cl, state, id, tol, total.longValue());
                    LOG.debug("{}impala查询结果写入ELS中结束，并更新,用时:{}", taskid, tol);
                } catch (Exception e) {
                    util.setTaskState(cs, cl, 3, id, -1);
                    util.setExceptionMess(errorCode - 3, e.getMessage(), id, taskid, taskdate, cs,
                            cl);
                    LOG.error("查询impala并写入ES异常", e);
                }
            }
        } catch (Exception e) {
            LOG.error("查询impala并写入ES异常", e);
        }
    }

}
