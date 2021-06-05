
package com.eversec.database.sdb.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.eversec.database.sdb.dao.base.executer.Executer;
import com.eversec.database.sdb.dao.mdb.CountExecuter;
import com.eversec.database.sdb.dao.mdb.InsertExecuter;
import com.eversec.database.sdb.dao.mdb.QueryExecuter;
import com.eversec.database.sdb.dao.mdb.RemoveExecuter;
import com.eversec.database.sdb.dao.mdb.UpdateExecuter;
import com.eversec.database.sdb.model.mdb.NoSqlCommand;
import com.eversec.database.sdb.model.rmessage.ExceptionRMessage;
import com.eversec.database.sdb.model.rmessage.RMessage;
import com.eversec.database.sdb.util.exceptions.BaseException;
import com.eversec.database.sdb.util.sql.AnalysisSQL;

@Service
public class MdbService {

    private static final Logger LOG = LoggerFactory.getLogger(MdbService.class);

    public RMessage service(String sql) {
        RMessage rMessage = null;
        AnalysisSQL analysisSQL = new AnalysisSQL();
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
            }
        } catch (Exception e) {
            LOG.error("异常", e);
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
}
