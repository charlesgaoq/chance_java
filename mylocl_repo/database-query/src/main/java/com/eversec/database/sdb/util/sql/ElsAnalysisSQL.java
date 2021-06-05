
package com.eversec.database.sdb.util.sql;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.eversec.database.sdb.constant.Constant;
import com.eversec.database.sdb.model.mdb.NoSqlCommand;
import com.eversec.database.sdb.util.exceptions.BaseException;
import com.mongodb.BasicDBObject;

/*
 * elasticsearch 2.3.1
 * sql语句解析
 * 错误编码:SQL0011
 */
public class ElsAnalysisSQL {
    private final int code = -90011;
    private String sql;
    private NoSqlCommand command;
    private BaseException exception;
    private Map<String, List<String>> indexToken;

    public ElsAnalysisSQL() {
        // code = 0;
        exception = null;
        command = new NoSqlCommand();
        this.indexToken = new HashMap<String, List<String>>();
    }

    public void setSql(String sql) {
        if (sql != null) {
            this.sql = sql.replaceAll("\\s{1,}", " ").trim() + " endsql";
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
            if (items.trim().toLowerCase().indexOf("count(") == 0) {
                command.cmd = "count";
                command.fileds.put("fileds", items.replace("count(", "").replace(")", "").trim());
            } else if (items.trim().toLowerCase().indexOf("max(") == 0) {
                command.cmd = "max";
                int pp = items.indexOf("max(");
                int ep = items.indexOf(")", pp + 4);
                command.fileds.put("fileds", items.substring(pp + 4, ep));
            } else if (items.trim().toLowerCase().indexOf("min(") == 0) {
                command.cmd = "min";
                int pp = items.indexOf("min(");
                int ep = items.indexOf(")", pp + 4);
                command.fileds.put("fileds", items.substring(pp + 4, ep));
            } else if (items.trim().toLowerCase().indexOf("sum(") == 0) {
                command.cmd = "sum";
                int pp = items.indexOf("sum(");
                int ep = items.indexOf(")", pp + 4);
                command.fileds.put("fileds", items.substring(pp + 4, ep));
            } else if (items.trim().toLowerCase().indexOf("avg(") == 0) {
                command.cmd = "avg";
                int pp = items.indexOf("avg(");
                int ep = items.indexOf(")", pp + 4);
                command.fileds.put("fileds", items.substring(pp + 4, ep));
            } else if (!"*".equals(items.trim())) {
                command.fileds.put("fileds", items.split(",", -1));
                command.cmd = "query";
            } else {
                command.fileds.put("fileds", "");
                command.cmd = "query";
            }
        } else {
            exception = new BaseException(code, "未识别到查询项！");
            throw exception;
        }

        // 判断库和表
        items = shuffle("(select) (.+) (from) (.+?) ", 4);
        items = shuffle("(select) (.+) (from) (.+?) ", 4);
        if (!"".equals(items.trim())) {
            if (items.indexOf(".") == -1) {
                command.cs = items + "_all";
                command.cl = items;
            } else {
                command.cs = items.split("\\.", -1)[0];
                command.cl = items.split("\\.", -1)[1];
            }
        } else {
            exception = new BaseException(code, "未识别到数据源名称（库名.表名）！");
            throw exception;
        }

        // 获取查询条件
        items = shuffle(
                "(select) (.+) (from) (.+) (where) (.+?) (group by|order by|offset|limit|endsql)",
                6);
        if (!"".equals(items.trim())) {
            String bsonFilter = where(items);

            if (command.cs.equals("ysp_log_all")) {
                List<Date> listDates = null;
                String service = "";
                for (Map.Entry<String, List<String>> xn : this.indexToken.entrySet()) {
                    String key = xn.getKey();
                    List<String> val = xn.getValue();
                    if ("day".equals(key)) {
                        Date bg = null;
                        Date eg = null;
                        for (String pp : val) {
                            String[] pps = pp.split("\\|", 2);
                            if ("=".equals(pps[0])) {
                                bg = getDate(pps[1], "yyyyMMdd", 0);
                                eg = getDate(pps[1], "yyyyMMdd", 0);
                            } else if (">".equals(pps[0])) {
                                bg = getDate(pps[1], "yyyyMMdd", 1);
                            } else if (">=".equals(pps[0])) {
                                bg = getDate(pps[1], "yyyyMMdd", 0);
                            } else if ("<".equals(pps[0])) {
                                eg = getDate(pps[1], "yyyyMMdd", 0);
                            } else if ("<=".equals(pps[0])) {
                                eg = getDate(pps[1], "yyyyMMdd", 1);
                            }
                        }
                        if ((bg != null) && (eg != null)) {
                            listDates = getBetweenDates(bg, eg);
                        }
                    } else if ("service".equals(key)) {
                        String[] pps = ((String) val.get(0)).split("\\|", 2);
                        service = pps[1];

                        if ((!"37".equals(service)) && (!"20".equals(service))) {
                            service = "other";
                        }
                    }
                }

                if ((!"".equals(service)) && (listDates != null) && (!listDates.isEmpty())) {
                    StringBuilder strb = new StringBuilder();
                    for (Date date : listDates) {
                        String sDate = Constant.getDateToStr(date, "yyyyMMdd");

                        strb.append(new StringBuilder().append(this.command.cs).append("_")
                                .append(service).append("_").append(sDate).append("*|").toString());
                    }

                    this.command.cs = strb.substring(0, strb.length() - 1);
                } else if ((!"".equals(service))
                        && ((listDates == null) || (listDates.isEmpty()))) {
                    NoSqlCommand tmp1154B1151 = this.command;
                    tmp1154B1151.cs = new StringBuilder().append(tmp1154B1151.cs).append("_")
                            .append(service).append("_*").toString();
                } else if (("".equals(service)) && (listDates != null) && (!listDates.isEmpty())) {
                    StringBuilder strb = new StringBuilder();
                    for (Date date : listDates) {
                        String sDate = Constant.getDateToStr(date, "yyyyMMdd");

                        strb.append(new StringBuilder().append(this.command.cs).append("_*")
                                .append("_").append(sDate).append("*|").toString());
                    }
                    this.command.cs = strb.substring(0, strb.length() - 1);
                }
            } else {
                this.command.cs += "*";
            }

            try {
                if (bsonFilter != null && !"".equals(bsonFilter)) {
                    command.filter = BasicDBObject.parse(bsonFilter);
                }
            } catch (Exception e) {
                throw e;
            }
        }

        if ("count".equals(command.cmd) || "max".equals(command.cmd) || "min".equals(command.cmd)
                || "sum".equals(command.cmd) || "avg".equals(command.cmd)) {
            // 获取分组条件
            items = shuffle(
                    "(select) (.+) (from) (.+) (group by) (.+?) (order by|offset|limit|endsql)", 6);
            if (!"".equals(items.trim())) {
                try {
                    command.groups.put("groups", items);
                } catch (Exception e) {
                    throw e;
                }
            } else {
                command.groups.put("groups", "");
            }
        }

        // 获取排序信息
        items = shuffle("(.+) (order by) (.+?) (offset|limit|endsql)", 3);
        if (!"".equals(items.trim())) {
            String[] sortItems = items.split(",", -1);
            for (String sortItem : sortItems) {
                String[] orders = sortItem.trim().split(" ", -1);
                if (orders.length == 1) {
                    command.sort.put(orders[0], 1);
                } else if (orders.length == 2) {
                    if ("asc".equals(orders[1].toLowerCase().trim())) {
                        command.sort.put(orders[0], 1);
                    } else if ("desc".equals(orders[1].toLowerCase().trim())) {
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

        // 获取offset参数
        items = shuffle("(.+) (offset) (.+?) (limit|endsql)", 3);
        if (!"".equals(items.trim())) {
            try {
                command.skip = Integer.parseInt(items);
            } catch (Exception e) {
                exception = new BaseException(code, "offset 命令参数应该为整数！ [" + items + "]");
                throw exception;
            }
        }

        // 获取limit参数
        items = shuffle("(.+) (limit) (.+?) (offset|endsql)", 3);
        if (!"".equals(items.trim())) {
            try {
                command.limit = Integer.parseInt(items);
            } catch (Exception e) {
                e.printStackTrace();
                exception = new BaseException(code, "limit 命令参数应该为整数！ [" + items + "]");
                throw exception;
            }

            if (command.limit > 200000) {
                exception = new BaseException(code,
                        "limit 最大值 不能超过 200000！[" + command.limit + "]");
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
        filter = filter.replaceAll("\\s+AND\\s+", " and ");
        filter = filter.replaceAll("\\s+OR\\s+", " or ");
        if ("1=1".equals(filter)) {
            filter = "";
        } else {
            filter = filter.replaceFirst("(1=1 and)|(1=1 or)", "");
        }

        filter = specialWhere(filter, 1);

        filter = toComparison(filter);
        String item = shuffle(filter, "(\\([^\\(\\)]*\\))", 1);
        while (!"".equals(item)) {
            for (String chunk : item.split("#&#", -1)) {
                String newChunk = toLogical(chunk);
                filter = filter.replace(chunk, newChunk);
            }
            item = shuffle(filter, "(\\([^\\(\\)]*\\))", 1);
        }
        filter = toLogical(filter);
        filter = specialWhere(filter, -1);
        System.out.println(filter);
        return filter;
    }

    /**
     * 处理字符串中的特殊字符
     *
     * @param filter
     * 过滤的条件语句
     * @param ant
     * 1：转义；-1：还原
     * @return
     */
    public String specialWhere(String filter, int ant) {

        String segmentRegExp = "('.*?')";
        Pattern pattern = Pattern.compile(segmentRegExp, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(filter);
        while (matcher.find()) {
            String valStr = matcher.group(1).trim();

            if (ant == 1) {
                String nitem = valStr.replace("<>", "#ne1#").replace("!=", "#ne2#")
                        .replace(">=", "#gte#").replace("<=", "#lte#").replace(">", "#gt#")
                        .replace("<", "#lt#").replace("=", "#eq#").replace("(", "#lpa#")
                        .replace(")", "#rpa#").replace("{", "#lbr#").replace("}", "#rbr#")
                        .replace("[", "#lsb#").replace("]", "#rsb#");
                filter = filter.replace(valStr, nitem);

                // segmentRegExp = "(\\w+)( *)(>=|<=|<>|!=|>|<|=)( *)(\\w+)";
                // pattern = Pattern.compile(segmentRegExp,
                // Pattern.CASE_INSENSITIVE);
                // matcher = pattern.matcher(valStr);
                // while (matcher.find()) {
                // // String lt = matcher.group(1).trim();
                // String ml = matcher.group(3).trim();
                // // String rt = matcher.group(5).trim();
                // String nml = "";
                // if ("=".equals(ml)) {
                // nml = "#eq#";
                // } else if (">=".equals(ml)) {
                // nml = "#gte#";
                // } else if ("<=".equals(ml)) {
                // nml = "#lte#";
                // } else if ("<>".equals(ml)) {
                // nml = "#ne1#";
                // } else if ("!=".equals(ml)) {
                // nml = "#ne2#";
                // } else if (">".equals(ml)) {
                // nml = "#gt#";
                // } else if ("<".equals(ml)) {
                // nml = "#lt#";
                // }
                //
                // String olditem = matcher.group();
                // // String newitem = lt+" "+ml+" "+rt;
                // filter =
                // filter.replaceFirst(Constant.escapeExprSpecialWord(valStr),
                // Constant.escapeExprSpecialWord(olditem.replace(ml, nml)));
                // }
            } else if (ant == -1) {
                filter = filter.replaceAll("#eq#", "=").replaceAll("#gte#", ">=")
                        .replaceAll("#lte#", "<=").replaceAll("#ne1#", "<>")
                        .replaceAll("#ne2#", "!=").replaceAll("#gt#", ">").replaceAll("#lt#", "<")
                        .replace("#lpa#", "(").replace("#rpa#", ")").replace("#lbr#", "{")
                        .replace("#rbr#", "}").replace("#lsb#", "[").replace("#rsb#", "]");
            }
        }

        return filter;
    }

    /**
     * 处理查询条件的比对命令
     *
     * @return
     */
    public String toComparison(String filter) throws Exception {
        // 过滤a='a1' and b='a2'
        String segmentRegExp = "(\\w+)( *)(>=|<=|<>|!=|>|<|=)( *)'(.*?)'";
        Pattern pattern = Pattern.compile(segmentRegExp, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(filter);
        while (matcher.find()) {
            String lt = matcher.group(1).trim();
            String ml = matcher.group(3).trim();
            String rt = "'" + matcher.group(5).trim() + "'";

            if (("day".equals(lt)) || ("service".equals(lt))) {
                if (this.indexToken.containsKey(lt)) {
                    (this.indexToken.get(lt)).add(new StringBuilder().append(ml).append("|")
                            .append(matcher.group(5).trim()).toString());
                } else {
                    List<String> tt = new ArrayList<String>();
                    tt.add(new StringBuilder().append(ml).append("|")
                            .append(matcher.group(5).trim()).toString());
                    this.indexToken.put(lt, tt);
                }
            }

            String olditem = matcher.group();
            String newitem = coverComparison(lt, ml, rt);
            filter = filter.replaceFirst(Constant.escapeExprSpecialWord(olditem), newitem);
        }
        // 过滤a=1 and b=2
        segmentRegExp = "(\\w+)( *)(>=|<=|<>|!=|>|<|=)( *)(\\w+)";
        pattern = Pattern.compile(segmentRegExp, Pattern.CASE_INSENSITIVE);
        matcher = pattern.matcher(filter);
        while (matcher.find()) {
            String lt = matcher.group(1).trim();
            String ml = matcher.group(3).trim();
            String rt = matcher.group(5).trim();

            if (("day".equals(lt)) || ("service".equals(lt))) {
                if (this.indexToken.containsKey(lt)) {
                    (this.indexToken.get(lt)).add(new StringBuilder().append(ml).append("|")
                            .append(matcher.group(5).trim()).toString());
                } else {
                    List<String> tt = new ArrayList<String>();
                    tt.add(new StringBuilder().append(ml).append("|")
                            .append(matcher.group(5).trim()).toString());
                    this.indexToken.put(lt, tt);
                }
            }

            String olditem = matcher.group();
            String newitem = coverComparison(lt, ml, rt);
            filter = filter.replaceFirst(Constant.escapeExprSpecialWord(olditem), newitem);
        }

        segmentRegExp = "([^\\s\\(]+?)( +)(in|not in)( +)(\\(.+?\\))";
        pattern = Pattern.compile(segmentRegExp, Pattern.CASE_INSENSITIVE);
        matcher = pattern.matcher(filter);
        while (matcher.find()) {
            String lt = matcher.group(1);
            String ml = matcher.group(3);
            String rt = matcher.group(5);

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
            rt = specialWhere(rt, -1);
            rt = Constant.escapeExprSpecialWordSearch(rt);
            rt = specialWhere(rt, 1);
            // rt = rt.replaceFirst("'", " '^").replaceFirst("'",
            // "'").replace("%", ".*").replace("_", ".");
            rt = rt.replaceAll("%", ".*").replace("_", ".");

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
            sbr.append("{").append(lt).append(":").append(Constant.escapeExprSpecialWord(rt))
                    .append("}");
        } else if ("!=".equals(ml) || "<>".equals(ml)) {
            sbr.append("{").append(lt).append(":{\\$ne:").append(Constant.escapeExprSpecialWord(rt))
                    .append("}}");
        } else if (">".equals(ml)) {
            sbr.append("{").append(lt).append(":{\\$gt:").append(Constant.escapeExprSpecialWord(rt))
                    .append("}}");
        } else if (">=".equals(ml)) {
            sbr.append("{").append(lt).append(":{\\$gte:")
                    .append(Constant.escapeExprSpecialWord(rt)).append("}}");
        } else if ("<".equals(ml)) {
            sbr.append("{").append(lt).append(":{\\$lt:").append(Constant.escapeExprSpecialWord(rt))
                    .append("}}");
        } else if ("<=".equals(ml)) {
            sbr.append("{").append(lt).append(":{\\$lte:")
                    .append(Constant.escapeExprSpecialWord(rt)).append("}}");
        } else if ("in".equals(ml)) {
            sbr.append("{").append(lt).append(":{$in:")
                    .append(rt.replace("(", "[").replace(")", "]")).append("}}");
        } else if ("not in".equals(ml)) {
            sbr.append("{").append(lt).append(":{$nin:")
                    .append(rt.replace("(", "[").replace(")", "]")).append("}}");
        } else if ("like".equals(ml)) {
            sbr.append("{").append(lt).append(":{$regexp:").append(rt).append("}}");
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
        chunk = chunk.replace("} and {", "}$#$and$#${").replace("} or {", "}$#$or$#${");
        String[] items = chunk.split("\\$#\\$", -1);
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
            sb.append(res).append("#&#");
            // matcher.appendReplacement(sb, ",");
        }
        // matcher.appendTail(sb);
        if (sb.length() > 0) {
            res = sb.substring(0, sb.length() - 3);
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
                command.cs = items + "_all*";
                command.cl = items;
            } else {
                command.cs = items.split("\\.", -1)[0];
                command.cl = items.split("\\.", -1)[1];
            }
        } else {
            exception = new BaseException(code, "未识别到数据源名称（库名.表名）！");
            throw exception;
        }

        // 获取修改字段
        items = shuffle("(update) (.+) (set) (.+?) (where|endsql)", 4);// 正则匹配
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
                command.cs = items + "_all*";
                command.cl = items;
            } else {
                command.cs = items.split("\\.", -1)[0];
                command.cl = items.split("\\.", -1)[1];
            }
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

    private Date getDate(String sDate, String format, int day) {
        Date date = Constant.getStrToDate(sDate, format);
        if (date == null) {
            return null;
        }
        if (day != 0) {
            Calendar tempStart = Calendar.getInstance();
            tempStart.setTime(date);
            tempStart.add(6, day);
            date = tempStart.getTime();
        }
        return date;
    }

    private List<Date> getBetweenDates(Date start, Date end) {
        List<Date> result = new ArrayList<Date>();
        result.add(start);
        Calendar tempStart = Calendar.getInstance();
        tempStart.setTime(start);
        tempStart.add(6, 1);

        Calendar tempEnd = Calendar.getInstance();
        tempEnd.setTime(end);
        while (tempStart.before(tempEnd)) {
            result.add(tempStart.getTime());
            tempStart.add(6, 1);
        }

        return result;
    }

}
