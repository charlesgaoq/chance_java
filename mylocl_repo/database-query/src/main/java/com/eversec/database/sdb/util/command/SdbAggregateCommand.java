
package com.eversec.database.sdb.util.command;

/**
 * 聚合函数命令编译类
 *
 * @author Jony
 */
public class SdbAggregateCommand extends SdbCommand {
    public SdbAggregateCommand() {
        super("aggregate");
    }

    // @Override
    public void compileCommand(String command) {
        // commandMap = new HashMap<String, Object>();
        // if (command != null && !"".equals(command)) {
        // commandMap.putAll(JSONObject.fromObject(command));
        // Object obj = commandMap.get("selector");
        // if (obj != null) {
        // StringBuffer stb = new StringBuffer();
        // StringBuffer stc = new StringBuffer();
        // String ms = obj.toString();
        // JSONObject jobj = JSONObject.fromObject(ms);
        // Iterator<String> iterator = jobj.keys();
        // while (iterator.hasNext()) {
        // String key = iterator.next();
        // String val = jobj.getString(key);
        // if ("".equals(key.trim())) {
        // continue;
        // }
        // if ("".equals(val.trim())) {
        // val = key;
        // }
        // if (stb.length() == 0) {
        // stb.append(val + ":" + "\"$" + key + "\"");
        // stc.append(val + ":{$first:\"$" + key + "\"}");
        // } else {
        // stb.append("," + val + ":" + "\"$" + key + "\"");
        // stc.append("," + val + ":{$first:\"$" + key + "\"}");
        // }
        // }
        // stb.append("}");
        // stb.insert(0, "_id:{");
        // commandMap.put("_id", stb.toString());
        // commandMap.put("selector", stc.toString());
        // }
        //
        // obj = commandMap.get("group");
        // if (obj != null) {
        // StringBuffer stb = new StringBuffer();
        // String ms = obj.toString();
        // JSONObject jobj = JSONObject.fromObject(ms);
        // Iterator<String> iterator = jobj.keys();
        // while (iterator.hasNext()) {
        // String key = iterator.next();
        // String val = jobj.getString(key);
        // if ("".equals(key.trim()) || "".equals(val.trim())) {
        // continue;
        // }
        // JSONObject jobjc = JSONObject.fromObject(val);
        // Iterator<String> itc = jobjc.keys();
        // while (itc.hasNext()) {
        // String ckey = (String) itc.next();
        // String cval = jobjc.getString(ckey);
        // if ("".equals(ckey.trim())) {
        // continue;
        // }
        // if ("".equals(cval.trim())) {
        // cval = ckey;
        // }
        // if (stb.length() == 0) {
        // stb.append(cval + ":{$" + key + ":\"$" + ckey
        // + "\"}");
        // } else {
        // stb.append("," + cval + ":{$" + key + ":\"$" + ckey
        // + "\"}");
        // }
        // }
        // }
        // commandMap.put("group", stb.toString());
        // }
        // obj = commandMap.get("limit");
        // commandMap.put("limit", (obj == null ? 10 : obj));
        // obj = commandMap.get("skip");
        // commandMap.put("skip", (obj == null ? 0 : obj));
        // }
    }
}
