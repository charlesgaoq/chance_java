
package com.eversec.database.sdb.dao.impala;

import com.eversec.database.sdb.constant.Constant;
import com.eversec.database.sdb.dao.base.impala.IdbDao;
import com.eversec.database.sdb.model.rmessage.QueryRMessage;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.LinkedHashMap;
import java.util.Map;

public class IdbDaoSave {
    protected Connection con;

    public Object save(String sql, String taskid) throws Exception {
        String filePath = Constant.getConfigItem("fileSavePath");
        filePath = filePath + taskid;
        FileWriter hfw = new FileWriter(filePath);
        BufferedWriter dataWrite = new BufferedWriter(hfw);
        QueryRMessage rMessage = new QueryRMessage();
        Map<String, Object> map = new LinkedHashMap<String, Object>();
        rMessage.cmd = "save";
        Statement st = null;
        ResultSet rs = null;
        String saveFile = "";
        try {
            if (con == null) {
                con = IdbDao.getConnection();
            }
            st = con.createStatement();
            rs = st.executeQuery(sql);
            ResultSetMetaData rsd = rs.getMetaData();
            int count = rsd.getColumnCount();
            while (rs.next()) {
                for (int i = 1; i <= count; i++) {
                    String key = rsd.getColumnLabel(i);
                    Object val = rs.getObject(key);
                    saveFile = saveFile + "|" + val;
                }
                if (saveFile.startsWith("|")) {
                    saveFile = saveFile.substring(1);
                }
                dataWrite.write(saveFile);
                dataWrite.newLine();
                saveFile = "";
            }
            map.put("File Save Path:", filePath);
            rMessage.datas.add(map);
            dataWrite.flush();
            dataWrite.close();
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (st != null) {
                st.close();
            }
            if (con != null) {
                con.close();
            }
        }
        return rMessage;
    }

}
