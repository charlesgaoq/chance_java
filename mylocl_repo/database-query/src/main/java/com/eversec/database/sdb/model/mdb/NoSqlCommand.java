
package com.eversec.database.sdb.model.mdb;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;

public class NoSqlCommand {

    private static Logger LOG = LoggerFactory.getLogger(NoSqlCommand.class);

    public BasicDBObject filter;
    public BasicDBObject fileds;
    public int skip;
    public int limit;
    public BasicDBObject groups;
    public BasicDBObject sort;
    public BasicDBObject set;
    public BasicDBObject inc;
    public BasicDBList values;
    public String cmd;
    public String cs;
    public String cl;

    public NoSqlCommand() {
        filter = new BasicDBObject();
        fileds = new BasicDBObject();
        skip = 0;
        limit = 10;
        groups = new BasicDBObject();
        sort = new BasicDBObject();
        set = new BasicDBObject();
        inc = new BasicDBObject();
        values = new BasicDBList();
        cmd = "";
        cs = "";
        cl = "";
    }

    public void showCommand() {
        LOG.debug("SQL:{}", this.toString());
        // System.out.println("cmd:" + cmd);
        // System.out.println("cs:" + cs);
        // System.out.println("cl:" + cl);
        // System.out.println("filter:" + filter.toString());
        // System.out.println("fileds:" + fileds.toString());
        // System.out.println("skip:" + skip);
        // System.out.println("limit:" + limit);
        // System.out.println("sort:" + sort.toString());
        // System.out.println("set:" + set.toString());
        // System.out.println("inc:" + inc.toString());
        // System.out.println("values:" + values.toString());
    }

    @Override
    public String toString() {
        return "NoSqlCommand [filter=" + filter.toString() + ", fileds=" + fileds.toString()
                + ", skip=" + skip + ", limit=" + limit + ", groups=" + groups + ", sort="
                + sort.toString() + ", set=" + set.toString() + ", inc=" + inc.toString()
                + ", values=" + values.toString() + ", cmd=" + cmd + ", cs=" + cs + ", cl=" + cl
                + "]";
    }

    public List<Document> getValues() {
        List<Document> listValues = new ArrayList<Document>();
        for (Object val : values) {
            BasicDBObject doc = (BasicDBObject) val;
            listValues.add(new Document(doc));
        }
        return listValues;
    }
}
