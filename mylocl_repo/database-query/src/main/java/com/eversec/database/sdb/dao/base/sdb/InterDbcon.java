
package com.eversec.database.sdb.dao.base.sdb;

import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public interface InterDbcon {
    /**
     * 获取连接
     *
     * @return 数据库连接
     */
    public MongoClient getDB();

    /**
     * 释放数据库连接
     *
     * @return true成功，false失败
     */
    public boolean freeDB() throws Exception;

    /**
     * 获取集合空间
     *
     * @param cs
     * 集合空间名称
     * @return 集合对象
     */
    public MongoDatabase getCollectionSpace(String cs) throws Exception;

    /**
     * 获取结合
     *
     * @param cs
     * 集合空间名称
     * @param cl
     * 集合名称
     * @return 集合对象
     */
    public MongoCollection<Document> getMongoCollection(String cs, String cl) throws Exception;

    /**
     * 获取结合
     *
     * @param cSpac
     * 集合空间对象
     * @param cl
     * 集合名称
     * @return 集合对象
     */
    public MongoCollection<Document> getMongoCollection(MongoDatabase cSpac, String cl)
            throws Exception;
}
