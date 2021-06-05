
package com.eversec.database.sdb.util.command;

import com.eversec.database.sdb.model.mdb.NoSqlCommand;

import net.sf.json.JSONException;

/**
 * 命令编译接口
 *
 * @author Jony
 */
public interface SdbCommandInter {
    /**
     * 命令编译方法
     *
     */
    public void compileCommand(String command) throws Exception;

    /**
     * 校验方法
     *
     * @return
     */
    public void validate(String command) throws JSONException, Exception;

    /**
     * 命令获取
     *
     * @return
     */
    public NoSqlCommand getCommand();
}
