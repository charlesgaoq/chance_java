
package com.eversec.database.sdb.dao.els;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.net.URI;

import java.util.Map.Entry;

import com.eversec.database.sdb.dao.base.els.OtherElsDao;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

import com.eversec.database.sdb.constant.Constant;
import com.eversec.database.sdb.dao.base.els.ElsDao;
import com.eversec.database.sdb.dao.base.executer.Executer;
import com.eversec.database.sdb.model.mdb.NoSqlCommand;
import com.eversec.database.sdb.model.rmessage.RMessage;
import com.eversec.database.sdb.util.exceptions.BaseException;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;

public abstract class BaseExecuter implements Executer {
    protected final int code = -81000;
    protected BaseException execption;
    protected CloseableHttpClient httpClient;
    // protected List<NameValuePair> querys = new ArrayList<NameValuePair>();
    protected HttpEntity bodyData;
    protected RestHighLevelClient client;
    protected URIBuilder uriBuilder;

    // @Override
    public abstract RMessage execute(NoSqlCommand command) throws Exception;

    // @Override
    public void vaildate(NoSqlCommand command) throws Exception {

    }

    public void initClient() throws Exception {
        if (client == null) {
            ElsDao esDao = new ElsDao();
            client = esDao.getEsClient();
        }
    }

    public void otherInitClient() throws Exception {
        if (client == null) {
            OtherElsDao oElsDao = new OtherElsDao();
//            client = oElsDao.getEsClient();
        }
    }

    public void close() throws Exception {
        if (client != null) {
            client.close();
            client = null;
        }
    }

    // public String getHttpMessage(String url) throws Exception {
    // String response = "";
    // GetMethod method = null;
    // try {
    // String baseUrl = "http://192.168.200.131:9200/_command";
    // httpClient = new HttpClient();
    // method = new GetMethod(baseUrl + url);
    // httpClient.executeMethod(method);
    // response = method.getResponseBodyAsString();
    // } finally {
    // if (method!=null) {
    // method.releaseConnection();
    // httpClient.getHttpConnectionManager().closeIdleConnections(0);
    // }
    // }
    // return response;
    // }

    protected void setURI(String path) {
        uriBuilder = new URIBuilder();
        String ip = Constant.getConfigItem("esip", "");
        String port = Constant.getConfigItem("esport", "9200");
        uriBuilder.setScheme("http").setHost(ip).setPort(Integer.parseInt(port)).setPath(path);
    }

    protected String sendHttpCommand(HttpUriRequest method) throws Exception {
        String response = "";
        try {
            HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
            httpClient = httpClientBuilder.build();
            HttpResponse httpResponse = httpClient.execute(method);
            HttpEntity entity = httpResponse.getEntity();
            response = EntityUtils.toString(entity);
        } finally {
            try {
                // 关闭流并释放资源
                httpClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return response;
    }

    protected void addPairs(String key, String val) throws Exception {
        uriBuilder.setParameter(key, val);
    }

    protected void addPairs(String key, int val) throws Exception {
        addPairs(key, String.valueOf(val));
    }

    protected void addPairs(String key, long val) throws Exception {
        addPairs(key, String.valueOf(val));
    }

    protected void setBodyData(String data) throws UnsupportedEncodingException {
        bodyData = new StringEntity(data, "utf-8");
    }

    protected HttpUriRequest createHttpMethod(String type) throws Exception {
        HttpUriRequest method = null;
        URI uri = uriBuilder.build();
        if ("query".equals(type) || "count".equals(type)) {
            method = new HttpGet(uri);
        } else if ("delete".equals(type)) {
            // do delete

        } else if ("update".equals(type)) {
            // do update
        } else if ("insert".equals(type)) {
            // do insert
        } else if ("put".equals(type)) {
            method = new HttpPut(uri);
            if (bodyData != null) {
                ((HttpPut) method).setEntity(bodyData);
            }
        }
        return method;
    }

    public BoolQueryBuilder queryBuilder(BasicDBObject filter, String b) throws Exception {
        BoolQueryBuilder selfIterator = QueryBuilders.boolQuery();
        for (String key : filter.keySet()) { // 一次单数组，例如{"$and":[]}
            Object obj = filter.get(key);
            if (obj instanceof BasicDBList) {
                if ("".equals(b)) {
                    b = key;
                }
                String boolparmas = "";
                if ("$or".equals(b)) {
                    boolparmas = "should";
                } else if ("$and".equals(b)) {
                    boolparmas = "must";
                }
                Method method = null;
                BasicDBList list = (BasicDBList) obj;
                for (int i = 0; i < list.size(); i++) { // list多次循环
                    BasicDBObject item = (BasicDBObject) list.get(i);

                    if (item.containsField("$or")) {
                        BoolQueryBuilder sonIterator = queryBuilder((BasicDBObject) item, "$or");

                        method = selfIterator.getClass().getMethod(boolparmas, QueryBuilder.class);
                        method.invoke(selfIterator, sonIterator);
                    } else if (item.containsField("$and")) {
                        BoolQueryBuilder sonIterator = queryBuilder((BasicDBObject) item, "$and");

                        method = selfIterator.getClass().getMethod(boolparmas, QueryBuilder.class);
                        method.invoke(selfIterator, sonIterator);
                    } else {
                        for (Entry<String, Object> entry : item.entrySet()) { // 一次单值，例如{"a":1}
                            String name = entry.getKey();
                            Object text = entry.getValue();
                            setMatch(selfIterator, boolparmas, name, text);
                        }
                    }
                }
            } else {
                for (Entry<String, Object> entry : filter.entrySet()) { // 一次单值，例如{"a":1}
                    String name = entry.getKey();
                    Object text = entry.getValue();

                    setMatch(selfIterator, "must", name, text);
                }
            }
        }
        return selfIterator;
    }

    public void setMatch(BoolQueryBuilder selfIterator, String boolparmas, String name, Object text)
            throws Exception {
        Method method = null;
        if (text instanceof BasicDBObject) {
            BasicDBObject textItem = (BasicDBObject) text;
            Entry<String, Object> textEntry = textItem.entrySet().iterator().next();
            String txtKey = textEntry.getKey();
            Object txtVal = textEntry.getValue();

            if ("$gte".equals(txtKey)) {
                method = selfIterator.getClass().getMethod(boolparmas, QueryBuilder.class);
                method.invoke(selfIterator, QueryBuilders.rangeQuery(name).gte(txtVal));
            } else if ("$gt".equals(txtKey)) {
                method = selfIterator.getClass().getMethod(boolparmas, QueryBuilder.class);
                method.invoke(selfIterator, QueryBuilders.rangeQuery(name).gt(txtVal));
            } else if ("$lte".equals(txtKey)) {
                method = selfIterator.getClass().getMethod(boolparmas, QueryBuilder.class);
                method.invoke(selfIterator, QueryBuilders.rangeQuery(name).lte(txtVal));
            } else if ("$lt".equals(txtKey)) {
                method = selfIterator.getClass().getMethod(boolparmas, QueryBuilder.class);
                method.invoke(selfIterator, QueryBuilders.rangeQuery(name).lt(txtVal));
            } else if ("$ne".equals(txtKey)) {
                method = selfIterator.getClass().getMethod(boolparmas, QueryBuilder.class);
                if (txtVal == null) {
                    method.invoke(selfIterator,
                            QueryBuilders.boolQuery().must(QueryBuilders.existsQuery(name)));
                } else {
                    method.invoke(selfIterator, QueryBuilders.boolQuery()
                            .mustNot(QueryBuilders.termQuery(name, txtVal)));
                }
            } else if ("$in".equals(txtKey)) {
                BasicDBList inlist = (BasicDBList) txtVal;
                method = selfIterator.getClass().getMethod(boolparmas, QueryBuilder.class);
                method.invoke(selfIterator, QueryBuilders.termsQuery(name, inlist));
            } else if ("$nin".equals(txtKey)) {
                BasicDBList inlist = (BasicDBList) txtVal;
                method = selfIterator.getClass().getMethod(boolparmas, QueryBuilder.class);
                method.invoke(selfIterator,
                        QueryBuilders.boolQuery().mustNot(QueryBuilders.termsQuery(name, inlist)));
            } else if ("$regexp".equals(txtKey)) {
                method = selfIterator.getClass().getMethod(boolparmas, QueryBuilder.class);
                method.invoke(selfIterator,
                        QueryBuilders.regexpQuery(name, String.valueOf(txtVal)));
            }
        } else {
            method = selfIterator.getClass().getMethod(boolparmas, QueryBuilder.class);

            if (text == null) {
                method.invoke(selfIterator,
                        QueryBuilders.boolQuery().mustNot(QueryBuilders.existsQuery(name)));
            } else {
                method.invoke(selfIterator, QueryBuilders.termQuery(name, text));
            }
        }
    }
}
