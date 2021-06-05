
package com.eversec.database.sdb.util.command;

import com.mongodb.BasicDBObject;

public class SdbCountCommand extends SdbCommand {
    public SdbCountCommand(String cmd) {
        super(cmd);
    }

    // @Override
    public void compileCommand(String command) throws Exception {
        validate(command);
        if (command != null && !"".equals(command)) {
            BasicDBObject commandMap = BasicDBObject.parse(command);
            if (commandMap.containsField("matcher")) {
                noCommand.filter = (BasicDBObject) commandMap.get("matcher");
            }
        }
    }
}
