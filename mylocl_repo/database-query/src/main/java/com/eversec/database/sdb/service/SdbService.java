// package com.eversec.database.sdb.service;
//
// import net.sf.json.JSONException;
//
// import org.springframework.stereotype.Service;
//
// import com.eversec.database.sdb.dao.base.executer.Executer;
// import com.eversec.database.sdb.dao.mdb.CountExecuter;
// import com.eversec.database.sdb.dao.mdb.InsertExecuter;
// import com.eversec.database.sdb.dao.mdb.QueryExecuter;
// import com.eversec.database.sdb.dao.mdb.RemoveExecuter;
// import com.eversec.database.sdb.dao.mdb.UpdateExecuter;
// import com.eversec.database.sdb.model.mdb.NoSqlCommand;
// import com.eversec.database.sdb.model.request.ReqParamterSdb;
// import com.eversec.database.sdb.model.rmessage.ExceptionRMessage;
// import com.eversec.database.sdb.model.rmessage.RMessage;
// import com.eversec.database.sdb.util.Exception.BaseException;
// import com.eversec.database.sdb.util.command.SdbCommandInter;
// import com.eversec.database.sdb.util.command.SdbCountCommand;
// import com.eversec.database.sdb.util.command.SdbInsertCommand;
// import com.eversec.database.sdb.util.command.SdbQueryCommand;
// import com.eversec.database.sdb.util.command.SdbRemoveCommand;
// import com.eversec.database.sdb.util.command.SdbUpdateCommand;
//
/// **
// * 巨衫数据库业务
// *
// * @author Jony 异常编码：-92000
// */
// @Service
// public class SdbService {
// RMessage rMessage = null;
// Executer executer = null;
//
// /**
// * query业务
// *
// * @param srm
// * 请求信息
// * @return 返回信息bean
// */
// public RMessage query(ReqParamterSdb srm) {
// SdbCommandInter sc = new SdbQueryCommand(srm.getCmd());
//
// try {
// sc.compileCommand(srm.getCommand());
// NoSqlCommand command = sc.getCommand();
// command.cs = srm.getCs();
// command.cl = srm.getCl();
// executer = new QueryExecuter();
// rMessage = executer.execute(command);
// } catch (JSONException e) {
// rMessage.code = -2;
// rMessage.exception = e.getMessage();
// e.printStackTrace();
// } catch (Exception e) {
// e.printStackTrace();
// rMessage = new ExceptionRMessage();
// if (e instanceof BaseException) {
// BaseException be = (BaseException) e;
// rMessage.code = be.getCode();
// rMessage.exception = be.getMessage();
// } else {
// rMessage.code = -1;
// rMessage.exception = e.getMessage();
// }
// }
// return rMessage;
// }
//
// /**
// * 统计查询
// *
// * @param srm
// * 请求信息
// * @return 返回信息ben
// */
// public RMessage count(ReqParamterSdb srm) {
// SdbCommandInter sc = new SdbCountCommand(srm.getCmd());
// try {
// sc.compileCommand(srm.getCommand());
// NoSqlCommand command = sc.getCommand();
// command.cs = srm.getCs();
// command.cl = srm.getCl();
// executer = new CountExecuter();
// rMessage = executer.execute(command);
// } catch (JSONException e) {
// rMessage.code = -2;
// rMessage.exception = e.getMessage();
// e.printStackTrace();
// } catch (Exception e) {
// e.printStackTrace();
// rMessage = new ExceptionRMessage();
// if (e instanceof BaseException) {
// BaseException be = (BaseException) e;
// rMessage.code = be.getCode();
// rMessage.exception = be.getMessage();
// } else {
// rMessage.code = -1;
// rMessage.exception = e.getMessage();
// }
// }
// return rMessage;
// }
//
// /**
// * 聚合查询
// *
// * @param srm
// * @return
// */
// // public RMessage aggregate(ReqParamterSdb srm) {
// //
// // SdbCommandInter sc = new SdbAggregateCommand();
// // try {
// // sc.compileCommand(srm.getCommand());
// // sdbDao = new SdbDaoCommandAggregate();
// // List<Map<String, Object>> res = (List<Map<String, Object>>) sdbDao
// // .executeCommand(srm.getCs(), srm.getCl(), sc);
// // sm.setTotal(res.size());
// // sm.datas = res;
// // sm.setCmd("aggregate");
// // } catch (JSONException e) {
// // rMessage.code = -2;
// // rMessage.exception = e.getMessage();
// // e.printStackTrace();
// // } catch (Exception e) {
// // e.printStackTrace();
// // rMessage = new ExceptionRMessage();
// // if (e instanceof BaseException) {
// // BaseException be = (BaseException)e;
// // rMessage.code = be.getCode();
// // rMessage.exception = be.getMessage();
// // }else {
// // rMessage.code = -1;
// // rMessage.exception = e.getMessage();
// // }
// // }
// // return rMessage;
// // }
//
// /**
// * 数据添加
// *
// * @param srm
// * @return
// */
// public RMessage insert(ReqParamterSdb srm) {
// SdbCommandInter sc = new SdbInsertCommand(srm.getCmd());
// try {
// sc.compileCommand(srm.getCommand());
// NoSqlCommand command = sc.getCommand();
// command.cs = srm.getCs();
// command.cl = srm.getCl();
// executer = new InsertExecuter();
// rMessage = executer.execute(command);
// } catch (JSONException e) {
// rMessage.code = -2;
// rMessage.exception = e.getMessage();
// e.printStackTrace();
// } catch (Exception e) {
// e.printStackTrace();
// rMessage = new ExceptionRMessage();
// if (e instanceof BaseException) {
// BaseException be = (BaseException) e;
// rMessage.code = be.getCode();
// rMessage.exception = be.getMessage();
// } else {
// rMessage.code = -1;
// rMessage.exception = e.getMessage();
// }
// }
// return rMessage;
// }
//
// /**
// * 删除数据 根据条件删除
// *
// * @param srm
// * @return
// */
// public RMessage remove(ReqParamterSdb srm) {
//
// SdbCommandInter sc = new SdbRemoveCommand(srm.getCmd());
// try {
// sc.compileCommand(srm.getCommand());
// NoSqlCommand command = sc.getCommand();
// command.cs = srm.getCs();
// command.cl = srm.getCl();
// executer = new RemoveExecuter();
// rMessage = executer.execute(command);
// } catch (JSONException e) {
// rMessage.code = -2;
// rMessage.exception = e.getMessage();
// e.printStackTrace();
// } catch (Exception e) {
// e.printStackTrace();
// rMessage = new ExceptionRMessage();
// if (e instanceof BaseException) {
// BaseException be = (BaseException) e;
// rMessage.code = be.getCode();
// rMessage.exception = be.getMessage();
// } else {
// rMessage.code = -1;
// rMessage.exception = e.getMessage();
// }
// }
// return rMessage;
// }
//
// /**
// * 删除全部数据
// *
// * @param srm
// * @return
// */
// public RMessage removeall(ReqParamterSdb srm) {
//
// SdbCommandInter sc = new SdbRemoveCommand(srm.getCmd());
// try {
// sc.compileCommand(srm.getCommand());
// NoSqlCommand command = sc.getCommand();
// command.cs = srm.getCs();
// command.cl = srm.getCl();
// executer = new RemoveExecuter();
// rMessage = executer.execute(command);
// } catch (JSONException e) {
// rMessage.code = -2;
// rMessage.exception = e.getMessage();
// e.printStackTrace();
// } catch (Exception e) {
// e.printStackTrace();
// rMessage = new ExceptionRMessage();
// if (e instanceof BaseException) {
// BaseException be = (BaseException) e;
// rMessage.code = be.getCode();
// rMessage.exception = be.getMessage();
// } else {
// rMessage.code = -1;
// rMessage.exception = e.getMessage();
// }
// }
// return rMessage;
// }
//
// /**
// * 修改数据
// *
// * @param srm
// * @return
// */
// public RMessage update(ReqParamterSdb srm) {
//
// SdbCommandInter sc = new SdbUpdateCommand();
// try {
// sc.compileCommand(srm.getCommand());
// NoSqlCommand command = sc.getCommand();
// command.cs = srm.getCs();
// command.cl = srm.getCl();
// executer = new UpdateExecuter();
// rMessage = executer.execute(command);
// } catch (JSONException e) {
// rMessage.code = -2;
// rMessage.exception = e.getMessage();
// e.printStackTrace();
// } catch (Exception e) {
// e.printStackTrace();
// rMessage = new ExceptionRMessage();
// if (e instanceof BaseException) {
// BaseException be = (BaseException) e;
// rMessage.code = be.getCode();
// rMessage.exception = be.getMessage();
// } else {
// rMessage.code = -1;
// rMessage.exception = e.getMessage();
// }
// }
// return rMessage;
// }
// }
