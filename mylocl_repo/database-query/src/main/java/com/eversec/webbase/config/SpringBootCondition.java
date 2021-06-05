
package com.eversec.webbase.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
//import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
//import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;
//import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

/**
 * 配置是否开启eureka及云服务，如果开启后，那么就开始redis session共享. 根据eureka.enable=false 的值来判断.
 *
 * @author Ken.Zheng
 * @since 2018年1月26日
 */
@Configuration
@ConditionalOnProperty(prefix = "spring.cloud", name = "flag", matchIfMissing = false)
// @EnableZuulProxy
// @EnableDiscoveryClient
// @EnableFeignClients
// @EnableRedisHttpSession
// https://docs.spring.io/spring-session/docs/current/reference/html5/guides/boot.html
public class SpringBootCondition {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    public SpringBootCondition() {
        logger.info("SpringBootApplication初始化");
    }

    // @Bean
    // public JedisConnectionFactory connectionFactory() {
    // return new JedisConnectionFactory();
    // }

}
