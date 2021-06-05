
package com.eversec.database.sdb.dao.base.els;

import com.eversec.database.sdb.constant.Constant;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ObjectUtils;

import java.net.InetAddress;

public class OtherElsDao {
	
	private static Logger LOG = LoggerFactory.getLogger(OtherElsDao.class);
	
    @SuppressWarnings("resource")
	public Client getEsClient() throws Exception {
        Client client = null;
        String clustername = Constant.getConfigItem("otherClustername");
        String ip = Constant.getConfigItem("otherEsip", "");
        String port = Constant.getConfigItem("otherEsport", "");
        String userPasswd = Constant.getConfigItem("es.userpasswd",null);
        String sslEnable = Constant.getConfigItem("es.xpack.ssl.enable","false");
        String[] ips = ip.split(",", -1);
//        InetSocketTransportAddress[] ta = new InetSocketTransportAddress[ips.length];
//        for (int i = 0; i < ips.length; i++) {
//            ta[i] = new InetSocketTransportAddress(InetAddress.getByName(ips[i]),
//                    Integer.parseInt(port) + 100);
//        }
        TransportAddress[] ta = new TransportAddress[ips.length];
        for (int i = 0;i < ips.length;i++) {
        	ta[i] = new TransportAddress(InetAddress.getByName(ips[i]), Integer.parseInt(port) + 100);
        }
        Settings settings = null;
        LOG.info("开始进行客户端设置，userPaswd:{}",userPasswd);
        if (ObjectUtils.isEmpty(userPasswd)){
//            settings = Settings.settingsBuilder().put("cluster.name", clustername)
//                    .put("client.transport.sniff", true).build();
            settings = Settings.builder().put("cluster.name", clustername)
            		.put("client.transport.sniff", true).build();
        } else if (!ObjectUtils.isEmpty(userPasswd)){
        	LOG.info("设置用户名密码：{}",userPasswd);
//            settings = Settings.settingsBuilder().put("cluster.name", clustername)
//                    .put("xpack.security.transport.ssl.enabled",true)
//                    .put("xpack.security.user",userPasswd)
//                    .put("client.transport.sniff", true).build();
        	LOG.info("开始构建settings。。。");
        	settings = Settings.builder().put("cluster.name", clustername)
                  .put("xpack.security.transport.ssl.enabled",Boolean.getBoolean(sslEnable))
                  .put("xpack.security.user",userPasswd)
                  .put("client.transport.sniff", true).build();
        	LOG.info("settings构建完成");
        }
        if (ObjectUtils.isEmpty(settings)){
            throw new Exception("ES settings is null");
        }
        try {
        	LOG.info("开始构建client.....");
//			client = TransportClient.builder().settings(settings).build().addTransportAddresses(ta);
        	client = new PreBuiltTransportClient(settings).addTransportAddresses(ta);
			LOG.info("client构建成功......");
		} catch (Exception e) {
			close(client);
			LOG.error("获取es client 出错：{}",e.getMessage());
			e.printStackTrace();
			throw e;
		} 
        return client;
    }

    public void close(Client client) throws Exception {
        if (client != null) {
            client.close();
        }
    }

}
