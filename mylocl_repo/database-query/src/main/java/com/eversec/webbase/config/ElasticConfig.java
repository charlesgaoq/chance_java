
package com.eversec.webbase.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

@Configuration
@EnableElasticsearchRepositories(basePackages = "com.eversec.database.synch.dao")
public class ElasticConfig {

}
