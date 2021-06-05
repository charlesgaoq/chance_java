
package com.eversec;

import java.net.InetAddress;
import java.security.PrivilegedExceptionAction;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.NodeSelector;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestClientBuilder.HttpClientConfigCallback;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.elasticsearch.xpack.client.PreBuiltXPackTransportClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
//import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.data.elasticsearch.ElasticsearchAutoConfiguration;
import org.springframework.boot.autoconfigure.data.elasticsearch.ElasticsearchDataAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.servlet.ServletComponentScan;
//import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
//import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Controller;
import org.springframework.util.ObjectUtils;

import com.eversec.database.sdb.constant.Constant;
import com.eversec.database.sdb.dao.base.impala.DynamicDataSourceContextHolder;
import com.eversec.database.sdb.dao.base.impala.DynamicRoutingDataSource;
import com.eversec.database.sdb.util.exceptions.DataSourceInitException;

/**
 * 系统入口.jar启动.
 */

@ServletComponentScan
@EnableAsync
@EnableScheduling
@SpringBootApplication(exclude = {MongoAutoConfiguration.class,ElasticsearchAutoConfiguration.class,ElasticsearchDataAutoConfiguration.class}, scanBasePackages = { "com.eversec" })
public class CommonModuleApplication {

    private static Logger logger = LoggerFactory.getLogger(CommonModuleApplication.class);

    @Autowired
    private Environment env;

    public static void main(String[] args) {
        // ApplicationContext ctx =
        SpringApplication.run(CommonModuleApplication.class, args);
        // SpringContextHelper.setApplicationContext(ctx);
        logger.info("Server Startuped 查询接口服务已启动 ");
    }

    @Bean
    public RestHighLevelClient restClient() throws Exception {
		RestClientBuilder restClientBuilder;
		String ip = Constant.getConfigItem("esip", "");
		String httpPort = Constant.getConfigItem("esport", "9200");
		String auth = Constant.getConfigItem("esAuth", "false");
		String username = Constant.getConfigItem("esUser", "");
		String password = Constant.getConfigItem("esPwd", "");
        String[] ips = ip.split(",", -1);
        HttpHost[] hosts = new HttpHost[ips.length];
		if (ips != null) {
			int i = 0;
			for (String host : ips) {
				hosts[i] = new HttpHost(host, Integer.valueOf(httpPort));
				i++;
			}
		}
		restClientBuilder = RestClient.builder(hosts);
		restClientBuilder.setNodeSelector(NodeSelector.SKIP_DEDICATED_MASTERS);
		
		// 是否开启鉴权
		if ("true".equals(auth)) {
			final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
			/** 设置账号密码 */
			credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(username, password));

			/** 创建rest client对象 */
			restClientBuilder.setHttpClientConfigCallback(new HttpClientConfigCallback() {
				@Override
				public HttpAsyncClientBuilder customizeHttpClient(HttpAsyncClientBuilder httpClientBuilder) {
					return httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider);
				}
			});
		}
		restClientBuilder.setRequestConfigCallback(new RestClientBuilder.RequestConfigCallback() {
			@Override
			public RequestConfig.Builder customizeRequestConfig(RequestConfig.Builder requestConfigBuilder) {
				requestConfigBuilder.setConnectionRequestTimeout(30000);
				requestConfigBuilder.setConnectTimeout(45000);
				return requestConfigBuilder.setSocketTimeout(60000);
			}
		});
		RestHighLevelClient restHighLevelClient = new RestHighLevelClient(restClientBuilder);
		logger.debug("初始化 es 客户端bean成功");
		return restHighLevelClient;
		
    }
    
    
    @Bean
    @ConditionalOnProperty(name = "idc.ismulti.enable", havingValue = "false")
    @ConfigurationProperties(prefix = "application")
    public BasicDataSource dateSourceHive() {
        BasicDataSource dataSource = new BasicDataSource();
        logger.debug("idbc.url 地址::{}", env.getProperty("idbc.url"));
        logger.debug("env 环境:{}", env);
        dataSource.setUrl(env.getProperty("idbc.url"));
        dataSource.setUsername(env.getProperty("idbc.username"));
        dataSource.setPassword(env.getProperty("idbc.password"));
        dataSource.setDriverClassName(env.getProperty("idbc.driverClassName"));
        dataSource.setInitialSize(Integer.parseInt(env.getProperty("idbc.initialSize")));
        dataSource.setMaxActive(Integer.parseInt(env.getProperty("idbc.maxActive")));
        dataSource.setMaxIdle(Integer.parseInt(env.getProperty("idbc.maxIdle")));
        dataSource.setMinIdle(Integer.parseInt(env.getProperty("idbc.minIdle")));
        dataSource.setValidationQuery(env.getProperty("idbc.validationQuery"));
        dataSource.setTestWhileIdle(Boolean.parseBoolean(env.getProperty("idbc.testWhileIdle")));
        dataSource.setTimeBetweenEvictionRunsMillis(
                Long.parseLong(env.getProperty("idbc.timeBetweenEvictionRunsMillis")));
        dataSource.setMinEvictableIdleTimeMillis(
                Long.parseLong(env.getProperty("idbc.minEvictableIdleTimeMillis")));
        dataSource.setTestOnBorrow(Boolean.parseBoolean(env.getProperty("idbc.testOnBorrow")));
        logger.debug("dateSourceHive: init ok :{}", dataSource);

        return dataSource;
    }


    @Bean
    @ConditionalOnProperty(name = "idbc.Kerberos", havingValue = "true")
    @ConfigurationProperties(prefix = "application")
    public Connection dataSourceHiveWithKerberos() {
        String conffile = env.getProperty("idbc.Kerberos.conf");
        String kerberosDebug = env.getProperty("idbc.Kerberos.debug");
        String kerberOsUser = env.getProperty("idbc.Kerberos.user");
        String kerberOskeyTab = env.getProperty("idbc.Kerberos.secrtKey");
        System.setProperty("java.security.krb5.conf", conffile);
        System.setProperty("sun.security.krb5.debug", kerberosDebug);
        logger.info("开始测试Kerberos连接");
        Configuration conf=new Configuration();
        conf.set("hadoop.security.authentication", "Kerberos");
        String jdbcUrl=env.getProperty("idbc.url");
        String driverName = env.getProperty("idbc.driverClassName", "org.apache.hive.jdbc.HiveDriver");
        try {
            UserGroupInformation.setConfiguration(conf);
            UserGroupInformation ugi =
                    UserGroupInformation
                            .loginUserFromKeytabAndReturnUGI(kerberOsUser, kerberOskeyTab);
            return ugi.doAs((PrivilegedExceptionAction<Connection>) () -> {
                Connection tcon = null;
                try {
                    // 父类的getConnection(long)方法
                	Class clazz = Class.forName(driverName);
                	if (ObjectUtils.isEmpty(clazz)) {
                		logger.error("获取不到类：{}",driverName);
                	} else {
                		logger.info("类名：{}，类所在包：{}",clazz.getName(),clazz.getPackage());
                	}
                    tcon = DriverManager.getConnection(jdbcUrl);
                    logger.info("驱动主版本号为{}",tcon.getMetaData().getDriverMajorVersion());
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                return tcon;
            });
        } catch (Exception e) {
            logger.error(e.toString(),e);
            e.printStackTrace();
        }
        return null;
    }

    @Bean("dynamicDataSource")
    @ConditionalOnProperty(name = "idc.ismulti.enable", havingValue = "true")
    @ConfigurationProperties(prefix = "application")
    public DynamicRoutingDataSource dynamicDataSource() {
        DynamicRoutingDataSource dynamicRoutingDataSource = new DynamicRoutingDataSource();
        try {
            Map<Object, Object> dataSourceMap = buildDataSourceMap();
            dynamicRoutingDataSource.setDefaultTargetDataSource(dataSourceMap.get("1"));
            dynamicRoutingDataSource.setTargetDataSources(dataSourceMap);

            // 将数据源的 key 放到数据源上下文的 key 集合中，用于切换时判断数据源是否有效
            DynamicDataSourceContextHolder.dataSourceKeys.addAll(dataSourceMap.keySet());
            // 设置默认的数据源为 1
            DynamicDataSourceContextHolder.setDataSourceKey("1");
            logger.debug("dynamicDataSource: init ok :{}", dynamicRoutingDataSource);
        } catch (DataSourceInitException ex) {
            logger.error("dynamicDataSource: error :{}", ex);
        }
        return dynamicRoutingDataSource;
    }

    private Map<Object, Object> buildDataSourceMap() throws DataSourceInitException {
        Map<Object, Object> dataSourceMap = null;
        try {
            int datasourcecount = Integer.parseInt(env.getProperty("idbc.connect.count"));
            dataSourceMap = new HashMap<>(datasourcecount);
            if (datasourcecount <= 0) {
                throw new DataSourceInitException("数据源个数必须大于等于1");
            }
            for (int i = 1; i <= datasourcecount; i++) {
                BasicDataSource dataSource = buildDataSource();
                String username = env.getProperty("idbc.username" + i);
                String password = env.getProperty("idbc.password" + i);
                String url = env.getProperty("idbc.url" + i);
                if (StringUtils.isNotBlank(username)) {
                    dataSource.setUsername(username);
                }
                if (StringUtils.isNotBlank(password)) {
                    dataSource.setPassword(password);
                }
                dataSource.setUrl(url);
                dataSourceMap.put(String.valueOf(i), dataSource);
            }
        } catch (Exception e) {
            throw new DataSourceInitException("初始化多数据源时候错误");
        }
        return dataSourceMap;
    }

    /**
     * 加载共性的属性构建一个impala的datasource对象
     */
    private BasicDataSource buildDataSource() {
        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setDriverClassName(env.getProperty("idbc.driverClassName"));
        dataSource.setInitialSize(Integer.parseInt(env.getProperty("idbc.initialSize")));
        dataSource.setMaxActive(Integer.parseInt(env.getProperty("idbc.maxActive")));
        dataSource.setMaxIdle(Integer.parseInt(env.getProperty("idbc.maxIdle")));
        dataSource.setMinIdle(Integer.parseInt(env.getProperty("idbc.minIdle")));
        dataSource.setValidationQuery(env.getProperty("idbc.validationQuery"));
        dataSource.setTestWhileIdle(Boolean.parseBoolean(env.getProperty("idbc.testWhileIdle")));
        dataSource.setTimeBetweenEvictionRunsMillis(
                Long.parseLong(env.getProperty("idbc.timeBetweenEvictionRunsMillis")));
        dataSource.setMinEvictableIdleTimeMillis(
                Long.parseLong(env.getProperty("idbc.minEvictableIdleTimeMillis")));
        dataSource.setTestOnBorrow(Boolean.parseBoolean("idbc.testOnBorrow"));
        return dataSource;
    }

}
