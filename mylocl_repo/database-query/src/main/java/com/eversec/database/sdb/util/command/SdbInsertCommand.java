
package com.eversec.database.sdb.util.command;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;

public class SdbInsertCommand extends SdbCommand {
    public SdbInsertCommand(String cmd) {
        super(cmd);
    }

    // @Override
    public void compileCommand(String command) throws Exception {
        validate(command);
        if (command != null && !"".equals(command)) {
            BasicDBObject commandMap = BasicDBObject.parse(command);
            noCommand.values = (BasicDBList) commandMap.get("datas");
        }
    }
}
