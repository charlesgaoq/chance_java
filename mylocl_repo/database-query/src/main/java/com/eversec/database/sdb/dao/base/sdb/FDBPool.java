
package com.eversec.database.sdb.dao.base.sdb;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.ReadPreference;
import com.mongodb.ServerAddress;
import com.mongodb.WriteConcern;

/**
 * 流数据特殊处理连接
 */
public class FDBPool {
    private static MongoClient mongo;

    static {
        initDBPool();
    }

    /**
     * 初始化数据库连接池
     */
    public static void initDBPool() {
        try {
            InputStream is = FDBPool.class.getClassLoader().getResourceAsStream("sdb.properties");
            Properties properties = new Properties();
            properties.load(is);
            String server1 = properties.getProperty("sdb.fserver1");
            // String server2=properties.getProperty("sdb.server2");
            // String server3=properties.getProperty("sdb.server3");
            int port = Integer.parseInt(properties.getProperty("sdb.fport"));

            // 服务列表
            List<ServerAddress> replicaSetSeeds = new ArrayList<ServerAddress>();
            replicaSetSeeds.add(new ServerAddress(server1, port));
            // replicaSetSeeds.add(new ServerAddress(server2, port));
            // replicaSetSeeds.add(new ServerAddress(server3, port));
            // 连接池参数设置
            MongoClientOptions options = null;
            options = new MongoClientOptions.Builder().socketKeepAlive(true) // 是否保持长链接
                    .connectTimeout(1000 * 20) // 链接超时时间
                    .socketTimeout(1000 * 10) // read数据超时时间
                    .readPreference(ReadPreference.primary()) // 最近优先策略
                    // .autoConnectRetry(false) // 是否重试机制
                    .connectionsPerHost(50) // 每个地址最大请求数
                    .maxWaitTime(1000 * 60 * 2) // 长链接的最大等待时间
                    .threadsAllowedToBlockForConnectionMultiplier(50) // 一个socket最大的等待请求数
                    .writeConcern(WriteConcern.ACKNOWLEDGED).build();

            mongo = new MongoClient(replicaSetSeeds, options);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static MongoClient getDs() {
        if (mongo == null) {
            initDBPool();
            System.out.println("new pool");
        }
        return mongo;
    }
}
