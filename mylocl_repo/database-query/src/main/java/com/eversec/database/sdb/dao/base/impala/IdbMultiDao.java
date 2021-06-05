
package com.eversec.database.sdb.dao.base.impala;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import org.apache.hadoop.conf.Configuration;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.eversec.database.sdb.constant.Constant;
import com.eversec.database.sdb.util.LoginUtil;
import com.eversec.database.sdb.util.exceptions.BaseException;
import com.eversec.webbase.config.SpringContextUtils;

/**
 * @author by hao.chen
 * @Classname IdbMultiDao
 * @Description Ipmala 多数据源访问
 * @Date 2018/10/16 19:43
 */
@Component
@Order(2)
@ConditionalOnProperty(name = "idc.ismulti.enable", havingValue = "true")
public class IdbMultiDao implements CommandLineRunner {
    private static DynamicRoutingDataSource dynamicDataSource;
    private static ApplicationContext appContext;

    @Override
    public void run(String... args) throws Exception {
        String autoInit = Constant.getConfigItem("autoimpala", "false");
        if ("true".equals(autoInit)) {
            initSource();
        }
    }

    private void initSource() {
        String isInit = Constant.getConfigItem("impala", "false");
        if ("false".equals(isInit)) {
            return;
        }
        if (dynamicDataSource == null) {
            appContext = SpringContextUtils.getApplicationContext();
            dynamicDataSource = (DynamicRoutingDataSource) appContext.getBean("dynamicDataSource");
            try {
                // System.out.println("我要认证了，kerberos的认证。。。。");
                // new IdbDao().getKerberos();
                // System.out.println("我认证完了，哈哈哈哈。。。。。");
                dynamicDataSource.getConnection();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void getKerberos() {
        Configuration conf = new Configuration();
        String krb5File = "/home/alx_int/ficlient/krb5.conf";
        String keytabFile = "/home/alx_int/ficlient/user.keytab";
        String userName = "hezhilong";
        /*
         * 第一种方式
         */
        String zkDefaultLoginName = "Client";
        String zkServerPrincipal = "zookeeper.server.principal";
        String zkDeafultServerPrincipal = "zookeeper/hadoop";
        // String krb5File = "/opt/ficlient/krb5.conf";
        // String keytabFile = "/opt/ficlient/user.keytab";
        // String userName = "dengjuan";
        // 设置客户端的keytab和krb5文件路径
        try {
            LoginUtil.setJaasConf(zkDefaultLoginName, userName, keytabFile);
            LoginUtil.setZookeeperServerPrincipal(zkServerPrincipal, zkDeafultServerPrincipal);
            // Zookeeper登录认证
            LoginUtil.login(userName, keytabFile, krb5File, conf);
        } catch (IOException e) {
            e.printStackTrace();
        }
        /*
         * 第二种方式
         */
        // conf.set("username.client.kerberos.principal", "hezhilong@HADOOP.COM");
        // conf.set(HADOOP_SECURITY_AUTHENTICATION, "kerberos");
        // conf.set(HADOOP_SECURITY_AUTHORIZATION, "true");
        // conf.set("username.client.keytab.file", keytabFile);
        //
        // System.setProperty("java.security.krb5.conf", krb5File);
        // System.setProperty("java.security.auth.login.config",
        // "/home/alx_int/ficlient/hezhilong.hive.jaas.conf");
        // try {
        // UserGroupInformation.setConfiguration(conf);
        // SecurityUtil.login(conf, "username.client.keytab.file",
        // "username.client.kerberos.principal");
        // System.out.println("我是getKerberos的人。。。。。。认证完了");
        // } catch (IOException ee) {
        // ee.printStackTrace();
        // System.exit(1);
        // }
    }

    public static Connection getConnection() throws Exception {
        // System.out.println("我要认证了，是在getConnection里的。。");
        // new IdbDao().getKerberos();
        // System.out.println("认证完毕，over。。。");
        Connection con = null;
        if (dynamicDataSource != null) {
            con = dynamicDataSource.getConnection();
        } else {
            BaseException idbException = new BaseException(-1, "impala连接池未初始化！！！！");
            throw idbException;
            // appContext = SpringContextUtils.getApplicationContext();
            // dataSource = (BasicDataSource) appContext.getBean("dataSource");
            // con = dataSource.getConnection();
        }
        return con;
    }

    public static Connection getConnection(String key) throws Exception {
        DynamicDataSourceContextHolder.useDataSource();
        Connection con = null;
        if (dynamicDataSource != null) {
            con = dynamicDataSource.getConnection();
        } else {
            BaseException idbException = new BaseException(-1, "impala连接池未初始化！！！！");
            throw idbException;
            // appContext = SpringContextUtils.getApplicationContext();
            // dataSource = (BasicDataSource) appContext.getBean("dataSource");
            // con = dataSource.getConnection();
        }
        return con;
    }

    public static void sourceClose() throws SQLException {
        if (dynamicDataSource != null) {
            dynamicDataSource = null;
        }

    }

    public static void close(Connection con) throws SQLException {
        if (con != null) {
            con.close();
        }
    }
}
