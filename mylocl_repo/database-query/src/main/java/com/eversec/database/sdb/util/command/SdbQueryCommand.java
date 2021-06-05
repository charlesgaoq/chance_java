
package com.eversec.database.sdb.util.command;

import org.bson.types.ObjectId;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;

public class SdbQueryCommand extends SdbCommand {
    public SdbQueryCommand(String cmd) {
        super(cmd);
    }

    // @Override
    public void compileCommand(String command) throws Exception {
        validate(command);
        if (command != null && !"".equals(command)) {
            BasicDBObject commandMap = BasicDBObject.parse(command);
            if (commandMap.containsField("limit")) {
                noCommand.limit = commandMap.getInt("limit");
            }
            if (commandMap.containsField("skip")) {
                noCommand.skip = commandMap.getInt("skip");
            }
            if (commandMap.containsField("sort")) {
                noCommand.sort = (BasicDBObject) commandMap.get("sort");
            }
            if (commandMap.containsField("selector")) {
                noCommand.fileds = (BasicDBObject) commandMap.get("selector");
            }

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
        }
    }
}
