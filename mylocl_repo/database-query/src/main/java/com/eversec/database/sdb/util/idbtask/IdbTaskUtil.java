
package com.eversec.database.sdb.util.idbtask;

import java.util.List;
import java.util.Map;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.eversec.database.sdb.constant.Constant;
import com.eversec.database.sdb.dao.impala.IdbDaoTaskQuery;
import com.eversec.database.sdb.dao.mdb.CountExecuter;
import com.eversec.database.sdb.dao.mdb.InsertExecuter;
import com.eversec.database.sdb.dao.mdb.QueryExecuter;
import com.eversec.database.sdb.dao.mdb.RemoveExecuter;
import com.eversec.database.sdb.dao.mdb.UpdateExecuter;
import com.mongodb.BasicDBObject;
import com.mongodb.util.JSON;

public class IdbTaskUtil {

    private static final Logger LOG = LoggerFactory.getLogger(IdbTaskThreadUtilEs.class);

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
        BasicDBObject filter = BasicDBObject.parse("{state:1}");
        long size = sdcc.execute(cs, cl, filter);
        if (size > -1L && idbTaskCycleNum > size) {
            int limit = (int) (idbTaskCycleNum - size);
            QueryExecuter sdcq = new QueryExecuter();
            BasicDBObject matcher = (BasicDBObject) JSON.parse("{state:0}");
            BasicDBObject sort = (BasicDBObject) JSON.parse("{level:-1}");
            list = sdcq.execute(cs, cl, matcher, null, sort, limit, 0);
        } else {
            if (idbTaskCycleNum == 0 && size == 0) {
                LOG.warn("task is none!!!");
            }
        }
        return list;
    }

    public void setTaskState(String cs, String cl, int state, String id) throws Exception {
        UpdateExecuter sdcu = new UpdateExecuter();
        BasicDBObject modifier = BasicDBObject.parse("{$set:{state:" + state + "}}");
        BasicDBObject matcher = new BasicDBObject();
        matcher.put("_id", new ObjectId(id));
        sdcu.execute(cs, cl, matcher, modifier);
    }

    public void setTaskState(String cs, String cl, int state, String id, long tol)
            throws Exception {
        UpdateExecuter sdcu = new UpdateExecuter();
        BasicDBObject modifier = BasicDBObject
                .parse("{$set:{state:" + state + ",usetime:" + tol + "}}");
        BasicDBObject matcher = new BasicDBObject();
        matcher.put("_id", new ObjectId(id));
        sdcu.execute(cs, cl, matcher, modifier);
    }

    public void queryTask(String cs, String cl, String sql, String taskid, String taskdate)
            throws Exception {
        IdbDaoTaskQuery stqd = new IdbDaoTaskQuery();
        stqd.rcs = cs;
        stqd.rcl = cl;
        stqd.taskid = taskid;
        stqd.taskdate = taskdate;
        stqd.execute(taskid, sql);
    }

    public void setExceptionMess(int code, String message, String taskid, String cs, String cl)
            throws Exception {
        InsertExecuter sdbDao = new InsertExecuter();
        RemoveExecuter sdcr = new RemoveExecuter();
        Document data = new Document();
        data.put("taskid", taskid);
        sdcr.execute(cs, cl, data);
        data.put("message", message);
        sdbDao.execute(cs, cl, data);
    }
}
