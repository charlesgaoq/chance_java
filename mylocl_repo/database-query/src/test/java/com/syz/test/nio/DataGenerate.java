
package com.syz.test.nio;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 云洲的生成数据。
 */
public class DataGenerate {
    public static void main(String[] args) {
        long t1 = System.currentTimeMillis();
        generate();
        long t2 = System.currentTimeMillis();
        System.out.println("time===" + (t2 - t1));
    }

    private static String[] domains = new String[] { "baidu.com", "taobao.com",
        "duanwangzhihuanyuan.51240.com", "sina.lt", "cnblogs.com", "tb1.cn", "csdn.net",
        "www.cssofree.com", "weixin.qq.com", "0010.cn" };

    public static void generate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String date = sdf.format(new Date());
        StringBuffer sb = new StringBuffer();
        String filePath = "d:/whoisResultTask/in/whois_result_" + date + "_1.txt";
        RandomAccessFile raf = null;
        FileChannel fc = null;
        try {
            raf = new RandomAccessFile(filePath, "rws");
            fc = raf.getChannel();
            int bufferSize = 1024 * 1024;
            ByteBuffer buffer = ByteBuffer.allocate(bufferSize);
            int count = 0;
            for (int i = 0; i < 10; i++) {
                sb = new StringBuffer();
                sb.append("﻿201707251_" + i);
                sb.append("|");
                // sb.append("139.196.230.82﻿");
                sb.append("");
                sb.append("|");
                sb.append("﻿浙江省杭州市 阿里云BGP数据中心");
                sb.append("|");
                // sb.append("﻿wytj.9vpay.com");//+date+"_"+i);
                sb.append(domains[i]);// +date+"_"+i);
                sb.append("|");
                sb.append("﻿");
                sb.append("|");
                sb.append("﻿");
                sb.append("|");
                sb.append("中国农业银行股份有限公司11﻿");
                sb.append("|");
                sb.append("﻿周晓蕾1");
                sb.append("|");
                sb.append("﻿");
                sb.append("|");
                sb.append("﻿2017-03-02");
                sb.append("|");
                sb.append("﻿2017-03-02");
                sb.append("|");
                sb.append("2017-03-02﻿");
                sb.append("|");
                sb.append("厦门纳网科技股份有限公司﻿1");
                sb.append("|");
                sb.append("whois.west.cn﻿");
                sb.append("\n");
                String data = sb.toString();
                byte[] dataByte = data.getBytes("UTF-8");
                int dataLen = dataByte.length;
                for (int j = 0; j < dataLen; j++) {
                    if (count == bufferSize) {
                        buffer.flip();
                        int a = fc.write(buffer);
                        System.out.println("a=" + a);
                        buffer.clear();
                        count = 0;
                    }
                    buffer.put(dataByte[j]);
                    count++;
                }
            }
            buffer.flip();
            int b = fc.write(buffer);
            System.out.println("b=" + b);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fc != null) {
                try {
                    fc.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (raf != null) {
                try {
                    raf.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
