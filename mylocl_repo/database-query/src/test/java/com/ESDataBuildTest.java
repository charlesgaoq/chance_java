package com;

import java.util.List;

import org.junit.Test;
import com.alibaba.fastjson.JSONObject;
import com.eversec.database.synch.dao.IdbTaskResult;

public class ESDataBuildTest {

    @Test
    public void buildTest() {
        String str = "[{\r\n" + "  \"sort_id\": 888888,\r\n"
                + "  \"taskid\": \"19a41a0ae086402387b28056aed909ee\",\r\n"
                + "  \"taskdate\": \"20180802\",\r\n" + "  \"result\":{\r\n"
                + "    \"starttime\": \"20180901070835\",\r\n"
                + "    \"endtime\": \"20180901070835\",\r\n"
                + "    \"msid\": \"8613620526800\",\r\n"
                + "    \"nat_cleintip\": \"117.136.40.168\",\r\n"
                + "    \"nat_cleintport\": 2224,\r\n" + "    \"protocoltype\": \"dns\",\r\n"
                + "    \"destinationip\": \"221.179.38.7\",\r\n"
                + "    \"destinationport\": 53,\r\n" + "    \"clientip\": \"10.34.89.221\",\r\n"
                + "    \"clientport\": 28080,\r\n" + "    \"apn\": \"cmnet\",\r\n"
                + "    \"day\": 20180801,\r\n" + "    \"hour\": 7\r\n" + "}\r\n" + " }]";

        List<IdbTaskResult> list = JSONObject.parseArray(str, IdbTaskResult.class);
        System.out.println(list.get(0).getResult());
    }
}
