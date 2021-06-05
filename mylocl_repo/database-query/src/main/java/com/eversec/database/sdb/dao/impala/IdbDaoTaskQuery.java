
package com.eversec.database.sdb.dao.impala;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.bson.Document;
import com.eversec.database.sdb.dao.els.InsertExecuter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class IdbDaoTaskQuery extends IdbDaoExecute {
    public String taskid;

    public String rcs;

    public String rcl;

    public String taskdate;

    @Override
    public Object execute(String taskId, String sql) throws SQLException, Exception {
        // String esTaskFile = Constant.getConfigItem("esTaskFile");
        // File file = new File(esTaskFile);
        // if (!file.exists()) {
        // file.mkdirs();
        // }
        // file = new File(esTaskFile+taskid+"_"+System.currentTimeMillis()+".tmp");
        // RandomAccessFile rafile = new RandomAccessFile(file, "rw");
        // List<Document> otherDatas = new ArrayList<Document>();
        List<Document> datas = new ArrayList<Document>();
        Statement st = null;
        ResultSet rs = null;
        Long n = 0L;
        try {
            con = getConnection(taskId);
            st = con.createStatement();
            rs = st.executeQuery(sql);
            ResultSetMetaData rsd = rs.getMetaData();
            int count = rsd.getColumnCount();
            while (rs.next()) {
                n++;
                Document map = new Document();
                // Document otherMap = new Document();
                for (int i = 1; i <= count; i++) {
                    String key = rsd.getColumnLabel(i);
                    Object val = rs.getObject(key);
                    map.put(key, val);
                }
                Gson gson = new GsonBuilder().enableComplexMapKeySerialization().create();
                String result = gson.toJson(map);
                map.clear();
                map.put("sort_id", n);
                map.put("result", result);
                map.put("taskid", taskid);
                map.put("taskdate", taskdate);
                datas.add(map);
                // 往另一个es存数据
                // otherMap.put("sort_id", n);
                // otherMap.put("result", result);
                // otherMap.put("taskid", taskid);
                // otherMap.put("taskdate", taskdate);
                // otherDatas.add(otherMap);
                if (n % 3000 == 0) {
                    saveResult(datas);
                    datas.clear();
                    // saveOtherEsResult(otherDatas);
                    // otherDatas.clear();
                }
            }
            if (!datas.isEmpty()) {
                // saveResultFile(datas,rafile);
                // new IdbDaoSave().save(sql,taskid); //写标准化文件到本地
                saveResult(datas);
                datas.clear();
                // saveOtherEsResult(otherDatas);
                // otherDatas.clear();
            }
            // if (n == 0L) {
            // System.out.println(">>>>>>>>>>>>>>>>>>>select 0"+taskid);
            // Document data = new Document();
            // data.put("taskid", taskid);
            // data.put("taskdate", taskdate);
            // data.put("message", "no data");
            // datas.add(data);
            // saveResultFile(datas,rafile);
            // closeDeleteFile(file,rafile);
            // }else{
            // closeFile(file,rafile);
            // }
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (st != null) {
                st.close();
            }
            closeConn(taskId);
            // if (rafile != null) {
            // rafile.close();
            // }
        }
        return n;
    }

    public void saveResult(List<Document> datas) throws Exception {
        InsertExecuter sdi = new InsertExecuter();
        sdi.bulkInsert(datas, rcs, rcl);
    }

    public void saveOtherEsResult(List<Document> datas) throws Exception {
        InsertExecuter sdi = new InsertExecuter();
        sdi.otherEsBulkInsert(datas, rcs, rcl);
    }

    // public void saveResultFile(List<Document> datas,RandomAccessFile rafile) throws Exception {
    // StringBuffer buffer = new StringBuffer();
    // for (Document doc:datas) {
    // buffer.append(taskdate).append("|");
    // buffer.append(doc.toJson()).append("|");
    // buffer.append(taskid).append("\n");
    // }
    // ByteBuffer source = ByteBuffer.wrap(buffer.toString().getBytes("utf-8"));
    // rafile.getChannel().write(source);
    // }
    //
    // public void closeFile(File file,RandomAccessFile rafile) throws IOException{
    // rafile.close();
    // rafile = null;
    // file.renameTo(new File(file.getAbsolutePath()+".csv"));
    // }
    //
    // public void closeDeleteFile(File file,RandomAccessFile rafile) throws IOException{
    // rafile.close();
    // rafile = null;
    // file.delete();
    // }

    public static void main(String[] args) {
        Document map = new Document();
        map.put("a1", 1);
        map.put("a2", "cc");
        map.put("a3", null);
        // Gson gson = new GsonBuilder().enableComplexMapKeySerialization().create();
        // String result = gson.toJson(map);
        String result = map.toJson();
        System.out.println(result);
    }
}
