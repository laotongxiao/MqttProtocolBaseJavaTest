package com.test01;

import org.junit.Test;

public class AppTestPublishProtocol {
    @Test
    public void testPen(){
        TestPublishProtocol protocol = new TestPublishProtocol();
        protocol.setMessageType(3);//消息类型
        protocol.setDupFlag(false);//打开标志
        protocol.setQosLevel(1);//服务质量
        protocol.setRetain(false);//保持

        protocol.setRemainingLength(12);//剩余长度

        protocol.setTopicNameData("Test");//主题名
        protocol.setMessageIdentifierSize(10);//报文标识符

        protocol.setContentData("Test");//消息体内容

        protocol.parseContentData(protocol.genContentData());
        System.out.println(protocol.toString());
    }

    public void testParse(){
        byte[] a = new byte[14];
        a[0] = 50;
        a[1] = 12;//剩余长度
        a[2] = 0;//主题名开始
        a[3] = 4;
        a[4] = 84;
        a[5] = 101;
        a[6] = 115;
        a[7] = 116;
        a[8] = 0;//报文标识符
        a[9] = 10;
        a[10] = 84;
        a[11] = 101;
        a[12] = 115;
        a[13] = 116;
        TestPublishProtocol protocol = new TestPublishProtocol();
        protocol.parseContentData(a);
        System.out.println(protocol.toString());
    }
}
