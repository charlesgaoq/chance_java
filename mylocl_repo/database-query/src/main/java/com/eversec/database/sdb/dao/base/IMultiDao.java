
package com.eversec.database.sdb.dao.base;

import java.sql.Connection;

/**
 * @author by hao.chen
 * @Classname IMultiDao
 * @Description 定义多数据源的顶层抽象接口
 * @Date 2018/10/19 17:44
 */
public interface IMultiDao {

    /**
     * 获取当前的连接对象
     * 
     * @return
     */
    Connection getConnection() throws Exception;

    /**
     * 获取指定key的数据源的连接对象
     * 
     * @param key
     * 数据源的key
     * @return
     */
    Connection getConnection(String key) throws Exception;
}
