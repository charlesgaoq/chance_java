
package com.eversec.webbase.config;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

/**
 * CSRF（Cross-site request forgery跨站请求伪造，也被称为“One Click Attack”或者Session
 * Riding，通常缩写为CSRF或者XSRF，是一种对网站的恶意利用。
 * 假设登录页面有个输入用户名和密码的输入框，可以有很多Xss/csrf/注入钓鱼网站/SQL等的攻击手段，例如：
 * 输入用户名 : >"'><script>alert(1779)</script> 输入用户名: usera>"'>
 */
public class XssFilter implements Filter {

    FilterConfig filterConfig = null;

    public void init(FilterConfig filterConfig) throws ServletException {
        this.filterConfig = filterConfig;
    }

    public void destroy() {
        this.filterConfig = null;
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        chain.doFilter(new XssHttpServletRequestWrapper((HttpServletRequest) request), response);
    }
}
