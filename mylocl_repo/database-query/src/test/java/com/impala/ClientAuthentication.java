
package com.impala;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ClientAuthentication {

    // static String JDBC_DRIVER = "com.cloudera.impala.jdbc41.Driver";
    // static String CONNECTION_URL = "jdbc:impala://192.168.17.21:21050/fwnmg";
    //
    static String JDBC_DRIVER = "org.apache.hive.jdbc.HiveDriver";
    static String CONNECTION_URL = "jdbc:hive2://192.168.17.21:21050/;auth=noSasl";

    public static void main(String[] args) {
        Connection con = null;
        ResultSet rs = null;
        PreparedStatement ps = null;

        try {
            Class.forName(JDBC_DRIVER);
            con = DriverManager.getConnection(CONNECTION_URL);
            ps = con.prepareStatement("select count(*) from idc_par_single");
            new StopThreadTest(con).start();
            System.out.println("启动了线程");
            rs = ps.executeQuery();
            System.out.println("执行了SQL");
            while (rs.next()) {
                System.out.println(rs.getString(1));
            }

            // 2018-12-13 23:21:09 INFO Utils:285 - Supplied authorities: 192.168.17.21:21050
            // 2018-12-13 23:21:09 INFO Utils:372 - Resolved authority: 192.168.17.21:21050
            // 2018-12-13 23:21:09 INFO HiveConnection:189 - Will try to open client transport
            // with JDBC Uri: jdbc:hive2://192.168.17.21:21050/;auth=noSasl
            // 启动了线程
            // 执行了SQL
            // 0
            // closeed
            // User Default Db Statement Query Type Start Time End Time Duration Scan
            // Progress State
            // # rows fetched Details
            // default select count(*) from idc_par_single QUERY 2018-12-13 23:21:09.470285000
            // 2018-12-13 23:21:14.678678000 5s208ms 0 / 0 ( 0%) FINISHED 1 Details

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 关闭rs、ps和con
        }
    }

}
