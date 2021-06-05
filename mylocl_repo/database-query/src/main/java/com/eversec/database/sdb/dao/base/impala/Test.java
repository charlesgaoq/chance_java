
package com.eversec.database.sdb.dao.base.impala;

import org.apache.commons.dbcp.BasicDataSource;

public class Test {

    public static void main(String[] args) throws Exception {
        BasicDataSource dataSource = new BasicDataSource();
        // logger.debug("env 环境:{}",env);//org.apache.commons.dbcp.BasicDataSource
        dataSource.setUrl("jdbc:hive2://192.168.17.21:2181,192.168.17.22:2181"
                + ",192.168.17.23:2181/" + ";auth=noSasl;serviceDiscoveryMode=zooKeeper");
        // dataSource.setUrl("jdbc:hive2://192.168.17.21:21050/;auth=noSasl");
        dataSource.setDriverClassName("org.apache.hive.jdbc.HiveDriver");
        dataSource.setInitialSize(3);
        dataSource.setMaxActive(5);
        dataSource.setMaxIdle(1);
        dataSource.setMinIdle(5);
        dataSource.setValidationQuery("SELECT 1");
        dataSource.setTestWhileIdle(true);
        dataSource.setTimeBetweenEvictionRunsMillis(3600000L);
        dataSource.setMinEvictableIdleTimeMillis(18000000L);
        dataSource.setTestOnBorrow(true);
        System.out.println(dataSource);
        System.out.println(dataSource.getConnection());
    }

}
