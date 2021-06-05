
package com.eversec.database.sdb.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.eversec.database.sdb.bo.IdbConnectionUtils;
import com.eversec.database.sdb.constant.Constant;
import com.eversec.database.sdb.dao.impala.IdbDaoExec;
import com.eversec.database.sdb.dao.impala.IdbDaoExecute;
import com.eversec.database.sdb.dao.impala.IdbDaoQuery;
import com.eversec.database.sdb.dao.impala.IdbDaoSave;
import com.eversec.database.sdb.model.request.ReqParamterIdb;
import com.eversec.database.sdb.model.rmessage.ExceptionRMessage;
import com.eversec.database.sdb.model.rmessage.RMessage;
import com.eversec.database.sdb.model.rmessage.StopRMessage;
import com.eversec.database.sdb.model.rmessage.TaskRMessage;
import com.eversec.database.sdb.util.exceptions.BaseException;
import com.eversec.database.sdb.util.idbtask.IdbTaskThreadUtil;
import com.eversec.database.sdb.util.idbtask.IdbTaskThreadUtilEs;
import com.eversec.database.sdb.util.idbtask.IdbTaskUtil;
import com.eversec.database.sdb.util.idbtask.IdbTaskUtilEs;

/**
 * Impala 数据库业务类 错误编码：-21000
 *
 * @author Jony
 */
@Service("idbService")
public class IdbService {

    @Resource
    private ElsService elsService;

    private IdbDaoExecute iexec;

    private int errorCode = -21000;

    private IdbDaoSave ids;

    private static final Logger LOG = LoggerFactory.getLogger(IdbService.class);

    public RMessage save(ReqParamterIdb rpi) {
        RMessage rMessage = null;
        try {
            ids = new IdbDaoSave();
            String sql = rpi.getCommand();
            String taskid = rpi.getTaskid();
            if ("".equals(taskid)) {
                taskid = "unname";
            }
            rMessage = (RMessage) ids.save(sql, taskid);
        } catch (Exception e) {
            LOG.error("保存idb异常", e);
            rMessage = new ExceptionRMessage();
            if (e instanceof BaseException) {
                BaseException be = (BaseException) e;
                rMessage.code = be.getCode();
                rMessage.exception = be.getMessage();
            } else {
                rMessage.code = -1;
                rMessage.exception = e.getMessage();
            }
        }
        return rMessage;
    }

    public RMessage query(ReqParamterIdb rpi) {
        RMessage rMessage = null;
        try {
            iexec = new IdbDaoQuery();
            String sql = rpi.getCommand();
            rMessage = (RMessage) iexec.execute(rpi.getTaskid(), sql);
        } catch (Exception e) {
            LOG.error("查询idb异常", e);
            rMessage = new ExceptionRMessage();
            if (e instanceof BaseException) {
                BaseException be = (BaseException) e;
                rMessage.code = be.getCode();
                rMessage.exception = be.getMessage();
            } else {
                rMessage.code = -1;
                rMessage.exception = e.getMessage();
            }
        }
        return rMessage;
    }

    /**
     * 停止idb查询.
     */
    public RMessage stop(ReqParamterIdb rpi) {
        RMessage rMessage = new StopRMessage();
        rMessage.cmd = "stop";
        try {
            //
            if (StringUtils.isBlank(rpi.getTaskid())) {
                rMessage.code = -1;
                rMessage.exception = "请输入合法的任务ID";
                return rMessage;
            }
            RMessage taskInfoMessage = elsService
                    .service("select * from idbtaskinfo where taskid = '" + rpi.getTaskid() + "'");
            // 如果已经有了任务，一般情况是有的
            // datas [{resultcs=idbtaskresult_all, level=1, taskdate=20181014, count=0,
            // resultcl=idbtaskresult, state=4, _id=AWZyuovXl2AG0nz-45XF, command= SELECT * FROM tt
            // limit 100, taskid=05560e7ea51d4ac888d8f8ec8de89d67, usetime=89}]
            if (taskInfoMessage == null || taskInfoMessage.total != 0) {
                rMessage.code = -1;
                rMessage.exception = "请输入合法的任务ID";
                return rMessage;
            }
            String result = taskInfoMessage.toJson();
            JSONObject jsonObject = JSONObject.parseObject(result);
            JSONArray jsonArray = jsonObject.getJSONArray("datas");
            if (jsonArray == null || jsonArray.size() < 1) {
                rMessage.code = -1;
                rMessage.exception = "任务ID未检索到相关任务";
                return rMessage;
            }
            // 有任务,检查任务的状态
            JSONObject tempJsonObj = jsonArray.getJSONObject(0);
            Integer taskState = tempJsonObj.getInteger("state");
            LOG.debug("{},ES任务表中的状态:{}", rpi.getTaskid(), taskState);
            // ES任务表state:0是新增,1是查询中,2是查询完成, 3是查询异常,4是查询完成无结果
            if (taskState == null || taskState == 2 || taskState == 3 || taskState == 4) {
                LOG.debug("{},ES任务表中的状态无或已执行,无需要再停止:{}", rpi.getTaskid(), taskState);
                rMessage.code = -1;
                rMessage.exception = "查询任务已执行完,无法操作。";
                return rMessage;
            }
            if (taskState == 1) {
                Boolean stopFlag = IdbConnectionUtils.stopConnection(rpi.getTaskid());
                LOG.info("任务查询中，现停止查询状态:{}", rpi.getTaskid(), stopFlag);
                clearByTaskId(rpi.getTaskid());
            } else if (taskState == 0) {
                LOG.info("任务尚未开始查询，现删除任务:{}", rpi.getTaskid());
                clearByTaskId(rpi.getTaskid());

            }
        } catch (Exception e) {
            LOG.error("停止idb异常", e);
            rMessage = new ExceptionRMessage();
            if (e instanceof BaseException) {
                BaseException be = (BaseException) e;
                rMessage.code = be.getCode();
                rMessage.exception = be.getMessage();
            } else {
                rMessage.code = -1;
                rMessage.exception = e.getMessage();
            }
        }
        return rMessage;
    }

    public boolean clearByTaskId(String taskId) {
        RMessage taskInfoDeleteMessage = elsService
                .service("delete from idbtaskinfo where taskid = '" + taskId + "'");
        LOG.info("{},删除任务表状态:{}", taskId, taskInfoDeleteMessage.toJson());

        // if (taskInfoDeleteMessage == null || taskInfoDeleteMessage.code != 0) {
        // }
        clearResultByTaskId(taskId);
        return true;
    }

    public boolean clearResultByTaskId(String taskId) {
        RMessage taskResultDeleteMessage = elsService
                .service("delete from idbtaskresult where taskid = '" + taskId + "'");
        LOG.info("{},删除结果表状态:{}", taskId, taskResultDeleteMessage.toJson());
        return true;
    }

    public RMessage exec(ReqParamterIdb rpi) {
        RMessage rMessage = null;
        try {
            iexec = new IdbDaoExec();
            String sql = rpi.getCommand();
            rMessage = (RMessage) iexec.execute(rpi.getTaskid(), sql);
        } catch (Exception e) {
            LOG.error("exec idb异常", e);
            rMessage = new ExceptionRMessage();
            if (e instanceof BaseException) {
                BaseException be = (BaseException) e;
                rMessage.code = be.getCode();
                rMessage.exception = be.getMessage();
            } else {
                rMessage.code = -1;
                rMessage.exception = e.getMessage();
            }
        }
        return rMessage;
    }

    /**
     * 非实时查询
     *
     * @return
     */
    // -----------------------------mongodb-----------------------------------//
    // public SdbResponseMessage queryTask(ReqParamterIdb rpi) {
    // SdbResponseMessage srm = new SdbResponseMessage();
    // try {
    // String cs = Constant.getConfigItem("taskCS");
    // String cl = Constant.getConfigItem("idbTaskResultCL");
    // String cl_i = Constant.getConfigItem("idbTaskInfoCL");
    //
    // String taskdate = Constant.getDateToStr("yyyyMMdd");
    //
    // Map<String, Object> data = new HashMap<String, Object>();
    // data.put("resultcs", cs);
    // data.put("resultcl", cl);
    // data.put("command", rpi.getCommand());
    // data.put("taskid", rpi.getTaskid());
    // data.put("state", 0);
    // data.put("taskdate", taskdate);
    // data.put("level", rpi.getLevel());
    // IdbTaskUtil util = new IdbTaskUtil();
    // util.saveTaskInfo(cs, cl_i, data);
    //
    // Map<String, String> map = new HashMap<String, String>();
    // map.put("cs", cs);
    // map.put("cl", cl);
    // map.put("taskid", rpi.getTaskid());
    // map.put("taskdate", taskdate);
    // srm.datas = map;
    // srm.setTotal(0);
    // srm.setCmd("query");
    // } catch (Exception e) {
    // srm.setCode(errorCode - 2);
    // srm.setExceptions(e.getMessage());
    // e.printStackTrace();
    // }
    // return srm;
    // }

    // -------------------------------elasticsearch----------------------------------------//
    public RMessage queryTask(ReqParamterIdb rpi) {
        LOG.debug("下发查询任务到ES任务:{}", rpi);
        TaskRMessage srm = new TaskRMessage();
        try {
            if (StringUtils.isBlank(rpi.getTaskid())) {
                srm.code = errorCode - 2;
                srm.exception = "请输入合法的任务ID";
                return srm;
            }

            try {
                boolean cleanFlag = false;
                RMessage taskInfoMessage = elsService.service(
                        "select * from idbtaskinfo where taskid = '" + rpi.getTaskid() + "'");
                if (taskInfoMessage != null && StringUtils.isNotBlank(taskInfoMessage.toJson())) {
                    JSONObject jsonObject = JSONObject.parseObject(taskInfoMessage.toJson());
                    if (null != jsonObject) {
                        JSONArray jsonArray = jsonObject.getJSONArray("datas");
                        if (jsonArray != null && jsonArray.size() >= 1) {
                            LOG.error("下发查询任务到ES任务时发现已有此任务:{},预先清理:{}", rpi.getTaskid(), rpi);
                            clearByTaskId(rpi.getTaskid());
                            cleanFlag = true;
                        }
                    }
                }
                if (!cleanFlag) {
                    LOG.info("下发查询任务到ES任务时无任务:{},预先清理结果表避免脏数据:{}", rpi.getTaskid(), rpi);
                    clearByTaskId(rpi.getTaskid());
                }
            } catch (Exception e) {
                LOG.error("下发查询任务到ES任务异常", e);
            }

            String csr = Constant.getConfigItem("idbTaskResultCS");
            String csi = Constant.getConfigItem("idbTaskInfoCS");
            String clr = Constant.getConfigItem("idbTaskResultCL");
            String cli = Constant.getConfigItem("idbTaskInfoCL");

            String taskdate = Constant.getDateToStr("yyyyMMdd");

            Map<String, Object> data = new HashMap<String, Object>();
            data.put("resultcs", csr);
            data.put("resultcl", clr);
            data.put("command", rpi.getCommand());
            data.put("taskid", rpi.getTaskid());
            data.put("state", 0);
            data.put("taskdate", taskdate);
            data.put("level", rpi.getLevel());
            data.put("count", 0);
            IdbTaskUtilEs util = new IdbTaskUtilEs();
            util.saveTaskInfo(csi, cli, data);

            LOG.debug("下发查询任务到ES任务OK:taskdate,{},{}", taskdate, rpi);
            Map<String, String> map = new HashMap<String, String>();
            map.put("cs", csr);
            map.put("cl", clr);
            map.put("taskid", rpi.getTaskid());
            map.put("taskdate", taskdate);
            srm.datas = map;
            srm.cmd = "query";
        } catch (Exception e) {
            srm.code = errorCode - 2;
            srm.exception = e.getMessage();
            LOG.error("下发查询任务到ES异常", e);
        }
        return srm;
    }

    // ----------------mongodb----------------------------------//
    // @Scheduled(cron = "0 0/5 * * * ?")
    public void executeTask() {
        String isInit = Constant.getConfigItem("md_timetask", "false");
        if ("false".equals(isInit)) {
            return;
        }

        IdbTaskUtil util = new IdbTaskUtil();
        try {
            String cs = Constant.getConfigItem("taskCS");
            String cl = Constant.getConfigItem("idbTaskInfoCL");
            List<Map<String, Object>> list = util.getTaskInfo(cs, cl);
            if (list != null && !list.isEmpty()) {
                IdbTaskThreadUtil taskThread = new IdbTaskThreadUtil(list, cs, cl);
                Thread thread = new Thread(taskThread);
                thread.start();
            }
        } catch (Exception e) {
            LOG.error("查询异常", e);
        }
    }

    // ------------------------------elasticsearch-------------------------------------//
    // 当没有启动多数据源的时候 还是使用原来的单 impala数据源api
    @ConditionalOnProperty(name = "idc.ismulti.enable", havingValue = "false")
    @Scheduled(cron = "*/5 * * * * ?")
    public void executeTaskEs() {
        String isInit = Constant.getConfigItem("es_timetask", "false");
        if ("false".equals(isInit)) {
            return;
        }
        // System.out.println("isInit:" + isInit + "-------------------------------->");

        IdbTaskUtilEs util = new IdbTaskUtilEs();
        try {
            String csi = Constant.getConfigItem("idbTaskInfoCS");
            String cli = Constant.getConfigItem("idbTaskInfoCL");
            List<Map<String, Object>> list = util.getTaskInfo(csi, cli);
            if (list != null && !list.isEmpty()) {
                for (Map<String, Object> map : list) {
                    String id = map.get("_id").toString();
                    String taskid = map.get("taskid").toString();
                    util.setTaskState(csi, cli, 1, id);
                    System.out.println("taskid:	" + taskid + " is runing");
                }
                IdbTaskThreadUtilEs taskThread = new IdbTaskThreadUtilEs(list, csi, cli);
                Thread thread = new Thread(taskThread);
                thread.start();
            }
        } catch (Exception e) {
            LOG.error("查询异常", e);
        }
    }

    @ConditionalOnProperty(name = "idc.ismulti.enable", havingValue = "true")
    @Scheduled(cron = "*/5 * * * * ?")
    public void executeTaskEsMultiDao() {
        String isInit = Constant.getConfigItem("es_timetask", "false");
        if ("false".equals(isInit)) {
            return;
        }
        // System.out.println("isInit:" + isInit + "-------------------------------->");

        IdbTaskUtilEs util = new IdbTaskUtilEs();
        try {
            String csi = Constant.getConfigItem("idbTaskInfoCS");
            String cli = Constant.getConfigItem("idbTaskInfoCL");
            List<Map<String, Object>> list = util.getTaskInfo(csi, cli);
            if (list != null && !list.isEmpty()) {
                for (Map<String, Object> map : list) {
                    String id = map.get("_id").toString();
                    String taskid = map.get("taskid").toString();
                    util.setTaskState(csi, cli, 1, id);
                    System.out.println("taskid:	" + taskid + " is runing");
                }
                IdbTaskThreadUtilEs taskThread = new IdbTaskThreadUtilEs(list, csi, cli);
                Thread thread = new Thread(taskThread);
                thread.start();
            }
        } catch (Exception e) {
            LOG.error("查询异常", e);
        }
    }
}
