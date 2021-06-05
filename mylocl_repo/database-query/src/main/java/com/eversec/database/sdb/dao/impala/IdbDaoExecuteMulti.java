
package com.eversec.database.sdb.dao.impala;

import java.sql.Connection;
import java.sql.SQLException;

import com.eversec.database.sdb.dao.base.impala.IdbMultiDao;

/**
 * @author by hao.chen
 * @Classname IdbDaoExecuteMulti
 * @Description impala 多数据源执行基类
 * @Date 2018/10/19 18:48
 */
public abstract class IdbDaoExecuteMulti {
    protected Connection con;

    /**
     * 获取连接
     *
     * @return
     */
    protected Connection getConnection() throws Exception {
        if (con == null) {
            con = IdbMultiDao.getConnection();
        }
        return con;
    }

    /**
     * 获取连接
     *
     * @return
     */
    protected Connection getConnection(String key) throws Exception {
        if (con == null) {
            con = IdbMultiDao.getConnection(key);
        }
        return con;
    }

    /**
     * 关闭连接
     */
    protected void closeConn() throws SQLException {
        if (con != null) {
            con.close();
        }
    }

    public abstract Object execute(String sql) throws SQLException, Exception;
}
