
package com.eversec.database.sdb.dao.mdb;

import org.bson.Document;

import com.eversec.database.sdb.dao.base.sdb.ConnectionDB;
import com.eversec.database.sdb.model.mdb.NoSqlCommand;
import com.eversec.database.sdb.model.rmessage.CountRMessage;
import com.eversec.database.sdb.model.rmessage.RMessage;
import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;

public class CountExecuter extends BaseExecuter {

    @Override
    public RMessage execute(NoSqlCommand command) throws Exception {
        conn = new ConnectionDB();
        vaildate(command);
        CountRMessage rMessage = new CountRMessage();
        rMessage.cmd = command.cmd;
        MongoCollection<Document> db = conn.getMongoCollection(command.cs, command.cl);
        long count = db.count(command.filter);
        rMessage.datas = count;
        return rMessage;
    }

    public long execute(String cs, String cl, BasicDBObject filter) throws Exception {
        conn = new ConnectionDB();
        NoSqlCommand command = new NoSqlCommand();
        command.cs = cs;
        command.cl = cl;
        vaildate(command);
        MongoCollection<Document> db = conn.getMongoCollection(command.cs, command.cl);
        long count = db.count(filter);
        return count;
    }
}
