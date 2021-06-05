
package com.eversec.webbase.config;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;

import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.config.FastJsonConfig;
//import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter4;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * http://blog.csdn.net/comven2/article/details/54632959 特别说明:
 * fastjson提供的MessageConverter分为两个版本，
 * 一个是spring4.0以下类名为FastJsonHttpMessageConverter，
 * spring4.0以上使用FastJsonHttpMessageConverter4。
 *
 * @author Ken.Zheng
 * @since 2018年1月9日
 */
@Configuration
public class WebMvcConfiguration extends WebMvcConfigurerAdapter {

    private Charset defaultCharset = Charset.forName("UTF-8");

    // @Bean
    // public MultipartConfigElement multipartConfigElement() {
    // MultipartConfigFactory factory = new MultipartConfigFactory();
    // factory.setMaxFileSize("100MB");
    // factory.setMaxRequestSize("100MB");
    // return factory.createMultipartConfig();
    // }

    /**
     * ?
     */
    // https://gist.github.com/AWolf81/d3ca839e711810345e42
    // https://www.programcreek.com/java-api-examples/index.php?class=or
    // g.springframework.web.servlet.config.annotation.InterceptorRegistry&method=addInterceptor
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(localeChangeInterceptor());
    }

    /**
     * https://www.concretepage.com/spring/resourcebundlemessagesource-spring-example <bean
     * id="messageSource" class="org.springframework.context.support.ResourceBundleMessageSource">
     * <property name="basenames">
     * <list>
     * <value>messages/result-code</value>
     * </list>
     * </property>
     * </bean>
     */
    @Bean
    public MessageSource messageSource() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasenames("messages/result-code");
        messageSource.setDefaultEncoding("UTF-8");
        return messageSource;
    }

    /**
     */
    @Bean(name = "multipartResolver")
    public CommonsMultipartResolver multipartResolver() {
        return new org.springframework.web.multipart.commons.CommonsMultipartResolver();
    }

    /**
     */
    @Bean(name = "localeChangeInterceptor")
    public LocaleChangeInterceptor localeChangeInterceptor() {
        LocaleChangeInterceptor localeChangeInterceptor = new LocaleChangeInterceptor();
        localeChangeInterceptor.setParamName("lang");
        return localeChangeInterceptor;
    }

    @Bean(name = "localeResolver")
    public CookieLocaleResolver localeResolver() {
        CookieLocaleResolver localeResolver = new CookieLocaleResolver();
        Locale defaultLocale = new Locale("zh");
        localeResolver.setDefaultLocale(defaultLocale);
        return localeResolver;
    }

    private static List<MediaType> buildDefaultMediaTypes() {
        List<MediaType> list = new ArrayList<>();
        list.add(MediaType.TEXT_HTML);
        list.add(MediaType.APPLICATION_JSON_UTF8);
        return list;
    }

    /**
     * <bean class="org.springframework.web.servlet.mvc.annotation.AnnotationMethodHandlerAdapter">
     * <property name="messageConverters">
     * <list>
     * <bean class="com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter">
     * <property name="supportedMediaTypes" value="text/html;charset=UTF-8" />
     * <property name="features">
     * <array>
     * <value>WriteMapNullValue</value>
     * <value>WriteNullStringAsEmpty</value>
     * </array>
     * </property>
     * </bean>
     * </list>
     * </property>
     * </bean>
     */
    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(stringHttpMessageConverter());
        converters.add(mappingJackson2HttpMessageConverter());
        // 如果不使用MappingJackson2HttpMessageConverter，可使用如下的转换器
        // spring4.2以上使用如下转换器
        converters.add(fastJsonHttpMessageConverter4());
        // spring4.2以下使用如下转换器
        // converters.add(fastJsonHttpMessageConverter());
        super.extendMessageConverters(converters);
    }

    @Bean
    public StringHttpMessageConverter stringHttpMessageConverter() {
        StringHttpMessageConverter converter = new StringHttpMessageConverter(defaultCharset);
        List<MediaType> list = buildDefaultMediaTypes();
        converter.setSupportedMediaTypes(list);
        return converter;
    }

    /**
     * 创建jackson类型的JsonHttpMessageConverter
     *
     * @return MappingJackson2HttpMessageConverter
     */
    @Bean
    public MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter() {
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        // 此设置使输出json时不显示null类型的字段

        // <value>WriteMapNullValue</value>
        // <value>WriteNullStringAsEmpty</value>

        converter.setObjectMapper(objectMapper);
        List<MediaType> list = buildDefaultMediaTypes();
        converter.setSupportedMediaTypes(list);
        return converter;
    }

    /**
     * spring4.2以后建议采用这个方式创建
     *
     * @return FastJsonHttpMessageConverter4
     */
    @Bean
    public FastJsonHttpMessageConverter4 fastJsonHttpMessageConverter4() {
        FastJsonHttpMessageConverter4 converter = new FastJsonHttpMessageConverter4();
        List<MediaType> list = buildDefaultMediaTypes();
        converter.setSupportedMediaTypes(list);
        FastJsonConfig fastJsonConfig = new FastJsonConfig();
        // fastJsonConfig.setDateFormat("yyyy-MM-dd HH:mm:ss");
        fastJsonConfig.setCharset(defaultCharset);
        // 此处是可变参数可以配置多个
        fastJsonConfig.setSerializerFeatures(SerializerFeature.WriteMapNullValue);
        fastJsonConfig.setSerializerFeatures(SerializerFeature.WriteNullStringAsEmpty);
        converter.setFastJsonConfig(fastJsonConfig);
        return converter;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/favicon.ico")
                .addResourceLocations("classpath:/static/favicon.ico");
        super.addResourceHandlers(registry);
    }

    /**
     * spring4.2以前可以采用这个方式创建
     *
     * @return FastJsonHttpMessageConverter
     */
    // @Bean
    // public FastJsonHttpMessageConverter fastJsonHttpMessageConverter() {
    // FastJsonHttpMessageConverter converter = new FastJsonHttpMessageConverter();
    // FastJsonConfig fastJsonConfig = new FastJsonConfig();
    // fastJsonConfig.setDateFormat("yyyy-MM-dd HH:mm:ss");
    // fastJsonConfig.setCharset(default_charset);
    // // 此处是可变参数可以配置多个
    // fastJsonConfig.setSerializerFeatures(SerializerFeature.WriteDateUseDateFormat);
    // SerializerFeature.WriteMapNullValue此配置加上后会输出null
    // converter.setFastJsonConfig(fastJsonConfig);
    // List<MediaType> list = buildDefaultMediaTypes();
    // converter.setSupportedMediaTypes(list);
    // return converter;
    // }
}
