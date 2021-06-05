
package com.eversec.database.sdb.dao.base.els;

import java.net.InetAddress;

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
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.RestClientBuilder.HttpClientConfigCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.eversec.database.sdb.constant.Constant;

/**
 * elasticsearch 2.3.1
 *
 * @author Jony
 */
public class ElsDao {

    private static Logger logger = LoggerFactory.getLogger(ElsDao.class);

	public RestHighLevelClient getEsClient() throws Exception {
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
		return restHighLevelClient;
    }

    public void close(Client client) throws Exception {
        if (client != null) {
            client.close();
        }
    }
}
