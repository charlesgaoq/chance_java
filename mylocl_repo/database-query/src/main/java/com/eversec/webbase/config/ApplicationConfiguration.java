
package com.eversec.webbase.config;

import javax.servlet.DispatcherType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
//import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * 总体配置.
 *
 * @author Ken.Zheng
 * @since 2018年5月9日
 */
@Configuration
@EnableWebMvc
public class ApplicationConfiguration extends WebMvcConfigurerAdapter {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    // @Autowired
    // private EntityManagerFactory entityManagerFactory;

    // @Autowired
    // private BussinessProperties bussinessProperties;

    /**
     * <bean id="springContextHelper" class="com.eversec.trace.util.SpringContextHelper"></bean>
     */
    // @Bean
    // public SpringContextHelper getSpringContextHelper() {
    // return new com.eversec.webbase.framework.core.SpringContextHelper();
    // }

    // @Bean
    // public SessionFactory sessionFactory() {
    // if(entityManagerFactory.unwrap(SessionFactory.class) == null){
    // throw new NullPointerException("factory is not a hibernate factory");
    // }
    // SessionFactory sessionFactory = entityManagerFactory.unwrap(SessionFactory.class);
    // logger.info("sessionFactory is :{}",sessionFactory);
    // return sessionFactory;
    // }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("swagger-ui.html")
                .addResourceLocations("classpath:/META-INF/resources/");

        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");
    }

    // @Override
    // public void addInterceptors(InterceptorRegistry registry) {
    // // 多个拦截器组成一个拦截器链
    // // addPathPatterns 用于添加拦截规则
    // // excludePathPatterns 用户排除拦截
    // registry.addInterceptor(new SqlInjectInterceptor()).addPathPatterns("/**");
    // super.addInterceptors(registry);
    // }

    // @Bean
    // public ServletListenerRegistrationBean servletListenerRegistrationBean(){
    // ServletListenerRegistrationBean servletListenerRegistrationBean =
    // new ServletListenerRegistrationBean();
    // servletListenerRegistrationBean.setListener(
    // new com.eversec.common.utils.springcloud.EurekaRegListener());
    // return servletListenerRegistrationBean;
    // }

    /**
     * Filter是有作用范围的，我们平常都是使用Filter作用于Request，
     * 这也是Filter中dispatcherTypes属性的默认值，
     * 这个属性的意思是由该过滤器管理的资源是通过什么样的方式访问到的，
     * 可以是请求、转发、声明式错误、包含等，但是我们还可以配置使Filter作用于其他范围，这是由dispatcherTypes属性决定的，它有如下几个值：
     * DispatcherType.REQUEST DispatcherType.FORWARD DispatcherType.INCLUDE DispatcherType.ERROR
     * DispatcherType.ASYNC
     */
    // Xss 与SQL注入攻击的过滤器.
    @Bean
    @Order(1)
    public FilterRegistrationBean xssFilterFilterRegistration() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(new XssFilter());
        registration.addUrlPatterns("/*");
        // registration.setDispatcherTypes(DispatcherType.REQUEST);
        // registration.setDispatcherTypes(DispatcherType.ERROR);
        registration.setName("XssFilter");
        registration.setOrder(1);
        logger.info("register Filter :XssFilter {}", registration);
        return registration;
    }

    @Bean
    @Order(2)
    public FilterRegistrationBean springSessionRepositoryFilterFilterRegistration() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(new org.springframework.web.filter.DelegatingFilterProxy());
        registration.addUrlPatterns("/*");
        registration.setDispatcherTypes(DispatcherType.REQUEST);
        registration.setDispatcherTypes(DispatcherType.ERROR);
        registration.setName("springSessionRepositoryFilter");
        registration.setOrder(2);
        logger.info("register Filter :springSessionRepositoryFilter {}", registration);
        return registration;
    }

    @Bean
    @Order(3)
    public FilterRegistrationBean encodingFilterRegistration() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(new org.springframework.web.filter.CharacterEncodingFilter());
        registration.addUrlPatterns("/*");
        registration.addInitParameter("encoding", "UTF-8");
        registration.addInitParameter("forceEncoding", "true");
        registration.setName("encodingFilter");
        registration.setOrder(3);
        logger.info("register Filter :encodingFilter {}", registration);
        return registration;
    }

    // @Bean
    // @Order(3)
    // public FilterRegistrationBean druidWebStatFilterRegistration() {
    // FilterRegistrationBean registration = new FilterRegistrationBean();
    // registration.setFilter(new com.alibaba.druid.support.http.WebStatFilter());
    // registration.addUrlPatterns("/*");
    // registration.addInitParameter("exclusions",
    // ".js,*.gif,*.jpg,*.png,*.css,*.ico,/druid");
    // registration.addInitParameter("profileEnable", "true");
    // registration.setName("DruidWebStatFilter");
    // registration.setOrder(3);
    // logger.info("register Filter :DruidWebStatFilter {}",registration);
    // return registration;
    // }

    @Bean
    @Order(4)
    public FilterRegistrationBean corsFilterRegistration() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(new SimpleCORSFilter());
        registration.addUrlPatterns("/*");
        registration.setName("cors");
        registration.setOrder(4);
        logger.info("register Filter :cors {}", registration);
        return registration;
    }

    /**
     * Ken 2018-03-14 认为此Filter无用，将会在1.1.0中不再使用.
     */
    // @Bean
    // @Order(5)
    // public FilterRegistrationBean sessionFilterRegistration() {
    // FilterRegistrationBean registration = new FilterRegistrationBean();
    // registration.setFilter(new com.eversec.oauth.filter.SessionTimeoutFilter());
    // registration.addUrlPatterns("/*");
    // registration.setName("sessionFilter");
    // registration.setOrder(5);
    // logger.info("register Filter :sessionFilter {}",registration);
    // return registration;
    // }
    @Bean
    @Order(6)
    public FilterRegistrationBean hiddenHttpMethodFilterRegistration() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(new org.springframework.web.filter.HiddenHttpMethodFilter());
        registration.addUrlPatterns("/*");
        registration.addInitParameter("HiddenHttpMethodFilter", "everapp");
        registration.setName("hiddenHttpMethodFilter");
        registration.setOrder(6);
        logger.info("register Filter :hiddenHttpMethodFilter {}", registration);
        return registration;
    }

    // @Bean
    // @Order(7)
    // public FilterRegistrationBean hibernateFilterMethodFilterRegistration() {
    // FilterRegistrationBean registration = new FilterRegistrationBean();
    // registration.setFilter(new org.springframework.orm.hibernate4.support
    // .OpenSessionInViewFilter());
    // registration.addUrlPatterns("/*");
    // registration.addInitParameter("singleSession", "false");
    // registration.setName("hibernateFilter");
    // registration.setOrder(7);
    // logger.info("register Filter :hibernateFilter {}",registration);
    // return registration;
    // }

    // @Bean
    // public ServletRegistrationBean druidServletRegistrationBean() {
    // ServletRegistrationBean bean = new ServletRegistrationBean();
    // bean.setServlet(new com.alibaba.druid.support.http.StatViewServlet());
    // Map<String, String> initParameters = new HashMap<String, String>();
    // initParameters.put("resetEnable", "true");
    // initParameters.put("loginUsername", "druid");
    // initParameters.put("loginPassword", "druid");
    // bean.setInitParameters(initParameters);
    // return new ServletRegistrationBean(new com.alibaba.druid.support.http.StatViewServlet(),
    // "/druid/*");// ServletName默认值为首字母小写，即myServlet
    // }

    // @Bean
    // public ServletRegistrationBean dispatcherRegistration
    // (DispatcherServlet dispatcherServlet) {
    // ServletRegistrationBean registration = new ServletRegistrationBean(dispatcherServlet);
    // registration.getUrlMappings().clear();
    // registration.addUrlMappings("*.do");
    // registration.addUrlMappings("*.json");
    // return registration;
    // }

}
