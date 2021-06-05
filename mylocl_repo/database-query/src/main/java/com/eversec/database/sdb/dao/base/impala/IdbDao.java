
package com.eversec.database.sdb.dao.base.impala;

import java.io.IOException;
import java.security.PrivilegedExceptionAction;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.security.UserGroupInformation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import com.eversec.database.sdb.constant.Constant;
import com.eversec.database.sdb.util.LoginUtil;
import com.eversec.database.sdb.util.exceptions.BaseException;
import com.eversec.webbase.config.SpringContextUtils;

@Component
@Order(2)
@ConditionalOnProperty(name = "idc.ismulti.enable", havingValue = "false")
public class IdbDao implements CommandLineRunner {

    private static Logger LOG = LoggerFactory.getLogger(IdbDao.class);

    private static BasicDataSource dataSource;
    private static ApplicationContext appContext;

    @Value("${autoimpala}")
    private Boolean autoimpala;

    @Override
    public void run(String... args) throws Exception {
        LOG.debug("autoimpala:{}", autoimpala);
        String autoInit = Constant.getConfigItem("autoimpala", "false");
        if ("true".equals(autoInit)) {
            initSource();
        }
    }

    private void initSource() {
        String isInit = Constant.getConfigItem("impala", "false");
        LOG.debug("impala 初始化:{}", isInit);
        if ("false".equals(isInit)) {
            return;
        }
        LOG.debug("impala 连接池 dataSource:{}", dataSource);
        if (dataSource == null) {
            appContext = SpringContextUtils.getApplicationContext();
            dataSource = (BasicDataSource) appContext.getBean("dateSourceHive");
            LOG.debug("impala 连接池(上下文) dataSource getApplicationContext:{}", dataSource);
            try {
                dataSource.getConnection();
            } catch (SQLException e) {
                LOG.error("初始化impalal连接池异常 ", e);
            }
        }
    }

    public void getKerberos() {
        Configuration conf = new Configuration();
        String krb5File = "/home/alx_int/ficlient/krb5.conf";
        String keytabFile = "/home/alx_int/ficlient/user.keytab";
        String userName = "hezhilong";
        String zkDefaultLoginName = "Client";
        String zkServerPrincipal = "zookeeper.server.principal";
        String zkDeafultServerPrincipal = "zookeeper/hadoop";
        try {
            LoginUtil.setJaasConf(zkDefaultLoginName, userName, keytabFile);
            LoginUtil.setZookeeperServerPrincipal(zkServerPrincipal, zkDeafultServerPrincipal);
            // Zookeeper登录认证
            LoginUtil.login(userName, keytabFile, krb5File, conf);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Connection getConnection() throws Exception {
        Connection con = null;
        String isenableKerberos =  Constant.getConfigItem("idbc.Kerberos", "false");
        if("true".equalsIgnoreCase(isenableKerberos)){
            appContext = SpringContextUtils.getApplicationContext();
//            LOG.debug("开始带kerberos认证获取数据源");
//            con = (Connection) appContext.getBean("dataSourceHiveWithKerberos");
//            LOG.debug("带kerberos认证获取数据源成功");
            Environment env = appContext.getEnvironment();
        	String conffile = env.getProperty("idbc.Kerberos.conf");
            String kerberosDebug = env.getProperty("idbc.Kerberos.debug");
            String kerberOsUser = env.getProperty("idbc.Kerberos.user");
            String kerberOskeyTab = env.getProperty("idbc.Kerberos.secrtKey");
            System.setProperty("java.security.krb5.conf", conffile);
            System.setProperty("sun.security.krb5.debug", kerberosDebug);
            LOG.info("开始测试Kerberos连接");
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
                    		LOG.error("获取不到类：{}",driverName);
                    	} else {
                    		LOG.info("类名：{}，类所在包：{}",clazz.getName(),clazz.getPackage());
                    	}
                        tcon = DriverManager.getConnection(jdbcUrl);
                        LOG.info("驱动主版本号为{}",tcon.getMetaData().getDriverMajorVersion());
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    return tcon;
                });
            } catch (Exception e) {
                LOG.error(e.toString(),e);
                e.printStackTrace();
            }
            return null;
        }else {
            if (dataSource != null) {
                con = dataSource.getConnection();
            } else {
                BaseException idbException = new BaseException(-1, "impala连接池未初始化！！！！");
                throw idbException;
            }
        }
        return con;
    }

    public static void sourceClose() throws SQLException {
        if (dataSource != null) {
            dataSource.close();
            dataSource = null;
        }

    }

    public static void close(Connection con) throws SQLException {
        if (con != null) {
            con.close();
        }
    }
}
