
package com.eversec.database.sdb.dao.els;

import java.util.List;
import java.util.Map;

import org.bson.Document;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.support.ActiveShardCount;
import org.elasticsearch.action.support.WriteRequest.RefreshPolicy;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;

import com.eversec.database.sdb.model.mdb.NoSqlCommand;
import com.eversec.database.sdb.model.rmessage.RMessage;
import com.eversec.database.sdb.model.rmessage.UpdateRMessage;
import com.eversec.database.sdb.util.exceptions.BaseException;

public class UpdateExecuter extends BaseExecuter {

    @Override
    public RMessage execute(NoSqlCommand command) throws Exception {
        QueryExecuter qe = new QueryExecuter();
        String[] fileds = { "aa" };
        command.fileds.put("fileds", fileds);
        List<Map<String, Object>> list = qe.execute(command.cs, command.cl, command.filter,
                command.fileds, command.sort, 10000, 0);

        UpdateRMessage rMessage = new UpdateRMessage();
        if (!list.isEmpty()) {
            try {
                initClient();
//                BulkRequestBuilder brb = client.prepareBulk();
                BulkRequest brb = new BulkRequest();
                for (Map<String, Object> sel : list) {
                    String id = (String) sel.get("_id");
                    // {$set:{level:100,state:3,age:1}}
                    @SuppressWarnings("unchecked")
                    Map<String, Object> doc = (Map<String, Object>) command.set.get("$set");
                    String index = (String) sel.get("_index");
                    String type = (String) sel.get("_type");
//                    brb.add(client.prepareUpdate(index, type, id).setDoc(doc));
                    brb.add(new UpdateRequest(index, type, id).doc(doc));
                }
//                BulkResponse bulkResponse = brb.setRefresh(true).execute().actionGet();
//                BulkResponse bulkResponse = brb.setRefreshPolicy(RefreshPolicy.IMMEDIATE).execute().actionGet();
                brb.setRefreshPolicy(RefreshPolicy.IMMEDIATE);
                BulkResponse bulkResponse = client.bulk(brb, RequestOptions.DEFAULT);
                if (bulkResponse.hasFailures()) {
                    BaseException exception = new BaseException(5000, "批量操作执行部分失败！！！");
                    System.out.println(bulkResponse.buildFailureMessage());
                    throw exception;
                }

            } finally {
                close();
            }
            rMessage.datas = list.size();
        }
        return rMessage;
    }

    public long execute(String cs, String cl, Document doc, String id) throws Exception {
        long version = 0L;
        try {
            initClient();
//            UpdateResponse response = client.prepareUpdate(cs, cl, id).setDoc(doc).setRefreshPolicy(RefreshPolicy.IMMEDIATE)
//        				.setWaitForActiveShards(ActiveShardCount.ALL).execute().actionGet();
            
            UpdateRequest updateRequest = new UpdateRequest(cs, cl, id).doc(doc).setRefreshPolicy(RefreshPolicy.IMMEDIATE)
            		.waitForActiveShards(ActiveShardCount.ALL);
            UpdateResponse response = client.update(updateRequest, RequestOptions.DEFAULT);
            
            
            version = response.getVersion();
        } finally {
            close();
        }
        return version;
    }
}
