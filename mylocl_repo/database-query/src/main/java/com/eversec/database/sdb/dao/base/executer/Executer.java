
package com.eversec.database.sdb.dao.base.executer;

import com.eversec.database.sdb.model.mdb.NoSqlCommand;
import com.eversec.database.sdb.model.rmessage.RMessage;

public interface Executer {
    public RMessage execute(NoSqlCommand command) throws Exception;

    public void vaildate(NoSqlCommand command) throws Exception;
}
