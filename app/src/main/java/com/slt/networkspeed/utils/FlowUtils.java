package com.slt.networkspeed.utils;

import android.net.TrafficStats;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Arrays;



public class FlowUtils {
    /**
     * 返回byte的数据大小对应的文本
     *
     * @param size
     * @return
     */
    public static String getDataSize(long size) {
        if (size < 0) {
            size = 0;
        }
        DecimalFormat format = new DecimalFormat("####.00");
        if (size < 1024) {
            return size + "B";
        } else if (size < 1024 * 1024) {
            float kbsize = size / 1024f;
            return format.format(kbsize) + "KB";
        } else if (size < 1024 * 1024 * 1024) {
            float mbsize = size / 1024f / 1024f;
            return format.format(mbsize) + "MB";
        } else if (size < 1024 * 1024 * 1024 * 1024) {
            float gbsize = size / 1024f / 1024f / 1024f;
            return format.format(gbsize) + "GB";
        } else {
            return "";
        }
    }
    /**
     * 通过uid查询文件夹中的数据
     * @param localUid
     * @return
     */
    public  static Long getTotalBytesManual(int localUid) {
        File dir = new File("/proc/uid_stat/");
        String[] children = dir.list();
        if(dir.list()==null){
            return   TrafficStats.getUidRxBytes(localUid);
        }else {
            StringBuffer stringBuffer = new StringBuffer();
            for (int i = 0; i < children.length; i++) {
                stringBuffer.append(children[i]);
                stringBuffer.append("   ");
            }
            if (!Arrays.asList(children).contains(String.valueOf(localUid))) {
                return 0L;
            }
            File uidFileDir = new File("/proc/uid_stat/" + String.valueOf(localUid));
            File uidActualFileReceived = new File(uidFileDir, "tcp_rcv");
            //  File uidActualFileSent = new File(uidFileDir, "tcp_snd"); //下行流量
            String textReceived = "0";
            // String textSent = "0";
            try {
                BufferedReader brReceived = new BufferedReader(new FileReader(uidActualFileReceived));
                //   BufferedReader brSent = new BufferedReader(new FileReader(uidActualFileSent));
                String receivedLine;
                //  String sentLine;

                if ((receivedLine = brReceived.readLine()) != null) {
                    textReceived = receivedLine;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            //Long.valueOf(textSent).longValue()
            return Long.valueOf(textReceived).longValue();
        }
    }
}
