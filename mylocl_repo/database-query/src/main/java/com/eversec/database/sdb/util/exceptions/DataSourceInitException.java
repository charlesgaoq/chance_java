
package com.eversec.database.sdb.util.exceptions;

/**
 * @author by hao.chen
 * @Classname DataSourceInitException
 * @Date 2018/10/16 20:30
 */
public class DataSourceInitException extends Exception {

    private static final long serialVersionUID = -6377593005686580969L;

    public DataSourceInitException() {

    }

    public DataSourceInitException(String mesg) {
        super(mesg);
    }

    public DataSourceInitException(String mesg, Exception e) {
        super(mesg, e);
    }
}
