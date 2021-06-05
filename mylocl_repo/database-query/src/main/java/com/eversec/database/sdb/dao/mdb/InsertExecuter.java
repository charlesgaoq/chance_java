
package com.eversec.database.sdb.dao.mdb;

import org.bson.Document;

import com.eversec.database.sdb.dao.base.sdb.ConnectionDB;
import com.eversec.database.sdb.model.mdb.NoSqlCommand;
import com.eversec.database.sdb.model.rmessage.InsertRMessage;
import com.eversec.database.sdb.model.rmessage.RMessage;
import com.mongodb.client.MongoCollection;

public class InsertExecuter extends BaseExecuter {

    @Override
    public RMessage execute(NoSqlCommand command) throws Exception {
        conn = new ConnectionDB();
        vaildate(command);
        InsertRMessage rMessage = new InsertRMessage();
        rMessage.cmd = command.cmd;
        MongoCollection<Document> db = conn.getMongoCollection(command.cs, command.cl);
        rMessage.datas = 0L;
        if (!command.getValues().isEmpty()) {
            rMessage.datas = command.getValues().size();
            db.insertMany(command.getValues());
        }
        return rMessage;
    }

    public String execute(String cs, String cl, Document data) throws Exception {
        conn = new ConnectionDB();
        NoSqlCommand command = new NoSqlCommand();
        command.cs = cs;
        command.cl = cl;
        vaildate(command);
        String id = null;
        MongoCollection<Document> db = conn.getMongoCollection(command.cs, command.cl);
        db.insertOne(data);
        id = data.getObjectId("id").toString();
        return id;
    }
}
