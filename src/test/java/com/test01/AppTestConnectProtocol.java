package com.test01;

import org.junit.Test;

public class AppTestConnectProtocol {
    @Test
    public void testPen(){
        TestConnectProtocol protocol = new TestConnectProtocol();
        protocol.setMessageType(1);//消息类型
        protocol.setDupFlag(false);//打开标志
        protocol.setQosLevel(0);//服务质量
        protocol.setRetain(false);//保持

        protocol.setRemainingLength(29);//剩余长度

        protocol.setProtocolName("MQTT");//协议名称内容

        protocol.setVersion(4);//协议版本号

        protocol.setUserNameFlag(false);//用户名标志
        protocol.setPasswordFlag(false);//密码标志
        protocol.setWillRetain(false);//将保留
        protocol.setWillQos(0);//将保留
        protocol.setWillFlag(false);//将标志
        protocol.setCleanSession(true);//清空Session标志
        protocol.setReserved(false);//保留的标志

        protocol.setAliveTimeSize(60);//保持存活计数器
        protocol.setContentData("paho8126700321262");//消息体内容
        protocol.parseContentData(protocol.genContentData());
        System.out.println(protocol.toString());
    }

    public void testParse(){
        byte[] a = new byte[31];
        a[0] = 16;
        a[1] = 29;//剩余长度
        a[2] = 0;//协议名称开始
        a[3] = 4;
        a[4] = 77;
        a[5] = 81;
        a[6] = 84;
        a[7] = 84;
        a[8] = 4;//协议版本号
        a[9] = 2;//连接标志
        a[10] = 0;//保持存活计数器开始
        a[11] = 60;
        a[12] = 0;//消息体开始
        a[13] = 17;
        a[14] = 112;
        a[15] = 97;
        a[16] = 104;
        a[17] = 111;
        a[18] = 56;
        a[19] = 49;
        a[20] = 50;
        a[21] = 54;
        a[22] = 55;
        a[23] = 48;
        a[24] = 48;
        a[25] = 51;
        a[26] = 50;
        a[27] = 49;
        a[28] = 50;
        a[29] = 54;
        a[30] = 50;
        TestConnectProtocol protocol = new TestConnectProtocol();
        protocol.parseContentData(a);
        System.out.println(protocol.toString());
    }
}
