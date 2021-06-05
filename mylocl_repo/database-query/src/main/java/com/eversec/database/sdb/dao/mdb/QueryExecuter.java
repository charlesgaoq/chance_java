
package com.eversec.database.sdb.dao.mdb;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bson.Document;
import org.bson.conversions.Bson;

import com.eversec.database.sdb.dao.base.sdb.ConnectionDB;
import com.eversec.database.sdb.model.mdb.NoSqlCommand;
import com.eversec.database.sdb.model.rmessage.QueryRMessage;
import com.eversec.database.sdb.model.rmessage.RMessage;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;

public class QueryExecuter extends BaseExecuter {

    @Override
    public RMessage execute(NoSqlCommand command) throws Exception {
        // 流数据特殊处理
        // Pattern p = Pattern.compile("flow_data_\\d{8}");
        // Matcher m = p.matcher(command.cl);
        // if ( m.matches()) {
        // int tdate = Integer.parseInt(command.cl.substring(10));
        // if (tdate<=20160316) {
        // conn = new ConnectionDB();
        // command.cs = "flow_data";
        // }else{
        // conn = new ConnectionDB("f");
        // }
        // }else{
        // conn = new ConnectionDB();
        // }
        conn = new ConnectionDB();
        vaildate(command);
        QueryRMessage rMessage = new QueryRMessage();
        rMessage.cmd = command.cmd;
        MongoCollection<Document> db = conn.getMongoCollection(command.cs, command.cl);
        FindIterable<Document> iterable = db.find();
        iterable.filter(command.filter);
        iterable.projection(command.fileds);
        iterable.sort(command.sort);
        iterable.limit(command.limit);
        iterable.skip(command.skip);
        MongoCursor<Document> cursor = iterable.iterator();
        while (cursor.hasNext()) {
            Document doc = cursor.next();
            String id = doc.get("_id").toString();
            doc.put("_id", id);
            rMessage.datas.add(doc);
        }
        cursor.close();
        return rMessage;
    }

    public List<Map<String, Object>> execute(String cs, String cl, Bson filter, Bson fileds,
            Bson sort, int limit, int skip) throws Exception {
        conn = new ConnectionDB();
        NoSqlCommand command = new NoSqlCommand();
        command.cs = cs;
        command.cl = cl;
        vaildate(command);
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        MongoCollection<Document> db = conn.getMongoCollection(command.cs, command.cl);
        FindIterable<Document> iterable = db.find();
        iterable.filter(filter);// 状态
        iterable.projection(fileds);
        iterable.sort(sort);
        iterable.limit(limit);
        iterable.skip(skip);
        MongoCursor<Document> cursor = iterable.iterator();
        while (cursor.hasNext()) {
            Document doc = cursor.next();
            String id = doc.get("_id").toString();
            doc.put("_id", id);
            list.add(doc);
        }
        cursor.close();
        return list;
    }
}
