
package com.eversec.database.sdb.service;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.eversec.database.sdb.dao.base.executer.Executer;
import com.eversec.database.sdb.dao.els.CountExecuter;
import com.eversec.database.sdb.dao.els.InsertExecuter;
import com.eversec.database.sdb.dao.els.QueryExecuter;
import com.eversec.database.sdb.dao.els.RemoveExecuter;
import com.eversec.database.sdb.dao.els.StateExecuter;
import com.eversec.database.sdb.dao.els.UpdateExecuter;
import com.eversec.database.sdb.dao.els.UrlExecuter;
import com.eversec.database.sdb.model.mdb.NoSqlCommand;
import com.eversec.database.sdb.model.request.ReqParamterElsManage;
import com.eversec.database.sdb.model.rmessage.ExceptionRMessage;
import com.eversec.database.sdb.model.rmessage.InsertRMessage;
import com.eversec.database.sdb.model.rmessage.RMessage;
import com.eversec.database.sdb.util.exceptions.BaseException;
import com.eversec.database.sdb.util.sql.ElsAnalysisSQL;
import com.eversec.database.synch.dao.IdbTaskResult;
//import com.eversec.database.synch.dao.SynchResultEsRepository;

@Service
public class ElsService {

    private static final Logger LOG = LoggerFactory.getLogger(ElsService.class);

//    @Resource
//    private SynchResultEsRepository synchResultEsRepository;

    public RMessage service(String sql) {
        RMessage rMessage = null;
        ElsAnalysisSQL analysisSQL = new ElsAnalysisSQL();
        analysisSQL.setSql(sql);
        Executer executer = null;
        try {
            analysisSQL.analysis();
            NoSqlCommand command = analysisSQL.getCommand();
            if ("query".equals(command.cmd)) {
                executer = new QueryExecuter();
                rMessage = executer.execute(command);
            } else if ("count".equals(command.cmd)) {
                executer = new CountExecuter();
                rMessage = executer.execute(command);
            } else if ("insert".equals(command.cmd)) {
                executer = new InsertExecuter();
                rMessage = executer.execute(command);
            } else if ("update".equals(command.cmd)) {
                executer = new UpdateExecuter();
                rMessage = executer.execute(command);
            } else if ("delete".equals(command.cmd)) {
                executer = new RemoveExecuter();
                rMessage = executer.execute(command);
            } else if ("max".equals(command.cmd) || "min".equals(command.cmd)
                    || "sum".equals(command.cmd) || "avg".equals(command.cmd)) {
                executer = new StateExecuter();
                rMessage = executer.execute(command);
            }
        } catch (Exception e) {
            LOG.error("els查询异常", e);
            rMessage = new ExceptionRMessage();
            if (e instanceof BaseException) {
                BaseException be = (BaseException) e;
                rMessage.code = be.getCode();
                rMessage.exception = be.getMessage();
            } else {
                rMessage.code = -1;
                rMessage.exception = e.getMessage();
            }
        }
        return rMessage;
    }

    public RMessage service(ReqParamterElsManage req) {
        RMessage rMessage = null;
        UrlExecuter executer = null;
        try {
            executer = new UrlExecuter();
            rMessage = executer.execute(req);
        } catch (Exception e) {
            LOG.error("els查询异常", e);
            rMessage = new ExceptionRMessage();
            if (e instanceof BaseException) {
                BaseException be = (BaseException) e;
                rMessage.code = be.getCode();
                rMessage.exception = be.getMessage();
            } else {
                rMessage.code = -1;
                rMessage.exception = e.getMessage();
            }
        }
        return rMessage;
    }

    public RMessage batchSaveIdbResult(List<IdbTaskResult> list) {
        String rcs = "idbtaskresult_all";
        String rcl = "idbtaskresult";
        List<Document> datas = new ArrayList<Document>();
        InsertExecuter sdi = new InsertExecuter();
        for (IdbTaskResult resulttmp : list) {
            Document map = new Document();
            map.put("sort_id", resulttmp.getSort_id());
            map.put("result", resulttmp.getResult());
            map.put("taskid", resulttmp.getTaskid());
            map.put("taskdate", resulttmp.getTaskdate());
            datas.add(map);
        }
        InsertRMessage rmessage = new InsertRMessage();
        rmessage.cmd = "batch insert IdbTaskResult";
        rmessage.datas = list.size();
        try {
            sdi.bulkInsert(datas, rcs, rcl);
        } catch (Exception e) {
            LOG.error("els查询异常", e);
            rmessage.code = -9000;
        }
        return rmessage;

        // InsertRMessage rmessage = new InsertRMessage();
        // rmessage.cmd = "batch insert IdbTaskResult";
        // rmessage.datas = list.size();
        // Iterable<IdbTaskResult> result = null;
        // try {
        // result = synchResultEsRepository.save(list);
        // }catch(Exception ex) {
        // ex.printStackTrace();
        // }
        // logger.debug("insert el result size is
        // :"+IteratorUtils.toList(result.iterator()).size());
        // logger.debug("list.get(1) id is : "+list.get(0).getId());
        // if (IteratorUtils.toList(result.iterator()).size() < 0) {
        // rmessage.code = -9000;
        // }
        // return rmessage;
    }

}
