
package com.eversec.database.sdb.dao.impala;

import java.sql.Connection;
import java.sql.SQLException;

import com.eversec.database.sdb.bo.IdbConnectionUtils;
import com.eversec.database.sdb.dao.base.impala.IdbDao;

/**
 * impala 执行基类
 *
 * @author Jony
 */
public abstract class IdbDaoExecute {

    protected Connection con;

    /**
     * 获取连接
     *
     * @return
     */
    protected Connection getConnection(String taskId) throws Exception {
        if (con == null) {
            con = IdbDao.getConnection();
            IdbConnectionUtils.addConnection(taskId, con);
        }
        return con;
    }

    /**
     * 关闭连接
     *
     */
    protected void closeConn(String taskId) throws SQLException {
        if (con != null) {
            con.close();
        }
        IdbConnectionUtils.stopConnection(taskId);
    }

    /**
     * 执行语句.
     * 
     * @param taskId
     * 查询任务ID
     * @param sql
     * 执行的SQL
     */
    public abstract Object execute(String taskId, String sql) throws SQLException, Exception;
}
