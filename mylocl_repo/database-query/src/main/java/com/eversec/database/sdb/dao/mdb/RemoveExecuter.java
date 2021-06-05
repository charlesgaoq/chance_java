
package com.eversec.database.sdb.dao.mdb;

import org.bson.Document;
import org.bson.conversions.Bson;

import com.eversec.database.sdb.dao.base.sdb.ConnectionDB;
import com.eversec.database.sdb.model.mdb.NoSqlCommand;
import com.eversec.database.sdb.model.rmessage.RMessage;
import com.eversec.database.sdb.model.rmessage.UpdateRMessage;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.result.DeleteResult;

public class RemoveExecuter extends BaseExecuter {

    @Override
    public RMessage execute(NoSqlCommand command) throws Exception {
        conn = new ConnectionDB();
        vaildate(command);
        UpdateRMessage rMessage = new UpdateRMessage();
        rMessage.cmd = command.cmd;
        MongoCollection<Document> db = conn.getMongoCollection(command.cs, command.cl);
        DeleteResult dResult = db.deleteMany(command.filter);
        rMessage.datas = dResult.getDeletedCount();
        return rMessage;
    }

    public long execute(String cs, String cl, Bson filter) throws Exception {
        conn = new ConnectionDB();
        NoSqlCommand command = new NoSqlCommand();
        command.cs = cs;
        command.cl = cl;
        vaildate(command);
        MongoCollection<Document> db = conn.getMongoCollection(command.cs, command.cl);
        DeleteResult dResult = db.deleteMany(filter);
        return dResult.getDeletedCount();
    }
}
