
package com.eversec.database.sdb.dao.base.impala;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

/**
 * @author by hao.chen
 * @Classname DynamicRoutingDataSource
 * @Description 动态数据源
 * @Date 2018/9/30 10:16
 */
public class DynamicRoutingDataSource extends AbstractRoutingDataSource {
    Logger logger = LoggerFactory.getLogger(DynamicRoutingDataSource.class);

    @Override
    protected Object determineCurrentLookupKey() {
        logger.info("Current DataSource is [{}]",
                DynamicDataSourceContextHolder.getDataSourceKey());
        return DynamicDataSourceContextHolder.getDataSourceKey();
    }
}
