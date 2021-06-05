
package com.eversec.database.sdb.dao.els;

import java.util.List;

import org.bson.Document;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.support.WriteRequest.RefreshPolicy;
import org.elasticsearch.client.RequestOptions;

import com.eversec.database.sdb.model.mdb.NoSqlCommand;
import com.eversec.database.sdb.model.rmessage.InsertRMessage;
import com.eversec.database.sdb.model.rmessage.RMessage;

public class InsertExecuter extends BaseExecuter {

    @Override
    public RMessage execute(NoSqlCommand command) throws Exception {
        InsertRMessage rMessage = new InsertRMessage();
        try {
            rMessage.cmd = command.cmd;
            rMessage.datas = 0L;
            initClient();
            if (!command.getValues().isEmpty()) {
                rMessage.datas = command.getValues().size();
                BulkRequest brb = new BulkRequest();
                for (Document doc : command.getValues()) {
//                    brb.add(client.prepareIndex(command.cs, command.cl).setSource(doc));
                    brb.add(new IndexRequest(command.cs, command.cl).source(doc));
                }
//                BulkResponse bulkResponse = brb.execute().actionGet();
                BulkResponse bulkResponse = client.bulk(brb, RequestOptions.DEFAULT);
                
                if (bulkResponse.hasFailures()) {
                    rMessage.exception = bulkResponse.buildFailureMessage();
                    rMessage.code = -9000;
                }
            }
        } finally {
            close();
        }
        return rMessage;
    }

    public String execute(String cs, String cl, Document data) throws Exception {
        // System.out.println(">>>>>>>>>>>>>insert state"+data.toJson());
        String id = null;
        try {
            initClient();
//            IndexResponse response = client.prepareIndex(cs, cl).setSource(data).setRefresh(true)
//                    .execute().actionGet();
//            IndexResponse response = client.prepareIndex(cs, cl).setSource(data).setRefreshPolicy(RefreshPolicy.IMMEDIATE).execute().actionGet();
            IndexRequest indexRequest = new IndexRequest(cs, cl).source(data).setRefreshPolicy(RefreshPolicy.IMMEDIATE);
            IndexResponse response = client.index(indexRequest, RequestOptions.DEFAULT);
            id = response.getId();
        } finally {
            close();
        }
        return id;
    }

    public String bulkInsert(List<Document> datas, String cs, String cl) throws Exception {
        try {
            initClient();
//            BulkRequestBuilder brb = client.prepareBulk();
            BulkRequest brb = new BulkRequest();
            for (Document doc : datas) {
//                brb.add(client.prepareIndex(cs, cl).setSource(doc));
            	brb.add(new IndexRequest(cs, cl).source(doc));
            }
//            BulkResponse bulkResponse = brb.execute().actionGet();
            BulkResponse bulkResponse = client.bulk(brb, RequestOptions.DEFAULT);
            if (bulkResponse.hasFailures()) {
                // process failures by iterating through each bulk response item
                System.out.println("buildFailureMessage:" + bulkResponse.buildFailureMessage());
            }
        } finally {
            close();
        }
        return "";
    }

    public String otherEsBulkInsert(List<Document> datas, String cs, String cl) throws Exception {
        try {
            otherInitClient();
//            BulkRequestBuilder brb = client.prepareBulk();
            BulkRequest brb = new BulkRequest();
            for (Document doc : datas) {
//                brb.add(client.prepareIndex(cs, cl).setSource(doc));
            	brb.add(new IndexRequest(cs, cl).source(doc));
            }
//            BulkResponse bulkResponse = brb.execute().actionGet();
            BulkResponse bulkResponse = client.bulk(brb, RequestOptions.DEFAULT);
            if (bulkResponse.hasFailures()) {
                // process failures by iterating through each bulk response item
                System.out.println("buildFailureMessage:" + bulkResponse.buildFailureMessage());
            }
        } finally {
            close();
        }
        return "";
    }

}
