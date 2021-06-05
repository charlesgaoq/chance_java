
package com.eversec.database.sdb.dao.els;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.bson.Document;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.util.ObjectUtils;

import com.eversec.database.sdb.model.mdb.NoSqlCommand;
import com.eversec.database.sdb.model.rmessage.QueryRMessage;
import com.eversec.database.sdb.model.rmessage.RMessage;
import com.mongodb.BasicDBObject;

public class QueryExecuter extends BaseExecuter {

    @Override
    public RMessage execute(NoSqlCommand command) throws Exception {
        QueryRMessage rMessage = new QueryRMessage();
        rMessage.cmd = "query";

        try {
            command.showCommand();
            initClient();
//            SearchRequestBuilder request = client.prepareSearch(command.cs.split("\\|", -1))
//                    .setTypes(command.cl);
            SearchRequest searchRequest = new SearchRequest(command.cs.split("\\|", -1));
            searchRequest.types(command.cl);
            SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
            if (!command.filter.isEmpty()) {
                BoolQueryBuilder qb = QueryBuilders.boolQuery();
                qb.filter(queryBuilder(command.filter, ""));
                // BoolQueryBuilder qb = queryBuilder(command.filter, "");
                sourceBuilder.query(qb);
            }

            if (!"".equals(command.fileds.getString("fileds"))) {
                String[] include = (String[]) command.fileds.get("fileds");
                sourceBuilder.fetchSource(include, null);
//                request = request.setFetchSource(include, null);
            }

            if (!command.sort.isEmpty()) {
                for (Entry<String, Object> entry : command.sort.entrySet()) {
                    String key = entry.getKey();
                    Integer val = (Integer) entry.getValue();
                    if (val == 1) {
//                        request = request.addSort(key, SortOrder.ASC);
                        sourceBuilder.sort(key, SortOrder.ASC);
                    } else if (val == -1) {
//                        request = request.addSort(key, SortOrder.DESC);
                        sourceBuilder.sort(key, SortOrder.DESC);
                    }
                }
            }

//            request = request.setFrom(command.skip);
            sourceBuilder.from(command.skip);
//            request = request.setSize(command.limit);
            sourceBuilder.size(command.limit);
//            SearchResponse response = request.execute().actionGet();
            searchRequest = searchRequest.source(sourceBuilder);
            SearchResponse response = client.search(searchRequest, RequestOptions.DEFAULT);
            
            Map<String, Object> map = null;
            for (SearchHit val : response.getHits().getHits()) {

                if (!"".equals(command.fileds.getString("fileds"))) {
                    map = new LinkedHashMap<String, Object>();
                    String[] include = (String[]) command.fileds.get("fileds");
                    for (String field : include) {
//                        Object obj = val.getSource().get(field);
                        Object obj = val.getSourceAsMap().get(field);
                        map.put(field, obj);
                    }
                } else {
                    map = val.getSourceAsMap();
                }
                map.put("_id", val.getId());
                if ("idbtaskresult".equals(command.cl) && map.containsKey("result")) {
                    String item = (String) map.get("result");
                    map.remove("result");
                    Document items = Document.parse(item);
                    map.putAll(items);
                }
                rMessage.datas.add(map);
            }
        } finally {
            close();
        }
        return rMessage;
    }

    public List<Map<String, Object>> execute(String cs, String cl, BasicDBObject filter,
            BasicDBObject fileds, BasicDBObject sort, int limit, int skip) throws Exception {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        try {
            initClient();
            SearchRequest searchRequest = new SearchRequest(cs);
            searchRequest.types(cl);
            SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
            
            if (!ObjectUtils.isEmpty(filter) && !filter.isEmpty()) {
                BoolQueryBuilder qb = QueryBuilders.boolQuery();
                qb.filter(queryBuilder(filter, ""));
                // BoolQueryBuilder qb = queryBuilder(command.filter, "");
                sourceBuilder.query(qb);
                searchRequest = searchRequest.source(sourceBuilder);
            }

            if (!ObjectUtils.isEmpty(fileds) && StringUtils.isNotBlank(fileds.getString("fileds"))) {
            	String[] include = (String[]) fileds.get("fileds");
                sourceBuilder.fetchSource(include, null);
//                request = request.setFetchSource(include, null);
            }

            if (!sort.isEmpty()) {
                for (Entry<String, Object> entry : sort.entrySet()) {
                    String key = entry.getKey();
                    Integer val = (Integer) entry.getValue();
                    if (val == 1) {
                    	sourceBuilder.sort(key, SortOrder.ASC);
                    } else if (val == -1) {
                    	sourceBuilder.sort(key, SortOrder.DESC);
                    }
                }
            }

//          request = request.setFrom(command.skip);
            sourceBuilder.from(skip);
//          request = request.setSize(command.limit);
            sourceBuilder.size(limit);
//            SearchResponse response = request.execute().actionGet();
            SearchResponse response = client.search(searchRequest, RequestOptions.DEFAULT);
            Map<String, Object> map = null;
            for (SearchHit val : response.getHits().getHits()) {
                map = val.getSourceAsMap();
                map.put("_id", val.getId());
                map.put("_type", val.getType());
                map.put("_index", val.getIndex());
                list.add(map);
            }
        } finally {
            close();
        }

        return list;
    }
}
