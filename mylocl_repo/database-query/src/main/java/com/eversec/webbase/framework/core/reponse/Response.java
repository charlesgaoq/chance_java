
package com.eversec.webbase.framework.core.reponse;

//import io.swagger.annotations.ApiModel;
//import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.List;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * 类名称: Response <br>
 * 类描述: controller返回的数据类型<br>
 * 创建人: mateng@eversec.cn <br>
 * 创建时间: 2016年11月25日上午9:28:50 <br>
 * 修改人: <br>
 * 修改时间: <br>
 */
// @ApiModel
public class Response implements Serializable {

    private static final long serialVersionUID = -3799117183776657761L;

    public static final int SUCCESS = 200;

    // @ApiModelProperty(value = "响应报文的响应码，code==200即为正常响应")
    private int code;

    // @ApiModelProperty(value = "响应报文的消息提示")
    private String message;

    // @ApiModelProperty(value = "响应报文的消息体")
    private Object body;

    // @ApiModelProperty(value = "响应报文的时间戳")
    private Long timestamp;

    public Response() {
    }

    public Response(Object body) {
        this(SUCCESS, "成功", body);
    }

    public Response(int code, String message, Object body) {
        this.code = code;
        this.message = message;
        this.body = body;
        timestamp = System.currentTimeMillis();
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getBody() {
        return body;
    }

    public Response setBody(Object data) {
        this.body = data;
        return this;
    }

    @JsonIgnore
    public <E> E getObjectBody(Class<E> clazz) {
        String json = getJsonBody();
        return JSONObject.parseObject(json, clazz);
    }

    @JsonIgnore
    public String getJsonBody() {
        return JSONObject.toJSONString(body);
    }

    public <E> List<E> getListBody(Class<E> clazz) {
        String json = getJsonBody();
        return JSONObject.parseArray(json, clazz);
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "Response [code=" + code + ", message=" + message + ", body=" + body + "]";
    }
}
