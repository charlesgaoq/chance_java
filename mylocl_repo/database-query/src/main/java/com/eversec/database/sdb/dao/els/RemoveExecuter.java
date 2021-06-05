
package com.eversec.database.sdb.dao.els;

import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchScrollRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.eversec.database.sdb.model.mdb.NoSqlCommand;
import com.eversec.database.sdb.model.rmessage.RMessage;
import com.eversec.database.sdb.model.rmessage.RemoverRMessage;
import com.eversec.database.sdb.util.exceptions.BaseException;
import com.mongodb.BasicDBObject;

public class RemoveExecuter extends BaseExecuter {

    private Logger Logger = LoggerFactory.getLogger(this.getClass());


    @Override
    public RMessage execute(NoSqlCommand command) throws Exception {
        RemoverRMessage rMessage = new RemoverRMessage();
        try {
            initClient();
//            SearchRequestBuilder request = client.prepareSearch(command.cs).setTypes(command.cl)
//                    .setScroll(new TimeValue(60000)).setSize(100000).setFetchSource(false);
            SearchRequest request = new SearchRequest(command.cs).types(command.cl).scroll(new TimeValue(60000));
            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
            searchSourceBuilder.size(100000).fetchSource(false);
            if (!command.filter.isEmpty()) {
                BoolQueryBuilder qb = queryBuilder(command.filter, "");
//                request = request.setPostFilter(qb);
                searchSourceBuilder.postFilter(qb);
            }
            request.source(searchSourceBuilder);
//            SearchResponse response = request.execute().actionGet();
            SearchResponse response = client.search(request, RequestOptions.DEFAULT);
//            BulkRequestBuilder brb = null;
            BulkRequest brb = null;
            // if (response.getHits().getHits().length>0) {
            // brb = client.prepareBulk();
            // }

            int delNum = 0;
            while (true) {
//                brb = client.prepareBulk();
                brb = new BulkRequest();

                for (SearchHit hit : response.getHits().getHits()) {
//                    brb.add(client.prepareDelete(hit.getIndex(), hit.getType(), hit.getId())
//                            .request());
                	brb.add(new DeleteRequest(hit.getIndex(), hit.getType(), hit.getId()));
                    delNum++;
                }
                Logger.debug("els删除数量:{}", delNum);

//                BulkResponse bulkResponse = brb.execute().actionGet();
                BulkResponse bulkResponse = client.bulk(brb, RequestOptions.DEFAULT);
                if (bulkResponse.hasFailures()) {
                    BaseException exception = new BaseException(5000, "批量操作执行部分失败！！！");
                    Logger.debug("els删除批量操作执行部分失败:{}", bulkResponse.buildFailureMessage());
                    throw exception;
                }

//                response = client.prepareSearchScroll(response.getScrollId())
//                        .setScroll(new TimeValue(60000)).execute().actionGet();
                
                SearchScrollRequest scrollRequest = new SearchScrollRequest(response.getScrollId()).scroll(new TimeValue(60000));
                response = client.searchScroll(scrollRequest, RequestOptions.DEFAULT);
                
                if (response.getHits().getHits().length == 0) {
                    break;
                }
            }

            // if (brb!=null) {
            // BulkResponse bulkResponse = brb.setRefresh(true).execute()
            // .actionGet();
            // if (bulkResponse.hasFailures()) {
            // BaseException exception = new BaseException(5000,"批量操作执行部分失败！！！");
            // System.out.println(bulkResponse.buildFailureMessage());
            // throw exception;
            // }
            // }
            rMessage.datas = delNum;

        } finally {
            close();
        }

        return rMessage;
    }

    public long execute(String cs, String cl, BasicDBObject filter) throws Exception {
        NoSqlCommand command = new NoSqlCommand();
        command.cs = cs;
        command.cl = cl;
        command.filter = BasicDBObject.parse(filter.toJson());
        execute(command);
        return 1;
    }

}
