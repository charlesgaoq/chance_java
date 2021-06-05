
package com.eversec.webbase.config;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 业务配置properties
 *
 * @author Ken.Zheng
 * @since 2018年3月20日
 */
@Configuration
@ConfigurationProperties(prefix = "bussiness")
public class BussinessProperties {

    private String oauthnoFilters; // oauthfilter不过滤的列表

    public String getOauthnoFilters() {
        return oauthnoFilters;
    }

    public void setOauthnoFilters(String oauthnoFilters) {
        this.oauthnoFilters = oauthnoFilters;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

}
