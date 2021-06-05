
package com.eversec.database.sdb.dao.base.impala;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author by hao.chen
 * @Classname DynamicDataSourceContextHolder
 * @Description 该类为数据源上下文配置，用于切换数据源
 * @Date 2018/9/30 10:17
 */
public class DynamicDataSourceContextHolder {
    private static final Logger logger = LoggerFactory
            .getLogger(DynamicDataSourceContextHolder.class);

    private static final String DETAILKEY = "master";
    /**
     * 用于在切换数据源时保证不会被其他线程修改
     */
    private static Lock lock = new ReentrantLock();

    private static int counter = 0;

    /**
     * Maintain variable for every thread, to avoid effect other thread
     */
    private static final ThreadLocal<String> CONTEXT_HOLDER = new ThreadLocal<>();

    /**
     * 所有的数据源集合
     */
    public static List<Object> dataSourceKeys = new ArrayList<>();

    public static void setDataSourceKey(String key) {
        CONTEXT_HOLDER.set(key);
    }

    /**
     * 当切换数据源 通过传递的index进行切换
     */
    public static void useDataSource(int datasourceKeyIndex) {
        lock.lock();
        if (datasourceKeyIndex < 0) {
            datasourceKeyIndex = 0;
        }
        try {
            CONTEXT_HOLDER.set(String.valueOf(dataSourceKeys.get(datasourceKeyIndex)));
        } catch (Exception e) {
            logger.error("Switch slave datasource failed, error message is {}", e.getMessage());
            setDataSourceKey(DETAILKEY);
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    public static void useDataSource(String datasourceKeyIndexStr) {
        int datasourceKeyIndex = Integer.parseInt(datasourceKeyIndexStr);
        useDataSource(datasourceKeyIndex);
    }

    /**
     * 自动切换数据源，没使用一次就按顺序切换下一个数据源
     */
    public static void useDataSource() {
        lock.lock();
        try {
            int datasourceKeyIndex = counter % dataSourceKeys.size();
            CONTEXT_HOLDER.set(String.valueOf(dataSourceKeys.get(datasourceKeyIndex)));
            counter++;
        } catch (Exception e) {
            logger.error("Switch slave datasource failed, error message is {}", e.getMessage());
            setDataSourceKey(DETAILKEY);
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    public static String getDataSourceKey() {
        return CONTEXT_HOLDER.get();
    }

    public static void clearDataSourceKey() {
        CONTEXT_HOLDER.remove();
    }

    public static boolean containDataSourceKey(String key) {
        return dataSourceKeys.contains(key);
    }
}
