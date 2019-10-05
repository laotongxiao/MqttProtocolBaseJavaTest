package com.test01;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

public class SocketUnit {
    private static Map<Integer, String> msgImp = new HashMap<>();

    static {
        msgImp.put(Config.CONNECT_PROTOCOL_TYPE, "com.test01.ConnectProtocol");       //1
//        msgImp.put(DataAckProtocol.PROTOCOL_TYPE, "com.test01.DataAckProtocol"); //1
        msgImp.put(Config.PING_PROTOCOL_TYPE, "com.test01.PingProtocol");             //12
        msgImp.put(Config.PING_ACK_PROTOCOL_TYPE, "com.test01.PingAckProtocol");      //13
        msgImp.put(Config.DIS_CONNECT_PROTOCOL_TYPE, "com.test01.DisConnectProtocol");   //14
        msgImp.put(Config.PUBLISH_PROTOCOL_TYPE, "com.test01.PublishProtocol");      //3
        msgImp.put(Config.PUBLISH_ACK_PROTOCOL_TYPE, "com.test01.PublishAckProtocol");    //4
        msgImp.put(Config.SUBSCRIBE_PROTOCOL_TYPE, "com.test01.SubscribeProtocol");      //8
        msgImp.put(Config.SUBSCRIBE_ACK_PROTOCOL_TYPE, "com.test01.SubscribeAckProtocol");      //9
        msgImp.put(Config.UNSUBSCRIBE_PROTOCOL_TYPE, "com.test01.UnSubscribeProtocol");      //10
        msgImp.put(Config.UNSUBSCRIBE_ACK_PROTOCOL_TYPE, "com.test01.UnSubscribeAckProtocol");  //11
//        msgImp.put(PingAckProtocol.PROTOCOL_TYPE, "com.test01.PingAckProtocol"); //3
    }
    //bye[4]个字节数组还原成十进制表示
    public static int byteArrayToInt(byte[] b) {
        int intValue = 0;
        for (int i = 0; i < b.length; i++) {
            intValue += (b[i] & 0xFF) << (8 * (3 - i)); //int占4个字节（0，1，2，3）
        }
        return intValue;
    }
    //bye[4]个字节数组还原成十进制,byteOffset表示长串字节中的第几个开始
    public static int byteArrayToInt(byte[] b, int byteOffset, int byteCount) {
        int intValue = 0;
        for (int i = byteOffset; i < (byteOffset + byteCount); i++) {
            intValue += (b[i] & 0xFF) << (8 * (3 - (i - byteOffset)));
        }
        return intValue;
    }
    /**
     * 将int转为大端，低字节存储高位(即[0]>> 24)  Java默认的也是大端 tcp规定的也是大端
     */
    public static byte[] int2ByteArrays(int i) {
        byte[] result = new byte[4];
        result[0] = (byte) ((i >> 24) & 0xFF);
        result[1] = (byte) ((i >> 16) & 0xFF);
        result[2] = (byte) ((i >> 8) & 0xFF);
        result[3] = (byte) (i & 0xFF);
        return result;
    }
    ////接收数据解析成协议
    public static BasicProtocol parseContentMsg(byte[] data) {
        int protocolTypeMap = (data[0] >> 4) & 0xF;
        if(protocolTypeMap ==10){
            for (int i = 0; i < data.length; i++) {
                //System.out.println("a[" + i + "]" + data[i]);
            }
        }
        String className = msgImp.get(protocolTypeMap);
        BasicProtocol basicProtocol;
        try {
            basicProtocol = (BasicProtocol) Class.forName(className).newInstance();
            basicProtocol.parseContentData(data);
        } catch (Exception e) {
            basicProtocol = null;
            e.printStackTrace();
        }
        return basicProtocol;
    }
    /**
     * 读数据
     * 功能1服务器从客户端读取协议数据
     * @param inputStream
     */
    public static BasicProtocol redFromStream(InputStream inputStream){
        BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
        int len = 0;
        int temp = 0;
        int multiplier = 1;
        int remainingLength = 0;
        int digit = 0;
        try {
            byte[] publishFixHeaderBytes = new byte[1];
            //第1步 先读取一条请求协议长度字节
            while (len < publishFixHeaderBytes.length){
                //从包装流读取header.length - len个长度数据到header的数组中,它是表示协议总长度用4个字节存贮
                //len 表示从len这个位置开始读取
                temp = bufferedInputStream.read(publishFixHeaderBytes, len, publishFixHeaderBytes.length - len);
                if(temp > 0){
                    len += temp;
                }else if(temp == -1){
                    bufferedInputStream.close();
                    //这个是一定要返回值的因为这方法要有返回置,要不就会抛异常
                    return null;
                }
            }
            //第2步 剩余长度
            byte[] remainingLengthBytes = new byte[4];
            len = 0;
            int remainingLEN = 0;
            do{
                temp = bufferedInputStream.read(remainingLengthBytes, remainingLEN, 1);
                if(temp > 0){
                    digit = remainingLengthBytes[remainingLEN]; //一个字节的有符号或者无符号，转换转换为四个字节有符号 int类型
                    remainingLength += (digit & 0x7f) * multiplier;
                    multiplier *= 128;
                    remainingLEN++;
                }
            }while ((digit & 0x80) != 0);
            //第3步 把可变头部和消息体读入
            len = 0;
            byte[] dataBytes = new byte[remainingLength];
            while (len < remainingLength){
                temp = bufferedInputStream.read(dataBytes, len, remainingLength - len);
                if(temp > 0){
                    len += temp;
                }
            }
            byte[] data = new byte[1 + remainingLEN + remainingLength];
            System.arraycopy(publishFixHeaderBytes,0, data,0,1);
            System.arraycopy(remainingLengthBytes,0, data,1,remainingLEN);
            System.arraycopy(dataBytes,0, data,remainingLEN + 1,remainingLength);
            BasicProtocol protocol = parseContentMsg(data);
            return protocol;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public static void write2Stream(BasicProtocol protocol, OutputStream outputStream) {
        try {
            outputStream.write(protocol.genContentData());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * 关闭输入流
     *
     * @param is
     */
    public static void closeInputStream(InputStream is) {
        try {
            if (is != null) {
                is.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 关闭输出流
     *
     * @param os
     */
    public static void closeOutputStream(OutputStream os) {
        try {
            if (os != null) {
                os.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
