
package com.eversec.database.sdb.dao.mdb;

import java.util.HashMap;
import java.util.Map;

import com.eversec.database.sdb.dao.base.executer.Executer;
import com.eversec.database.sdb.dao.base.sdb.ConnectionDB;
import com.eversec.database.sdb.model.mdb.NoSqlCommand;
import com.eversec.database.sdb.model.rmessage.RMessage;
import com.eversec.database.sdb.util.exceptions.BaseException;
import com.mongodb.Block;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoIterable;

public abstract class BaseExecuter implements Executer {
    protected final int code = -80011;
    protected ConnectionDB conn = null;
    protected BaseException execption;

    // @Override
    public abstract RMessage execute(NoSqlCommand command) throws Exception;

    // @Override
    public void vaildate(NoSqlCommand command) throws Exception {
        MongoDatabase mdb = conn.getCollectionSpace(command.cs);
        MongoIterable<String> mi = mdb.listCollectionNames();
        final Map<String, Integer> cols = new HashMap<String, Integer>();
        mi.forEach(new Block<String>() {
            // @Override
            public void apply(String t) {
                cols.put(t, 1);
            }
        });
        if (cols.isEmpty()) {
            execption = new BaseException(code, "数据库不存在！");
            throw execption;
        } else if (!cols.containsKey(command.cl)) {
            execption = new BaseException(code, "表不存在！");
            throw execption;
        }
    }

}
