
package com.eversec.database.sdb.controller;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.UUID;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.eversec.database.sdb.model.request.ReqParamterIdb;
import com.eversec.database.sdb.model.rmessage.RMessage;
import com.eversec.database.sdb.service.IdbService;

/**
 * impala SQL 操作
 *
 * @author Jony 异常编码：-20000
 */
@Controller
@RequestMapping(value = "database-web/_eversec/_idbsql")
public class IdbSqlAction {

    private static Logger logger = LoggerFactory.getLogger(IdbSqlAction.class);

    @Resource
    private IdbService idbService;

    private int errorCode = -20000;

    @RequestMapping(value = "", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String idbIndex(ReqParamterIdb rpi) {
        String errorMes = "";
        String uuid = UUID.randomUUID().toString();
        if (rpi == null) {
            errorMes = "{\"code\":" + (errorCode - 1) + ",\"exception\":\"无法找到命令参数！\"}";
        } else {
            logger.info("idb:{}:{}", uuid, rpi);

            String vMessage = rpi.validate();
            if (!"".equals(vMessage)) {
                errorMes = "{\"code\":" + (errorCode - 2) + ",\"exception\":\"" + vMessage + "\"}";
            } else {
                try {
                    Method method = idbService.getClass().getMethod(rpi.getCmd(), rpi.getClass());
                    Object object = method.invoke(idbService, rpi);
                    if (object != null && object instanceof RMessage) {
                        String result = ((RMessage) object).toJson();
                        logger.info("idb:{}:结束:{}", uuid, result);
                        return result;
                    }
                } catch (NoSuchMethodException e) {
                    logger.error("查询impala异常", e);
                    errorMes = "{\"code\":" + (errorCode - 3) + ",\"exception\":\"出现异常！\"}";
                } catch (SecurityException e) {
                    logger.error("查询impala异常", e);
                    errorMes = "{\"code\":" + (errorCode - 4) + ",\"exception\":\"出现异常！\"}";
                } catch (IllegalAccessException e) {
                    logger.error("查询impala异常", e);
                    errorMes = "{\"code\":" + (errorCode - 5) + ",\"exception\":\"出现异常！\"}";
                } catch (IllegalArgumentException e) {
                    logger.error("查询impala异常", e);
                    errorMes = "{\"code\":" + (errorCode - 6) + ",\"exception\":\"出现异常！\"}";
                } catch (InvocationTargetException e) {
                    logger.error("查询impala异常", e);
                    errorMes = "{\"code\":" + (errorCode - 7) + ",\"exception\":\"出现异常！\"}";
                }
            }
        }

        logger.info("idb:{}:结束errorMes:{}", uuid, errorMes);
        return errorMes;
    }

    @RequestMapping(value = "/_task", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String idbTaskIndex(ReqParamterIdb rpi) {
        String errorMes = "";
        if (rpi == null) {
            errorMes = "{\"code\":" + (errorCode - 1) + ",\"exception\":\"无法找到命令参数！\"}";
        }
        String vMessage = rpi.validate();
        if (!"".equals(vMessage)) {
            errorMes = "{\"code\":" + (errorCode - 2) + ",\"exception\":\"" + vMessage + "\"}";
        } else {
            try {
                Method method = idbService.getClass().getMethod(rpi.getCmd() + "Task",
                        rpi.getClass());
                Object object = method.invoke(idbService, rpi);
                if (object != null && object instanceof RMessage) {
                    return ((RMessage) object).toJson();
                }
            } catch (NoSuchMethodException e) {
                logger.error("查询impala异常", e);
                errorMes = "{\"code\":" + (errorCode - 3) + ",\"exception\":\"出现异常！\"}";
            } catch (SecurityException e) {
                logger.error("查询impala异常", e);
                errorMes = "{\"code\":" + (errorCode - 4) + ",\"exception\":\"出现异常！\"}";
            } catch (IllegalAccessException e) {
                logger.error("查询impala异常", e);
                errorMes = "{\"code\":" + (errorCode - 5) + ",\"exception\":\"出现异常！\"}";
            } catch (IllegalArgumentException e) {
                logger.error("查询impala异常", e);
                errorMes = "{\"code\":" + (errorCode - 6) + ",\"exception\":\"出现异常！\"}";
            } catch (InvocationTargetException e) {
                logger.error("查询impala异常", e);
                errorMes = "{\"code\":" + (errorCode - 7) + ",\"exception\":\"出现异常！\"}";
            }
        }

        return errorMes;
    }

    /**
     * 帮助文档接口
     */
    @RequestMapping(value = "/help", method = RequestMethod.GET, produces = "text/html;charset=UTF-8")
    public String acceptHelpSdb() {
        return "/help/idbhelp";
    }
}
