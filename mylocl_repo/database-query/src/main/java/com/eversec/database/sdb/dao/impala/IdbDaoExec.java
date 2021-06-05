
package com.eversec.database.sdb.dao.impala;

import java.sql.SQLException;
import java.sql.Statement;

import com.eversec.database.sdb.model.rmessage.CountRMessage;

public class IdbDaoExec extends IdbDaoExecute {

    @Override
    public Object execute(String taskId, String sql) throws SQLException, Exception {
        CountRMessage rMessage = new CountRMessage();
        rMessage.cmd = "exec";
        Statement st = null;
        try {
            con = getConnection(taskId);
            st = con.createStatement();
            st.execute(sql);
            rMessage.datas = 1L;
        } finally {
            if (st != null) {
                st.close();
            }
            closeConn(taskId);
        }
        return rMessage;
    }

}
