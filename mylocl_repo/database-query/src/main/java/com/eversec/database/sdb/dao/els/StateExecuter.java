
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
import org.elasticsearch.search.aggregations.AbstractAggregationBuilder;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.BucketOrder;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.InternalNumericMetricsAggregation.SingleValue;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import com.eversec.database.sdb.model.mdb.NoSqlCommand;
import com.eversec.database.sdb.model.rmessage.QueryRMessage;
import com.eversec.database.sdb.model.rmessage.RMessage;

public class StateExecuter extends BaseExecuter {

    private void getAggs(Aggregations aggs, String key, List<Document> docs) {

        Map<String, Aggregation> aggsMap = aggs.getAsMap();
        for (Map.Entry<String, Aggregation> entry : aggsMap.entrySet()) {
            String filed = entry.getKey();
            if ("aggs_state".equals(filed)) {
                SingleValue tt = (SingleValue) entry.getValue();
                Document doc = Document
                        .parse("{" + key + "," + "\"num\":" + tt.getValueAsString() + "}");
                docs.add(doc);
            } else {
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
                        Document doc = Document.parse(
                                "{" + keyTmp + "," + "\"num\":" + bucket.getDocCount() + "}");
                        docs.add(doc);
                    }
                }
            }
        }
    }

    @Override
    public RMessage execute(NoSqlCommand command) throws Exception {
        QueryRMessage rMessage = new QueryRMessage();
        rMessage.cmd = command.cmd;

        try {
            initClient();
//            SearchRequestBuilder request = client.prepareSearch(command.cs.split("\\|", -1))
//                    .setTypes(command.cl).setSearchType(SearchType.QUERY_THEN_FETCH)
//                    .setFetchSource(false).setSize(0);
            SearchRequest request = new SearchRequest(command.cs.split("\\|", -1)).types(command.cl).searchType(SearchType.QUERY_THEN_FETCH);
            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder().fetchSource(false).size(0);
            

            BoolQueryBuilder qb = null;
            if (!command.filter.isEmpty()) {
                qb = QueryBuilders.boolQuery();
                qb.filter(queryBuilder(command.filter, ""));
//                request = request.setQuery(qb);
                searchSourceBuilder.query(qb);
            }

            AbstractAggregationBuilder aab = null;
            if ("max".equals(rMessage.cmd)) {
                aab = AggregationBuilders.max("aggs_state")
                        .field(command.fileds.getString("fileds"));
            } else if ("min".equals(rMessage.cmd)) {
                aab = AggregationBuilders.min("aggs_state")
                        .field(command.fileds.getString("fileds"));
            } else if ("sum".equals(rMessage.cmd)) {
                aab = AggregationBuilders.sum("aggs_state")
                        .field(command.fileds.getString("fileds"));
            } else if ("avg".equals(rMessage.cmd)) {
                aab = AggregationBuilders.avg("aggs_state")
                        .field(command.fileds.getString("fileds"));
            }

            String groups = command.groups.getString("groups");
            if (!"".equals(groups)) {
            	TermsAggregationBuilder terms = null;
                String[] groupArr = groups.split(",", -1);
                for (int i = groupArr.length; i > 0; i--) {
                    if (terms == null) {
                        terms = AggregationBuilders.terms(groupArr[i - 1]).field(groupArr[i - 1])
                                .size(command.limit).subAggregation(aab);
                        if (!command.sort.isEmpty()) {
                            terms.order(BucketOrder.aggregation("aggs_state",
                                    command.sort.getBoolean(command.cmd)));
                        } else {
                            terms.order(BucketOrder.aggregation("aggs_state", true));
                        }
                    } else {
                        terms = AggregationBuilders.terms(groupArr[i - 1]).field(groupArr[i - 1])
                                .size(0).subAggregation(terms);
                    }
                }

                if (terms != null) {
//                    request = request.addAggregation(terms);
                    searchSourceBuilder.aggregation(terms);
                }
            } else {
                if (aab != null) {
//                    request = request.addAggregation(aab);
                    searchSourceBuilder.aggregation(aab);
                }
            }
            request.source(searchSourceBuilder);
//            SearchResponse response = request.execute().actionGet();
            SearchResponse response = client.search(request, RequestOptions.DEFAULT);
            if (!"".equals(groups)) {
                List<Document> docs = new ArrayList<Document>();
                Aggregations aggs = response.getAggregations();
                if (aggs != null) {
                    getAggs(aggs, "", docs);
                }
                Map<String, Object> map = new LinkedHashMap<String, Object>();
                map.put("num", docs.size());
                for (Document doc : docs) {
                    rMessage.datas.add(doc);
                }
            } else {
                Map<String, Object> map = new LinkedHashMap<String, Object>();
//                map.put("num", response.getAggregations().get("aggs_state").getProperty("value"));
                map.put("num", response.getAggregations().get("aggs_state").getMetaData().get("value"));
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

            QueryBuilder qb = QueryBuilders.boolQuery().filter(QueryBuilders.termQuery(key, val));
//            SearchRequestBuilder request = client.prepareSearch(cs).setTypes(cl)
//                    .setSearchType(SearchType.QUERY_THEN_FETCH).setFetchSource(false).setSize(0);
            
            SearchRequest request = new SearchRequest(cs).types(cl).searchType(SearchType.QUERY_THEN_FETCH);
            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
            searchSourceBuilder.fetchSource(false).size(0);
            
            
//            request = request.setQuery(qb);
            searchSourceBuilder.query(qb);
            request.source(searchSourceBuilder);
            
//            SearchResponse response = request.execute().actionGet();
            SearchResponse response = client.search(request, RequestOptions.DEFAULT);
            count = response.getHits().getTotalHits();
        } finally {
            close();
        }
        return count;
    }
}
