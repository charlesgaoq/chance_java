
package com.eversec.database.sdb.dao.els;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.bson.Document;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.BucketOrder;
import org.elasticsearch.search.aggregations.bucket.filter.Filter;
import org.elasticsearch.search.aggregations.bucket.filter.FilterAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import com.eversec.database.sdb.model.mdb.NoSqlCommand;
import com.eversec.database.sdb.model.rmessage.QueryRMessage;
import com.eversec.database.sdb.model.rmessage.RMessage;

public class CountExecuter extends BaseExecuter {
	
	private static Logger logger = LoggerFactory.getLogger(CountExecuter.class);
	
    private void getAggs(Aggregations aggs, String key, List<Document> docs) {

        Map<String, Aggregation> aggsMap = aggs.getAsMap();
        for (Map.Entry<String, Aggregation> entry : aggsMap.entrySet()) {
            String filed = entry.getKey();
            Terms tt = (Terms) entry.getValue();
            for (Terms.Bucket bucket : tt.getBuckets()) {
                String keyTmp = "";
                if (!"".equals(key)) {
                    keyTmp = new StringBuffer().append(key).append(",\"").append(filed)
                            .append("\":\"").append(bucket.getKeyAsString()).append("\"")
                            .toString();
                } else {
                    keyTmp = new StringBuffer().append("\"").append(filed).append("\":\"")
                            .append(bucket.getKeyAsString()).append("\"").toString();
                }

                Aggregations aggsSon = bucket.getAggregations();
                if (!aggsSon.getAsMap().isEmpty()) {
                    getAggs(aggsSon, keyTmp, docs);
                } else {
                    // System.out.println("{"+keyTmp+","+"\"count\":"+bucket.getDocCount()+"}");
                    Document doc = Document
                            .parse("{" + keyTmp + "," + "\"num\":" + bucket.getDocCount() + "}");
                    docs.add(doc);
                }
            }
        }
    }

    @Override
    @SuppressWarnings("unused")
    public RMessage execute(NoSqlCommand command) throws Exception {
        QueryRMessage rMessage = new QueryRMessage();
        rMessage.cmd = "count";

        try {
            initClient();
            SearchRequest searchRequest = new SearchRequest(command.cs.split("\\|", -1));
            searchRequest.types(command.cl).searchType(SearchType.QUERY_THEN_FETCH);
            
            SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
            sourceBuilder.fetchSource(false).size(0);
            

            BoolQueryBuilder qb = null;
            if (!command.filter.isEmpty()) {
                qb = QueryBuilders.boolQuery();
                qb.filter(queryBuilder(command.filter, ""));
                sourceBuilder = sourceBuilder.query(qb);
            }

            FilterAggregationBuilder filterAggs = null;
            String groups = command.groups.getString("groups");
            if (!"".equals(groups)) {
            	TermsAggregationBuilder terms = null;
                // String groups = command.groups.getString("groups");
                String[] groupArr = groups.split(",", -1);
                for (int i = groupArr.length; i > 0; i--) {
                    if (terms == null) {
                        terms = AggregationBuilders.terms(groupArr[i - 1]).field(groupArr[i - 1])
                                .size(0).order(BucketOrder.count(false));
                    } else {
                        terms = AggregationBuilders.terms(groupArr[i - 1]).field(groupArr[i - 1])
                                .size(0).order(BucketOrder.count(true)).subAggregation(terms);
                    }
                }

                if (terms != null) {
                    if (filterAggs != null) {
//                        request = request.addAggregation(filterAggs.subAggregation(terms));
                        sourceBuilder.aggregation(filterAggs.subAggregation(terms));
                    } else {
//                        request = request.addAggregation(terms);
                        sourceBuilder.aggregation(terms);
                    }
                }
            }
            searchRequest.source(sourceBuilder);
//            SearchResponse response = request.execute().actionGet();
            SearchResponse response = client.search(searchRequest, RequestOptions.DEFAULT);
            if (!"".equals(groups)) {
                List<Document> docs = new ArrayList<Document>();
                Aggregations aggs = response.getAggregations();
                if (aggs != null) {
                    if (filterAggs != null) {
                        Filter filter = aggs.get("group_filter");
                        aggs = filter.getAggregations();
                        getAggs(aggs, "", docs);
                    } else {
                        getAggs(aggs, "", docs);
                    }
                }
                Map<String, Object> map = new LinkedHashMap<String, Object>();
                map.put("num", docs.size());
                for (Document doc : docs) {
                    rMessage.datas.add(doc);
                }
            } else {
                Map<String, Object> map = new LinkedHashMap<String, Object>();
                map.put("num", response.getHits().getTotalHits());
                rMessage.datas.add(map);
            }

        } finally {
            close();
        }
        return rMessage;
    }

    public long execute(String cs, String cl, String key, Object val) throws Exception {
        long count = 0L;
        try {
            initClient();
            logger.debug("初始化client 完成");
            QueryBuilder qb = QueryBuilders.boolQuery().filter(QueryBuilders.termQuery(key, val));
            SearchRequest searchRequest = new SearchRequest(cs).types(cl).searchType(SearchType.QUERY_THEN_FETCH);
            SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
            sourceBuilder.fetchSource(false).size(0);
            sourceBuilder.query(qb);
            searchRequest.source(sourceBuilder);
            logger.debug("开始查询,cs:{},cl:{},key:{},val:{}",cs,cl,key,val);
            SearchResponse response = client.search(searchRequest, RequestOptions.DEFAULT);
            logger.debug("查询结束");
            count = response.getHits().getTotalHits();
        } finally {
            close();
        }
        return count;
    }
}
