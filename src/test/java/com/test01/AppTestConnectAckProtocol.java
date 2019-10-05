package com.test01;

import org.junit.Test;

public class AppTestConnectAckProtocol {
    @Test
    public void testPen(){
        TestConnectAckProtocol protocol = new TestConnectAckProtocol();
        protocol.setMessageType(2);//消息类型
        protocol.setDupFlag(false);//打开标志
        protocol.setQosLevel(0);//服务质量
        protocol.setRetain(false);//保持

        protocol.setRemainingLength(2);//剩余长度

        protocol.setSession(false);//连接确认标志的当前会话
        protocol.setConnectReturnCode(0);//连接返回码
        protocol.parseContentData(protocol.genContentData());
        System.out.println(protocol.toString());
    }

    public void testParse(){
        byte[] a = new byte[4];
        a[0] = 32;
        a[1] = 2;//剩余长度
        a[2] = 0;//连接确认标志
        a[3] = 0;//连接返回码
        TestConnectAckProtocol protocol = new TestConnectAckProtocol();
        protocol.parseContentData(a);
        System.out.println(protocol.toString());
    }
}
