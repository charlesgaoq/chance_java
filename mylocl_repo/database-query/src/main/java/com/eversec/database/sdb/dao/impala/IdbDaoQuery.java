
package com.eversec.database.sdb.dao.impala;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedHashMap;
import java.util.Map;

import com.eversec.database.sdb.model.rmessage.QueryRMessage;
import com.eversec.database.sdb.model.rmessage.RMessage;

/**
 * impala 查询类
 *
 * @author Jony
 */
public class IdbDaoQuery extends IdbDaoExecute {

    @Override
    public RMessage execute(String taskId, String sql) throws SQLException, Exception {
        QueryRMessage rMessage = new QueryRMessage();
        rMessage.cmd = "query";
        Statement st = null;
        ResultSet rs = null;
        try {
            con = getConnection(taskId);
            st = con.createStatement();
            rs = st.executeQuery(sql);
            ResultSetMetaData rsd = rs.getMetaData();
            int count = rsd.getColumnCount();
            while (rs.next()) {
                Map<String, Object> map = new LinkedHashMap<String, Object>();
                for (int i = 1; i <= count; i++) {
                    String key = rsd.getColumnLabel(i);
                    Object val = rs.getObject(key);
                    map.put(key, val);
                }
                rMessage.datas.add(map);
            }
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (st != null) {
                st.close();
            }
            closeConn(taskId);
        }
        return rMessage;
    }
}
