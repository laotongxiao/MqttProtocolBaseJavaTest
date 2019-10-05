package com.test01;

import org.junit.Test;

public class AppTestUnSubscribeAckProtocol {
    @Test
    public void testPen(){
        TestUnSubscribeAckProtocol protocol = new TestUnSubscribeAckProtocol();
        protocol.setMessageType(11);//消息类型
        protocol.setDupFlag(false);//打开标志
        protocol.setQosLevel(0);//服务质量
        protocol.setRetain(false);//保持

        protocol.setRemainingLength(2);//剩余长度
        protocol.setMessageIdentifierSize(1);//报文标识符

        protocol.parseContentData(protocol.genContentData());
        System.out.println(protocol.toString());
    }

    public void testParse(){
        byte[] a = new byte[4];
        a[0] = -80;
        a[1] = 2;//剩余长度
        a[2] = 0;//报文标识符
        a[3] = 1;
        TestUnSubscribeAckProtocol protocol = new TestUnSubscribeAckProtocol();
        protocol.parseContentData(a);
        System.out.println(protocol.toString());
    }
}
