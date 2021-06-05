
package com.eversec.database.sdb.model.request;

public class ReqParamterElsManage extends ReqParamter {
    private String url;
    private String data;
    private String type;

    @Override
    public String validate() {
        // Auto-generated method stub
        return null;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

}
