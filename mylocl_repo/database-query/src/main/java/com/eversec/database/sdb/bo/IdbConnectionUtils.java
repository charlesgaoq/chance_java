
package com.eversec.database.sdb.bo;

import java.sql.Connection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * IDB/impala查询
 * 
 * @author Ken.Zheng
 * @since 2018年12月17日
 */
public class IdbConnectionUtils {

    private static Logger LOG = LoggerFactory.getLogger(IdbConnectionUtils.class);

    private static Map<String, IdbConnectionBo> taskConnectionMap = new ConcurrentHashMap<>();

    public static IdbConnectionBo getConnection(String taskId) {
        if (StringUtils.isBlank(taskId)) {
            LOG.warn("任务ID为空,无此连接:{}", taskId);
            return null;
        }
        return taskConnectionMap.get(taskId);
    }

    /**
     * 关闭正在运行的impala查询.
     * 如果无此任务对应的连接表示已经关闭了，返回null
     * 如果正常关闭返回true
     * 如果关闭出现异常，返回false.
     * 
     * @param taskId
     * 需要关闭的任务ID
     */
    public static Boolean stopConnection(String taskId) {
        if (StringUtils.isBlank(taskId)) {
            LOG.warn("任务ID:{}为空,不执行停止:", taskId);
            return null;
        }
        IdbConnectionBo bo = taskConnectionMap.get(taskId);
        LOG.debug("{},准备关闭连接:{}", taskId, bo);
        if (bo == null || bo.getHiveCon() == null) {
            return null;
        }
        try {
            if (!bo.getHiveCon().isClosed()) {
                LOG.debug("{} 连接未关闭，现在准备关闭.关闭连接:{}", taskId, bo);
                bo.getHiveCon().close();
                LOG.debug("{} 连接关闭，现在状态:{}", taskId, bo.getHiveCon().isClosed());
            }
            return true;
        } catch (Exception e) {
            LOG.error(taskId + "关闭impala查询连接异常", e);
            return false;
        } finally {
            taskConnectionMap.remove(taskId);
            LOG.debug("{} 连接现缓存:{},当前连接数:{}", taskId, taskConnectionMap.get(taskId),
                    taskConnectionMap.size());
        }
    }

    /**
     * 添加任务与连接关系.
     * 
     * @param taskId
     * 查询任务taskID
     * @param con
     * 连接
     * @return 返回是否连接成功
     */
    public static Boolean addConnection(String taskId, Connection con) {
        if (StringUtils.isBlank(taskId)) {
            LOG.warn("任务ID为空,不需要建立连接:{}", taskId);
            return null;
        }
        LOG.debug("准备添加监控连接:{},{}", taskId, con);
        IdbConnectionBo bo = taskConnectionMap.get(taskId);
        if (bo != null) {
            if (bo.getHiveCon() != null) {
                Boolean foreceStopFlag = stopConnection(taskId);
                LOG.debug("添加监控连接先关闭连接状态:{},{}", taskId, foreceStopFlag);
            }
        }
        bo = new IdbConnectionBo(con);
        taskConnectionMap.put(taskId, bo);
        LOG.debug("添加监控连接:{},{},当前任务数:{}", taskId, bo, taskConnectionMap.size());
        return true;
    }

}
