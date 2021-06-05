
package com.eversec.database.sdb.constant;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

/**
 * 全局常量
 *
 * @author Jony
 */
public class Constant {
    public static final String ERROR_CODE_QUERY = "";

    private static Properties configpro; // config配置文件对象
    private static Set<String> imprtFileSet; // 待导入文件集合

    private static Map<String, String> commands; // sdb接口命令参数项
    private static Map<String, String> cmds; // sdb接口参数项
    private static Map<Integer, String> sdbErrorMsg; // sdb错误码解释

    // --------------------------------------初始化-------------------------------------------------//

    /**
     * sdb接口相关参数初始化
     */
    static {
        commands = new HashMap<String, String>();
        commands.put("matcher", "");
        commands.put("selector", "");
        commands.put("limit", "");
        commands.put("skip", "");
        commands.put("sort", "");
        commands.put("group", "");
        commands.put("datas", "");
        commands.put("modifier", "");
        commands.put("hint", "");
        commands.put("upsert", "");

        commands.put("$gt", "");
        commands.put("$gte", "");
        commands.put("$lt", "");
        commands.put("$lte", "");
        commands.put("$ne", "");
        // commands.put("$et", "");
        commands.put("$in", "");
        commands.put("$regex", "");
        // commands.put("$isnull", "");
        commands.put("$nin", "");
        commands.put("$and", "");
        commands.put("$or", "");
        commands.put("$as", "");

        cmds = new HashMap<String, String>();
        cmds.put("query", "");
        cmds.put("count", "");
        cmds.put("aggregate", "");
        cmds.put("insert", "");
        cmds.put("update", "");
        cmds.put("remove", "");
        cmds.put("removeall", "");
        cmds.put("exec", "");
        cmds.put("save", "");
        // Ken 20181217关闭
        cmds.put("stop", "");

        sdbErrorMsg = new HashMap<Integer, String>();
        sdbErrorMsg.put(-23, "集合不存在！");
        sdbErrorMsg.put(-34, "集合空间不存在！");
    }

    /**
     * config 配置文件对象初始化
     */
    static {
        InputStream is = Constant.class.getClassLoader()
                .getResourceAsStream("application.properties");
        configpro = new Properties();
        try {
            configpro.load(is);
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 待导入文件集合imprtFileSet初始化
     */
    static {
        imprtFileSet = new HashSet<String>();
    }

    // ---------------------------------------------------------------------------------------//
    public static boolean isCommandItem(String item) {

        return commands.containsKey(item);
    }

    public static boolean isCmdItem(String item) {

        return cmds.containsKey(item);
    }

    public static String getSdbError(int code) {
        return sdbErrorMsg.get(code);
    }

    public static String getConfigItem(String key) {
        return configpro.getProperty(key);
    }

    public static String getConfigItem(String key, String defval) {
        return configpro.getProperty(key, defval);
    }

    public static String getConfigItemCur(String key, String defval) { // 每次都重新加载配置文件
        InputStream is = Constant.class.getClassLoader()
                .getResourceAsStream("application.properties");
        Properties configpro = new Properties();
        try {
            configpro.load(is);
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return configpro.getProperty(key, defval);
    }

    public static synchronized void addImprtFileSet(String filePath) {
        synchronized (imprtFileSet) {
            imprtFileSet.add(filePath);
        }
    }

    public static synchronized String getImprtFile() {
        /*
         * String imprtFile = ""; synchronized(imprtFileSet){ if (!imprtFileSet.isEmpty()) {
         * imprtFile =
         * imprtFileSet.iterator().next(); imprtFileSet.remove(imprtFile); } }
         */
        return imprtFileSet.size() + "";
    }

    public static synchronized void reomveImprtFile(String fileName) {
        synchronized (imprtFileSet) {
            imprtFileSet.remove(fileName);
        }
    }

    public static boolean hasImprtFile(String fileName) {
        return imprtFileSet.contains(fileName);
    }

    /**
     * 日期格式转换 字符串
     *
     * @return
     */
    public static String getDateToStr(String format) {
        Date date = new Date();
        return getDateToStr(date, format);
    }

    /**
     * 日期格式转换 字符串
     *
     * @return
     */
    public static String getDateToStr(Date date, String format) {
        SimpleDateFormat formatter = new SimpleDateFormat(format);
        String dateString = formatter.format(date);
        return dateString;
    }

    /**
     * 正则特殊字符 转义
     *
     * @param keyword
     * keyword
     * @return
     */
    public static String escapeExprSpecialWord(String keyword) {
        if (StringUtils.isNotBlank(keyword)) {
            String[] fbsArr = { "\\", "$", "(", ")", "*", "+", ".", "[", "]", "?", "^", "{", "}",
                "|" };
            for (String key : fbsArr) {
                if (keyword.contains(key)) {
                    keyword = keyword.replace(key, "\\" + key);
                }
            }
        }
        return keyword;
    }

    public static String escapeExprSpecialWordSearch(String keyword) {
        if (StringUtils.isNotBlank(keyword)) {
            String[] fbsArr = { "\\", "$", "(", ")", "*", "+", ".", "[", "]", "?", "^", "{", "}",
                "|", "\"", "#", "@", "&", "<", ">", "~" };
            for (String key : fbsArr) {
                if (keyword.contains(key)) {
                    keyword = keyword.replace(key, "\\\\" + key);
                }
            }
        }
        return keyword;
    }

    public static Date getStrToDate(String sDate, String format) {
        Date date = null;
        if (!StringUtils.isBlank(sDate)) {
            SimpleDateFormat parser = new SimpleDateFormat(format);
            try {
                date = parser.parse(sDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return date;
    }
}
