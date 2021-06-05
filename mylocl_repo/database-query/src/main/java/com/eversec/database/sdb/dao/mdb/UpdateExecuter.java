
package com.eversec.database.sdb.dao.mdb;

import org.bson.Document;
import org.bson.conversions.Bson;
import com.eversec.database.sdb.dao.base.sdb.ConnectionDB;
import com.eversec.database.sdb.model.mdb.NoSqlCommand;
import com.eversec.database.sdb.model.rmessage.RMessage;
import com.eversec.database.sdb.model.rmessage.UpdateRMessage;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.result.UpdateResult;

public class UpdateExecuter extends BaseExecuter {
    @Override
    public RMessage execute(NoSqlCommand command) throws Exception {
        conn = new ConnectionDB();
        vaildate(command);
        UpdateRMessage rMessage = new UpdateRMessage();
        rMessage.cmd = command.cmd;
        MongoCollection<Document> db = conn.getMongoCollection(command.cs, command.cl);
        UpdateOptions uOptions = new UpdateOptions();
        uOptions.upsert(true);
        UpdateResult uResult = db.updateMany(command.filter, command.set, uOptions);
        long num = uResult.getModifiedCount();
        rMessage.datas = num;
        return rMessage;
    }

    public long execute(String cs, String cl, Bson filter, Bson set) throws Exception {
        conn = new ConnectionDB();
        NoSqlCommand command = new NoSqlCommand();
        command.cs = cs;
        command.cl = cl;
        vaildate(command);
        MongoCollection<Document> db = conn.getMongoCollection(command.cs, command.cl);
        UpdateOptions uOptions = new UpdateOptions();
        uOptions.upsert(true);
        UpdateResult uResult = db.updateMany(filter, set, uOptions);
        long num = uResult.getModifiedCount();
        return num;
    }
}
