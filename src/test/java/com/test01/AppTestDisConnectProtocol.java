package com.test01;

import org.junit.Test;

public class AppTestDisConnectProtocol {
    @Test
    public void testPen(){
        TestDisConnectProtocol protocol = new TestDisConnectProtocol();
        protocol.setMessageType(14);//消息类型
        protocol.setDupFlag(false);//打开标志
        protocol.setQosLevel(0);//服务质量
        protocol.setRetain(false);//保持

        protocol.setRemainingLength(0);//剩余长度

        protocol.parseContentData(protocol.genContentData());
        System.out.println(protocol.toString());
    }

    public void testParse(){
        byte[] a = new byte[2];
        a[0] = -32;
        a[1] = 0;//剩余长度
        TestDisConnectProtocol protocol = new TestDisConnectProtocol();
        protocol.parseContentData(a);
        System.out.println(protocol.toString());
    }
}
