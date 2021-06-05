
package com.eversec.database.sdb.dao.base.sdb;

import org.bson.Document;
import org.springframework.stereotype.Component;
import com.mongodb.DBCollection;
import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

@Component
public class ConnectionDB implements InterDbcon {
    private MongoClient mongo = null;

    public ConnectionDB() throws Exception {
        this.mongo = DBPool.getDs(); // 从连接池获取连接
        // System.out.println("idle:"+ds.getIdleConnNum()+"used"+ds.getUsedConnNum());
        // this.db = mongo.get
    }

    /**
     * 流数据单独库特殊处理
     */
    // public ConnectionDB(String ltype) throws Exception {
    // if ("f".equals(ltype)) {
    // this.mongo = FDBPool.getDs();
    // } else {
    // this.mongo = DBPool.getDs(); // 从连接池获取连接
    // }
    // // System.out.println("idle:"+ds.getIdleConnNum()+"used"+ds.getUsedConnNum());
    // // this.db = mongo.get
    // }
    public MongoClient getDB() {
        if (mongo == null) {
            return null;
        }
        return mongo;
    }

    public boolean freeDB() throws Exception {
        return true;
    }

    public MongoDatabase getCollectionSpace(String cs) throws Exception {
        if (mongo == null) {
            return null;
        }
        return mongo.getDatabase(cs);
    }

    public MongoCollection<Document> getMongoCollection(String cs, String cl) throws Exception {
        if (mongo == null) {
            return null;
        }
        MongoDatabase cSpac = mongo.getDatabase(cs);
        if (cSpac == null) {
            return null;
        }
        MongoCollection<Document> coll = cSpac.getCollection(cl);
        return coll;
    }

    public MongoCollection<Document> getMongoCollection(MongoDatabase cSpac, String cl)
            throws Exception {
        if (cSpac == null) {
            return null;
        }
        return cSpac.getCollection(cl);
    }

    @SuppressWarnings("deprecation")
    public DBCollection getDBCollection(String cs, String cl) throws Exception {
        if (mongo == null) {
            return null;
        }
        Mongo mg = mongo;

        return mg.getDB(cs).getCollection(cl);
    }

}
