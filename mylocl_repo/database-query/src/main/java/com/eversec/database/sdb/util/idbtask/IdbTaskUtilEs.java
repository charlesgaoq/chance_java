
package com.eversec.database.sdb.util.idbtask;

import java.util.List;
import java.util.Map;

import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.eversec.database.sdb.constant.Constant;
import com.eversec.database.sdb.dao.els.CountExecuter;
import com.eversec.database.sdb.dao.els.QueryExecuter;
import com.eversec.database.sdb.dao.els.UpdateExecuter;
import com.eversec.database.sdb.dao.impala.IdbDaoTaskQuery;
import com.eversec.database.sdb.dao.els.InsertExecuter;
import com.mongodb.BasicDBObject;

public class IdbTaskUtilEs {

    private static final Logger LOG = LoggerFactory.getLogger(IdbTaskUtilEs.class);

    public void saveTaskInfo(String cs, String cl, Map<String, Object> map) throws Exception {
        InsertExecuter sdbDao = new InsertExecuter();
        Document data = new Document(map);
        sdbDao.execute(cs, cl, data);
    }

    public List<Map<String, Object>> getTaskInfo(String cs, String cl) throws Exception {
        List<Map<String, Object>> list = null;
        // 获取并行执行任务数量
        long idbTaskCycleNum = Long.parseLong(Constant.getConfigItemCur("idbTaskCycleNum", "5"));

        CountExecuter sdcc = new CountExecuter();
        LOG.debug("开始count查询");
        long size = sdcc.execute(cs, cl, "state", 1);
        LOG.debug("count结果：{}",size);
        if (size > -1L && idbTaskCycleNum > size) {
            int limit = (int) (idbTaskCycleNum - size);
            QueryExecuter sdcq = new QueryExecuter();
            BasicDBObject filter = BasicDBObject.parse("{\"state\":0}");
            BasicDBObject sort = BasicDBObject.parse("{\"level\":-1}");
            list = sdcq.execute(cs, cl, filter, null, sort, limit, 0);
            // System.out.println(">>>>>>>>>>>>>>>list.size="+list.size());
        } else {
            if (idbTaskCycleNum == 0 && size == 0) {
                LOG.warn("task is none!!!");
            }
        }
        return list;
    }

    public void setTaskState(String cs, String cl, int state, String id) throws Exception {
        UpdateExecuter sdcu = new UpdateExecuter();
        Document modifier = new Document();
        modifier.put("state", state);
        sdcu.execute(cs, cl, modifier, id);
    }

    public void setTaskState(String cs, String cl, int state, String id, long tol)
            throws Exception {
        UpdateExecuter sdcu = new UpdateExecuter();
        Document modifier = new Document();
        modifier.put("state", state);
        modifier.put("usetime", tol);
        sdcu.execute(cs, cl, modifier, id);
    }

    public void setTaskState(String cs, String cl, int state, String id, long tol, long count)
            throws Exception {
        // System.out.println("cccccccc"+count);
        UpdateExecuter sdcu = new UpdateExecuter();
        Document modifier = new Document();
        modifier.put("state", state);
        modifier.put("usetime", tol);
        modifier.put("count", count);
        sdcu.execute(cs, cl, modifier, id);
    }

    public Long queryTask(String cs, String cl, String sql, String taskid, String taskdate)
            throws Exception {
        IdbDaoTaskQuery stqd = new IdbDaoTaskQuery();
        stqd.rcs = cs;
        stqd.rcl = cl;
        stqd.taskid = taskid;
        stqd.taskdate = taskdate;
        Long total = (Long) stqd.execute(taskid, sql);
        return total;
    }

    public void setExceptionMess(int code, String message, String id, String taskid,
            String taskdate, String cs, String cl) throws Exception {
        // InsertExecuter sdbDao = new InsertExecuter();
        // RemoveExecuter sdcr = new RemoveExecuter();
        UpdateExecuter sdcu = new UpdateExecuter();
        Document data = new Document();
        // data.put("_id", _id);
        // sdcr.execute(cs, cl, data);
        data.put("exception", message);
        // data.put("taskid", taskid);
        // data.put("taskdate", taskdate);
        // data.remove("_id");
        sdcu.execute(cs, cl, data, id);
    }
}
