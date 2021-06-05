
package com.eversec.database.sdb.service;

//import java.util.Date;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//import org.springframework.context.annotation.Scope;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Component;
//
//import com.eversec.database.sdb.constant.Constant;
//import com.eversec.database.sdb.model.SdbResponseMessage;
//import com.eversec.database.sdb.model.request.ReqParamterSdbsql;
//import com.eversec.database.sdb.util.sdbtask.SdbTaskUtil;

/**
 * sequoiadb sql查询(关联查询) 错误编码：-51000
 *
 * @author Jony
 */
// @Component
// @Scope("prototype")
public class SdbsqlService {
    // private int errorCode = -21000;

    /**
     * 实时查询
     *
     * @param rpi
     * @return
     */
    /*
     * @SuppressWarnings("unchecked")
     * public SdbResponseMessage query(ReqParamterSdbsql rpi) {
     * SdbResponseMessage srm = new SdbResponseMessage();
     * try {
     * ssd = new SdbsqlDaoQuery();
     * String sql = rpi.getCommand();
     * List<Map<String, Object>> res = (List<Map<String, Object>>) ssd
     * .execute(sql);
     * srm.setTotal(res.size());
     * srm.datas = res;
     * srm.setCmd("query");
     * } catch (Exception e) {
     * srm.setCode(errorCode - 1);
     * srm.setExceptions(e.getMessage());
     * e.printStackTrace();
     * }
     * return srm;
     * }
     */

    /**
     * 非实时查询
     *
     * @param rpi
     * @return
     */
    /*
     * public SdbResponseMessage queryTask(ReqParamterSdbsql rpi) {
     * SdbResponseMessage srm = new SdbResponseMessage();
     * try {
     * String cs = Constant.getConfigItem("taskCS");
     * String cl = Constant.getConfigItem("sdbTaskResultCL");
     * String cl_i = Constant.getConfigItem("sdbTaskInfoCL");
     *
     * String taskdate = Constant.getDateToStr("yyyyMMdd");
     *
     *
     * Map<String, Object> data = new HashMap<String, Object>();
     * data.put("resultcs", cs);
     * data.put("resultcl", cl);
     * data.put("command", rpi.getCommand());
     * data.put("taskid", rpi.getTaskid());
     * data.put("state", 0);
     * data.put("taskdate", taskdate);
     * data.put("level", rpi.getLevel());
     * SdbTaskUtil util = new SdbTaskUtil();
     * util.saveTaskInfo(cs, cl_i, data);
     *
     * Map<String, String> map = new HashMap<String, String>();
     * map.put("cs", cs);
     * map.put("cl", cl);
     * map.put("taskid", rpi.getTaskid());
     * map.put("taskdate", taskdate);
     * srm.datas = map;
     * srm.setTotal(0);
     * srm.setCmd("query");
     * } catch (Exception e) {
     * srm.setCode(errorCode - 2);
     * srm.setExceptions(e.getMessage());
     * e.printStackTrace();
     * }
     * return srm;
     * }
     */

    // @Scheduled(cron = "* */1 * * * *")
    /*
     * public void executeTask() {
     * String cs = Constant.getConfigItem("taskCS");
     * String cl = Constant.getConfigItem("sdbTaskInfoCL");
     * SdbTaskUtil util = new SdbTaskUtil();
     * try {
     * List<Map<String, Object>> list = util.getTaskInfo(cs, cl);
     * if (list != null && !list.isEmpty()) {
     * for (Map<String, Object> map : list) {
     * String _id = map.get("_id").toString();
     * String resultcs = map.get("resultcs").toString();
     * String resultcl = map.get("resultcl").toString();
     * String command = map.get("command").toString();
     * String taskid = map.get("taskid").toString();
     * String taskdate = map.get("taskdate").toString();
     * util.setTaskState(cs, cl, 1, _id);
     * try {
     * long st = new Date().getTime();
     * util.queryTask(resultcs, resultcl, command, taskid,taskdate); // 查询及保存记录
     * long et = new Date().getTime();
     * long tol = et - st;
     * util.setTaskState(cs, cl, 2, _id, tol);
     * } catch (BaseException e) {
     * util.setTaskState(cs, cl, 3, _id, -1);
     * util.setExceptionMess(e.getErrorCode(), e.getMessage(),
     * taskid, resultcs, resultcl);
     * e.printStackTrace();
     * } catch (Exception e) {
     * util.setTaskState(cs, cl, 3, _id, -1);
     * util.setExceptionMess(errorCode - 3, e.getMessage(),
     * taskid, resultcs, resultcl);
     * e.printStackTrace();
     * }
     * }
     * }
     * } catch (BaseException e) {
     * e.printStackTrace();
     * } catch (Exception e) {
     * e.printStackTrace();
     * }
     * }
     */
}
