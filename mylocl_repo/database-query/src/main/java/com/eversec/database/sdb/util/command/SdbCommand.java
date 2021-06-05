
package com.eversec.database.sdb.util.command;

import java.util.Iterator;

import net.sf.json.JSONArray;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;

import com.eversec.database.sdb.constant.Constant;
import com.eversec.database.sdb.model.mdb.NoSqlCommand;
import com.eversec.database.sdb.util.exceptions.BaseException;

public abstract class SdbCommand implements SdbCommandInter {
    protected String cmd;
    protected NoSqlCommand noCommand;
    private final int errorCode = -92000;

    public SdbCommand(String cmd) {
        noCommand = new NoSqlCommand();
        noCommand.cmd = this.cmd = cmd;
    }

    @SuppressWarnings("unchecked")
    public void validate(String command) throws JSONException, Exception {
        String mes = "";
        try {
            JSONObject jobj = null;
            if (command != null && !"".equals(command)) {
                jobj = JSONObject.fromObject(command);
                Iterator<String> it = jobj.keys();
                while (it.hasNext()) {
                    String key = it.next();
                    if (!Constant.isCommandItem(key)) {
                        mes = key + " 不是command的合法参数";
                        BaseException be = new BaseException(errorCode, mes);
                        throw be;
                    }
                }
            }

            if ("".equals(mes)) {
                if ("insert".equals(cmd)) {
                    if (jobj == null || jobj.isEmpty()) {
                        mes = "未找到添加数据datas命令对象！";
                        BaseException be = new BaseException(errorCode, mes);
                        throw be;
                    } else {
                        JSONArray jj = (JSONArray) jobj.get("datas");
                        if (jj == null || jj.isEmpty()) {
                            mes = "datas 添加数据无内容！";
                            BaseException be = new BaseException(errorCode, mes);
                            throw be;
                        } else if (jj.size() > 10) {
                            mes = "datas 内的数据条数不能超过10条！";
                            BaseException be = new BaseException(errorCode, mes);
                            throw be;
                        }
                    }
                } else if ("update".equals(cmd)) {
                    if (jobj == null || jobj.isEmpty()) {
                        mes = "未找到修改数据modifier命令对象！";
                        BaseException be = new BaseException(errorCode, mes);
                        throw be;
                    } else {
                        JSONObject jj = (JSONObject) jobj.get("modifier");
                        if (jj == null || jj.isEmpty()) {
                            mes = "modifier 修改数据无内容！";
                            BaseException be = new BaseException(errorCode, mes);
                            throw be;
                        }
                    }
                } else if ("aggregate".equals(cmd)) {
                    if (jobj == null || jobj.isEmpty()) {
                        mes = "未找到分组group命令对象！";
                        BaseException be = new BaseException(errorCode, mes);
                        throw be;
                    } else {
                        JSONObject jj = (JSONObject) jobj.get("group");
                        if (jj == null || jj.isEmpty()) {
                            mes = "group 组数据无内容！";
                            BaseException be = new BaseException(errorCode, mes);
                            throw be;
                        }
                    }
                } else if ("remove".equals(cmd)) {
                    if (jobj == null || jobj.isEmpty()) {
                        mes = "未找到修改数据matcher命令对象！";
                        BaseException be = new BaseException(errorCode, mes);
                        throw be;
                    } else {
                        JSONObject jj = (JSONObject) jobj.get("matcher");
                        if (jj == null || jj.isEmpty()) {
                            mes = "remove 必须匹配删除条件，全部删除数请使用removeall命令";
                            BaseException be = new BaseException(errorCode, mes);
                            throw be;
                        }
                    }
                }
            }
        } catch (JSONException e) {
            throw e;
        } catch (Exception e) {
            throw e;
        }
    }

    public NoSqlCommand getCommand() {
        return noCommand;
    }

}
