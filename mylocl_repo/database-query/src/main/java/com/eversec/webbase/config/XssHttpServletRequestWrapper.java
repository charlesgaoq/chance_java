
package com.eversec.webbase.config;

import java.util.Arrays;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Xss 与SQL注入攻击的过滤器.
 *
 * @author Ken.Zheng
 * @since 2018年5月8日
 */
public class XssHttpServletRequestWrapper extends HttpServletRequestWrapper {

    static String[] SQL_INJECT = "'| or | and |;|--|+| like ".split("\\|");
    // static String SQL_INJECT [] = "'| or | and |;|--|+|,| like ".split("\\|");
    static String SQL_INJECT_STR = Arrays.asList(SQL_INJECT).toString();
    static Logger LOG = LoggerFactory.getLogger(XssHttpServletRequestWrapper.class);
    static Logger LOG_ERRORATTACK = LoggerFactory.getLogger("errorattack");

    public XssHttpServletRequestWrapper(HttpServletRequest servletRequest) {
        super(servletRequest);
    }

    public String[] getParameterValues(String parameter) {
        String[] values = super.getParameterValues(parameter);
        if (values == null) {
            return null;
        }
        int count = values.length;
        String[] encodedValues = new String[count];
        for (int i = 0; i < count; i++) {
            encodedValues[i] = clean(parameter, values[i]);
        }
        return encodedValues;
    }

    public String getParameter(String parameter) {
        String value = super.getParameter(parameter);
        if (value == null) {
            return null;
        }
        return clean(parameter, value);
    }

    public String getHeader(String name) {
        String value = super.getHeader(name);
        if (value == null) {
            return null;
        }
        return clean(name, value);
    }

    private String clean(String parameter, String value) {
        return cleanSqlInjection(parameter, cleanXSS(value));
    }

    /**
     * Ken 20180508暂时屏蔽
     */
    private String cleanXSS(String value) {
        // You'll need to remove the spaces from the html entities below
        // value = value.replaceAll("<", "& lt;").replaceAll(">", "& gt;");
        // value = value.replaceAll("\\(", "& #40;").replaceAll("\\)", "& #41;");
        // value = value.replaceAll("'", "& #39;"); //显示单引号(')，我们必须这样写：&apos; 或 &#39;
        // value = value.replaceAll("eval\\((.*)\\)", "");
        // value = value.replaceAll("[\\\"\\\'][\\s]*javascript:(.*)[\\\"\\\']", "\"\"");
        // value = value.replaceAll("script", "");
        return value;
    }

    // 校验SQL
    protected static String cleanSqlInjection(String name, String value) {
        // 统一转为小写
        // String str = value.toLowerCase();
        // for (int i = 0; i < SQL_INJECT.length; i++) {
        // // 检索
        // if (str.indexOf(SQL_INJECT[i]) >= 0) {
        // String tmp = value;
        // value = str.replaceAll(SQL_INJECT[i], "");//一旦命中规则，则开始value为全小写转换了
        // str = value;//重置串
        // }
        // }
        return value;
    }

}
