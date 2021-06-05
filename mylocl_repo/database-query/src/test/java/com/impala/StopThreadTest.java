
package com.impala;

import java.sql.Connection;

public class StopThreadTest extends Thread {
    private Connection con;

    public StopThreadTest(Connection con) {
        this.con = con;
    }

    @Override
    public void run() {
        try {
            Thread.sleep(5 * 1000);
            con.close();
            System.out.println("closeed ");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}