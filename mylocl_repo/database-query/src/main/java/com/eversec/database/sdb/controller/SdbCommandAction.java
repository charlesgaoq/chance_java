// package com.eversec.database.sdb.controller;
//
//
// import org.springframework.stereotype.Controller;
// import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.RequestMethod;
// import org.springframework.web.bind.annotation.ResponseBody;
//
// import javax.annotation.Resource;
//
// import com.eversec.database.sdb.model.request.ReqParamterSdb;
// import com.eversec.database.sdb.model.rmessage.ExceptionRMessage;
// import com.eversec.database.sdb.model.rmessage.RMessage;
// import com.eversec.database.sdb.service.SdbService;
//
/// **
// * 巨衫数据库命令接口类
// *
// * @author Jony 异常编码：-91000
// */
// @Controller
// @RequestMapping(value = "database-web/_eversec/_sequoiadb")
// public class SdbCommandAction {
// @Resource
// private SdbService service;
//
// /**
// * 巨衫数据库查询接口
// *
// * @return
// */
// @RequestMapping(value = "", method = RequestMethod.GET, produces =
// "application/json;charset=UTF-8")
// @ResponseBody
// public String AcceptCommandSdb(ReqParamterSdb srm) {
// // String errorMes = "";
// // if (srm == null) {
// // errorMes = "{\"code\":-91000,\"exception\":\"无法找到命令参数！\"}";
// // }
// // String vMessage = srm.validate();
// // if (!"".equals(vMessage)) {
// // errorMes = "{\"code\":-91006,\"exception\":\"" + vMessage + "\"}";
// // } else {
// // try {
// // Method method = service.getClass().getMethod(srm.getCmd(),
// // srm.getClass());
// // Object object = method.invoke(service, srm);
// // if (object != null && object instanceof RMessage) {
// // return ((RMessage)object).toJson();
// // }
// // } catch (NoSuchMethodException e) {
// // e.printStackTrace();
// // errorMes = "{\"code\":-91001,\"exception\":\"出现异常！\"}";
// // } catch (SecurityException e) {
// // e.printStackTrace();
// // errorMes = "{\"code\":-91002,\"exception\":\"出现异常！\"}";
// // } catch (IllegalAccessException e) {
// // e.printStackTrace();
// // errorMes = "{\"code\":-91003,\"exception\":\"出现异常！\"}";
// // } catch (IllegalArgumentException e) {
// // e.printStackTrace();
// // errorMes = "{\"code\":-91004,\"exception\":\"出现异常！\"}";
// // } catch (InvocationTargetException e) {
// // e.printStackTrace();
// // errorMes = "{\"code\":-91005,\"exception\":\"出现异常！\"}";
// // }
// // }
//
// RMessage rMessage = new ExceptionRMessage();
// rMessage.cmd = "mongodb";
// rMessage.code = 500;
// rMessage.exception = "mdb接口服务未开启";
// return rMessage.toJson();
// }
//
// /**
// * 帮助文档接口
// *
// * @return
// */
// @RequestMapping(value = "/help", method = RequestMethod.GET, produces =
// "text/html;charset=UTF-8")
// public String AcceptHelpSdb() {
// return "help/help";
// }
// }
