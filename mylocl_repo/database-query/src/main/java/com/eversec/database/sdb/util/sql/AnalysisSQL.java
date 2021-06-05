
package com.eversec.database.sdb.util.sql;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.eversec.database.sdb.model.mdb.NoSqlCommand;
import com.eversec.database.sdb.util.exceptions.BaseException;
import com.mongodb.BasicDBObject;

/*
 * sql语句解析
 * 错误编码:SQL0011
 */
public class AnalysisSQL {
    private final int code = -90011;
    private String sql;
    private NoSqlCommand command;
    private BaseException exception;

    public AnalysisSQL() {
        // code = 0;
        exception = null;
        command = new NoSqlCommand();
    }

    public void setSql(String sql) {
        if (sql != null) {
            this.sql = sql.toLowerCase().replaceAll("\\s{1,}", " ").trim() + " endsql";
        } else {
            this.sql = "";
        }
    }

    public NoSqlCommand getCommand() {
        return this.command;
    }

    public void analysis() throws Exception {
        Pattern p = null;
        Matcher m = null;
        if (sql == null || "".equals(sql)) {
            exception = new BaseException(code, "sql语句为空！！！");
            throw exception;
        } else if (sql.indexOf("select") == 0) {
            p = Pattern.compile("select .+ from");
            m = p.matcher(sql);
            if (!m.find()) {
                exception = new BaseException(code, "select from 语法错误！");
                throw exception;
            } else {
                select();
            }
        } else if (sql.indexOf("update") == 0) {
            p = Pattern.compile("update .+ set");
            m = p.matcher(sql);
            if (!m.find()) {
                exception = new BaseException(code, "update set 语法错误！");
                throw exception;
            } else {
                update();
            }
        } else if (sql.indexOf("insert") == 0) {
            p = Pattern.compile("insert into.+values");
            m = p.matcher(sql);
            if (!m.find()) {
                exception = new BaseException(code, "insert into values 语法错误！");
                throw exception;
            } else {
                insert();
            }
        } else if (sql.indexOf("delete") == 0) {
            p = Pattern.compile("delete from .+");
            m = p.matcher(sql);
            if (!m.find()) {
                exception = new BaseException(code, "delete from 语法错误！");
                throw exception;
            } else {
                delete();
            }
        } else {
            exception = new BaseException(code, "接口未识别的sql语法错误！");
            throw exception;
        }
    }

    /*
     * 处理查询
     */
    public void select() throws Exception {
        // 获取查询字段，并判断是否是统计查询
        String items = shuffle("(select) (.+) (from)", 2);
        if (!"".equals(items.trim())) {
            if ("count(*)".equals(items.trim())) {
                command.cmd = "count";
            } else if (!"*".equals(items.trim())) {
                for (String filed : items.split(",", -1)) {
                    command.fileds.put(filed, "");
                }
                command.cmd = "query";
            } else {
                command.cmd = "query";
            }
        } else {
            exception = new BaseException(code, "未识别到查询项！");
            throw exception;
        }

        // 判断库和表
        items = shuffle("(select) (.+) (from) (.+?) ", 4);
        if (!"".equals(items.trim())) {
            if (items.indexOf(".") == -1) {
                exception = new BaseException(code, "数据源名称不符合接口规范（库名.表名）！");
                throw exception;
            }
            command.cs = items.split("\\.", -1)[0];
            command.cl = items.split("\\.", -1)[1];
        } else {
            exception = new BaseException(code, "未识别到数据源名称（库名.表名）！");
            throw exception;
        }

        // 获取查询条件
        items = shuffle("(select) (.+) (from) (.+) (where) (.+?) (order by|skip|limit|endsql)", 6);
        if (!"".equals(items.trim())) {
            String bsonFilter = where(items);
            try {
                command.filter = BasicDBObject.parse(bsonFilter);
            } catch (Exception e) {
                throw e;
            }
        }

        // 获取排序信息
        items = shuffle("(.+) (order by) (.+?) (skip|limit|endsql)", 3);
        if (!"".equals(items.trim())) {
            String[] sortItems = items.split(",", -1);
            for (String sortItem : sortItems) {
                String[] orders = sortItem.split(" ", -1);
                if (orders.length == 1) {
                    command.sort.put(orders[0], 1);
                } else if (orders.length == 2) {
                    if ("asc".equals(orders[1].trim())) {
                        command.sort.put(orders[0], 1);
                    } else if ("desc".equals(orders[1].trim())) {
                        command.sort.put(orders[0], -1);
                    } else {
                        exception = new BaseException(code, "不可识别的排序命令语句！ [" + items + "]");
                        throw exception;
                    }
                } else {
                    exception = new BaseException(code, "未识别到order by排序项！");
                    throw exception;
                }
            }
        }

        // 获取skip参数
        items = shuffle("(.+) (skip) (.+?) (limit|endsql)", 3);
        if (!"".equals(items.trim())) {
            try {
                command.skip = Integer.parseInt(items);
            } catch (Exception e) {
                exception = new BaseException(code, "skip 命令参数应该为整数！ [" + items + "]");
                throw exception;
            }
        }

        // 获取limit参数
        items = shuffle("(.+) (limit) (.+?) (skip|endsql)", 3);
        if (!"".equals(items.trim())) {
            try {
                command.limit = Integer.parseInt(items);
            } catch (Exception e) {
                e.printStackTrace();
                exception = new BaseException(code, "limit 命令参数应该为整数！ [" + items + "]");
                throw exception;
            }
        }
    }

    /**
     * 查询条件转换
     *
     * @param filter
     * :sql形式的查询条件
     * @return Nosql查询命令形式
     */
    public String where(String filter) throws Exception {
        filter = toComparison(filter);
        String item = shuffle(filter, "(\\([^\\(\\)]*\\))", 1);
        while (!"".equals(item)) {
            for (String chunk : item.split("#", -1)) {
                String newChunk = toLogical(chunk);
                filter = filter.replace(chunk, newChunk);
            }
            item = shuffle(filter, "(\\([^\\(\\)]*\\))", 1);
        }
        filter = toLogical(filter);
        return filter;
    }

    /**
     * 处理查询条件的比对命令
     *
     */
    public String toComparison(String filter) throws Exception {
        String segmentRegExp = "([^\\s\\(]+)( *)(=|>|<|>=|<=|<>|!=)( *)([^\\s\\)]+)";
        Pattern pattern = Pattern.compile(segmentRegExp, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(filter);
        while (matcher.find()) {
            String lt = matcher.group(1).trim();
            String ml = matcher.group(3).trim();
            String rt = matcher.group(5).trim();
            if ("_id".equals(lt)) {
                rt = "{\"$oid\":" + rt + "}";
            }
            String olditem = matcher.group();
            String newitem = coverComparison(lt, ml, rt);
            filter = filter.replace(olditem, newitem);
        }

        segmentRegExp = "([^\\s\\(]+?)( +)(in|not in)( +)(\\(.+?\\))";
        pattern = Pattern.compile(segmentRegExp, Pattern.CASE_INSENSITIVE);
        matcher = pattern.matcher(filter);
        while (matcher.find()) {
            String lt = matcher.group(1);
            String ml = matcher.group(3);
            String rt = matcher.group(5);
            if ("_id".equals(lt)) {
                String[] rts = rt.substring(1, rt.length() - 1).split(",", -1);
                rt = "(";
                for (int i = 0; i < rts.length; i++) {
                    rt += "{\"$oid\":" + rts[i].trim() + "},";
                }
                rt = rt.substring(0, rt.length() - 1) + ")";
            }
            String olditem = matcher.group();
            String newitem = coverComparison(lt, ml, rt);
            filter = filter.replace(olditem, newitem);
        }

        segmentRegExp = "([^\\s\\(]+?)( +)(like)( +)('[^']+?')";
        pattern = Pattern.compile(segmentRegExp, Pattern.CASE_INSENSITIVE);
        matcher = pattern.matcher(filter);
        while (matcher.find()) {
            String lt = matcher.group(1);
            String ml = matcher.group(3);
            String rt = matcher.group(5);

            rt = rt.replaceFirst("'", " '^").replaceFirst("'", "'").replace("%", ".*").replace("_",
                    ".");

            String olditem = matcher.group();
            String newitem = coverComparison(lt, ml, rt);
            filter = filter.replace(olditem, newitem);
        }
        return filter;
    }

    /**
     * 查询条件的具体转换方法
     *
     * @return
     */
    public String coverComparison(String lt, String ml, String rt) throws Exception {
        StringBuilder sbr = new StringBuilder();
        ml = ml.trim();
        if ("=".equals(ml)) {
            sbr.append("{").append(lt).append(":").append(rt).append("}");
        } else if ("!=".equals(ml) || "<>".equals(ml)) {
            sbr.append("{").append(lt).append(":{$ne:").append(rt).append("}}");
        } else if (">".equals(ml)) {
            sbr.append("{").append(lt).append(":{$gt:").append(rt).append("}}");
        } else if (">=".equals(ml)) {
            sbr.append("{").append(lt).append(":{$gte:").append(rt).append("}}");
        } else if ("<".equals(ml)) {
            sbr.append("{").append(lt).append(":{$lt:").append(rt).append("}}");
        } else if ("<=".equals(ml)) {
            sbr.append("{").append(lt).append(":{$lte:").append(rt).append("}}");
        } else if ("in".equals(ml)) {
            sbr.append("{").append(lt).append(":{$in:")
                    .append(rt.replace("(", "[").replace(")", "]")).append("}}");
        } else if ("not in".equals(ml)) {
            sbr.append("{").append(lt).append(":{$nin:")
                    .append(rt.replace("(", "[").replace(")", "]")).append("}}");
        } else if ("like".equals(ml)) {
            sbr.append("{").append(lt).append(":{$regex:").append(rt).append("}}");
        }
        return sbr.toString();
    }

    /**
     * 处理查询条件的逻辑嵌套组合
     *
     * @return
     */
    public String toLogical(String chunk) throws Exception {
        chunk = chunk.replace("(", "").replace(")", "");
        chunk = chunk.replace(" and ", "$$and$$").replace(" or ", "$$or$$");
        String[] items = chunk.split("\\$\\$", -1);
        String te = "";
        String cu = "";
        for (String item : items) {
            item = item.trim();
            if (!"".equals(item)) {
                if ("and".equals(item) || "or".equals(item)) {
                    if (!item.equals(te) && !"".equals(te)) {
                        cu = "{$" + te + ":[" + cu + "]}";
                    }
                    te = item;
                } else {
                    if ("".equals(cu)) {
                        cu = item;
                    } else {
                        cu += "," + item;
                    }
                }
            }
        }
        if (!"".equals(te)) {
            cu = "{$" + te + ":[" + cu + "]}";
        }
        return cu;
    }

    /*
     * 梳理方法，通过正则表达式截取字符串
     */
    public String shuffle(String segmentRegExp, int index) throws Exception {
        String res = "";
        Pattern pattern = Pattern.compile(segmentRegExp, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(sql);
        while (matcher.find()) {
            res = matcher.group(index);
        }
        return res;
    }

    /*
     * 梳理方法，通过正则表达式截取字符串
     */
    public String shuffle(String item, String segmentRegExp, int index) throws Exception {
        String res = "";
        Pattern pattern = Pattern.compile(segmentRegExp, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(item.trim());
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            res = matcher.group(index);
            sb.append(res).append("#");
            // matcher.appendReplacement(sb, ",");
        }
        // matcher.appendTail(sb);
        if (sb.length() > 0) {
            res = sb.substring(0, sb.length() - 1);
        }
        return res;
    }

    /**
     * 处理修改方法
     *
     */
    public void update() throws Exception {
        // 判断数据库和表
        command.cmd = "update";
        String items = shuffle("(update) (.+) (set)", 2);
        if (!"".equals(items.trim())) {
            if (items.indexOf(".") == -1) {
                exception = new BaseException(code, "数据源名称不符合接口规范（库名.表名）！");
                throw exception;
            }
            command.cs = items.split("\\.", -1)[0];
            command.cl = items.split("\\.", -1)[1];
        } else {
            exception = new BaseException(code, "未识别到数据源名称（库名.表名）！");
            throw exception;
        }

        // 获取修改字段
        items = shuffle("(update) (.+) (set) (.+?) (where|endsql)", 4);
        if (!"".equals(items)) {
            String[] setItems = items.split(",", -1);
            StringBuilder sets = new StringBuilder();
            for (String setItem : setItems) {
                String[] options = setItem.split("=");
                if (options.length == 2) {
                    sets.append(options[0]).append(":").append(options[1]).append(",");
                } else {
                    exception = new BaseException(code, "未识别到数据修改项！");
                    throw exception;
                }
            }
            if (sets.length() > 0) {
                command.set = BasicDBObject
                        .parse("{$set:{" + sets.substring(0, sets.length() - 1) + "}}");
            }
        }

        // 获取修改条件
        items = shuffle("(update) (.+) (set) (.+) (where) (.+?) (endsql)", 6);
        if (!"".equals(items.trim())) {
            String bsonFilter = where(items);
            try {
                command.filter = BasicDBObject.parse(bsonFilter);
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }
    }

    /**
     * 处理删除
     *
     */
    public void delete() throws Exception {
        command.cmd = "delete";
        String items = shuffle("(delete from) (.+?) (where|endsql)", 2);
        if (!"".equals(items.trim())) {
            if (items.indexOf(".") == -1) {
                exception = new BaseException(code, "数据源名称不符合接口规范（库名.表名）！");
                throw exception;
            }
            command.cs = items.split("\\.", -1)[0];
            command.cl = items.split("\\.", -1)[1];
        } else {
            exception = new BaseException(code, "未识别到数据源名称（库名.表名）！");
            throw exception;
        }

        // 获取删除条件
        items = shuffle("(.+) (where) (.+?) (endsql)", 3);
        if (!"".equals(items.trim())) {
            String bsonFilter = where(items);
            try {
                command.filter = BasicDBObject.parse(bsonFilter);
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }
    }

    /**
     * 处理添加
     *
     */
    public void insert() throws Exception {
        command.cmd = "insert";
        String items = shuffle("(insert into) (.+?)( \\(|\\()", 2);
        if (!"".equals(items.trim())) {
            if (items.indexOf(".") == -1) {
                exception = new BaseException(code, "数据源名称不符合接口规范（库名.表名）！");
                throw exception;
            }
            command.cs = items.split("\\.", -1)[0];
            command.cl = items.split("\\.", -1)[1];
        } else {
            exception = new BaseException(code, "未识别到数据源名称（库名.表名）！");
            throw exception;
        }

        // 获取添加字段
        StringBuilder value = new StringBuilder();
        String[] key = null;
        String[] val = null;
        items = shuffle("(insert into) (.+?)( \\(|\\()(.+?)(\\) values)", 4);
        if (!"".equals(items.trim())) {
            key = items.split(",", -1);
            if (key == null || key.length == 0) {
                exception = new BaseException(code, "添加项字段名称部分语法错误！ [" + items + "]");
                throw exception;
            }
        } else {
            exception = new BaseException(code, "未识别到添加项字段名称！");
            throw exception;
        }

        // 获取字段值
        items = shuffle("(.+) (values)( \\(|\\()(.+?)(\\) endsql)", 4);
        if (!"".equals(items.trim())) {
            String[] vals = items.split(";", -1);
            if (vals != null && vals.length > 0) {
                for (int i = 0; i < vals.length; i++) {
                    val = vals[i].split(",", -1);
                    if (key.length == val.length) {
                        value.append("{");
                        for (int j = 0; j < key.length; j++) {
                            value.append(key[j]).append(":").append(val[j]).append(",");
                        }
                        value.deleteCharAt(value.length() - 1).append("}");
                        command.values.add(BasicDBObject.parse(value.toString()));
                        value.delete(0, value.length());
                    } else {
                        exception = new BaseException(code, "添加项字段值个数与字段个数不匹配！");
                        throw exception;
                    }
                }
            }
        } else {
            exception = new BaseException(code, "未识别到添加项字段值！");
            throw exception;
        }

    }

    public static void main(String[] args) {
        AnalysisSQL ass = new AnalysisSQL();
        ass.setSql("select * from eversec.des_eyyb where x=0");
        try {
            ass.analysis();
            NoSqlCommand nsc = ass.getCommand();
            nsc.showCommand();
        } catch (Exception e) {
            if (e instanceof BaseException) {
                System.out.println(e.getMessage());
            } else {
                e.printStackTrace();
            }
        }
    }
}
