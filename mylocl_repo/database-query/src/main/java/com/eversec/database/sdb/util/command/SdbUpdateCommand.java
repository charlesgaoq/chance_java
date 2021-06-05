
package com.eversec.database.sdb.util.command;

import java.util.Map;

import org.bson.types.ObjectId;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;

public class SdbUpdateCommand extends SdbCommand {
    public SdbUpdateCommand() {
        super("update");
    }

    // @Override
    public void compileCommand(String command) throws Exception {
        validate(command);
        if (command != null && !"".equals(command)) {
            BasicDBObject commandMap = BasicDBObject.parse(command);

            if (commandMap.containsField("matcher")) {
                BasicDBObject obj = (BasicDBObject) commandMap.get("matcher");
                if (obj.containsField("_id")) {
                    Object val = obj.get("_id");
                    if (val instanceof BasicDBObject) {
                        BasicDBObject itm = (BasicDBObject) val;
                        if (itm.containsField("$in")) {
                            Object valin = itm.get("$in");
                            if (valin instanceof BasicDBList) {
                                BasicDBList itmin = (BasicDBList) valin;
                                for (int n = 0; n < itmin.size(); n++) {
                                    Object objin = itmin.get(n);
                                    itmin.remove(n);
                                    itmin.add(n, new ObjectId(objin.toString()));

                                }
                            }
                        }
                    } else {
                        obj.put("_id", new ObjectId(val.toString()));
                    }
                }
                noCommand.filter = obj;
            }

            /*
             * obj = commandMap.get("upsert"); if (obj==null) {
             * commandMap.put("upsert", false); }
             */

            BasicDBObject setMap = new BasicDBObject();
            BasicDBObject incMap = new BasicDBObject();

            BasicDBObject map = (BasicDBObject) commandMap.get("modifier");
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                String key = entry.getKey();
                Object val = entry.getValue();
                if (val instanceof BasicDBObject) {
                    incMap.put(key, ((BasicDBObject) val).get("$add"));
                } else {
                    setMap.put(key, val);
                }
            }
            map.clear();
            if (!setMap.isEmpty()) {
                map.put("$set", setMap);
            }
            if (!incMap.isEmpty()) {
                map.put("$inc", incMap);
            }
            noCommand.set = map;
        }
    }

}
