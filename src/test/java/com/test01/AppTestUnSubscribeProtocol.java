package com.test01;

import org.junit.Test;

public class AppTestUnSubscribeProtocol {
    @Test
    public void testPen(){
        TestUnSubscribeProtocol protocol = new TestUnSubscribeProtocol();
        protocol.setMessageType(10);//消息类型
        protocol.setDupFlag(false);//打开标志
        protocol.setQosLevel(1);//服务质量
        protocol.setRetain(false);//保持

        protocol.setRemainingLength(14);//剩余长度
        protocol.setMessageIdentifierSize(1);//报文标识符
        for (int i = 0; i < 2; i++) {
            TestUnSubscribeProtocol.SubscribeContent subscribeContent = protocol.new SubscribeContent();
            subscribeContent.setTopicNameData("Test");
            protocol.getList().add(subscribeContent);
        }
        protocol.parseContentData(protocol.genContentData());
        System.out.println(protocol.toString());
    }

    public void testParse(){
        byte[] a = new byte[16];
        a[0] = -94;
        a[1] = 14;//剩余长度
        a[2] = 0;//报文标识符
        a[3] = 1;
        a[4] = 0;//主题过滤器开始
        a[5] = 4;
        a[6] = 84;//主题名字Test
        a[7] = 101;
        a[8] = 115;
        a[9] = 116;
        a[10] = 0;//主题过滤器开始
        a[11] = 4;
        a[12] = 84;//主题名字Test
        a[13] = 101;
        a[14] = 115;
        a[15] = 116;

        TestUnSubscribeProtocol protocol = new TestUnSubscribeProtocol();
        protocol.parseContentData(a);
        System.out.println(protocol.toString());
    }
}
