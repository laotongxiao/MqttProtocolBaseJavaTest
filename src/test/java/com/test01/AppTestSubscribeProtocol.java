package com.test01;

import org.junit.Test;

public class AppTestSubscribeProtocol {
    @Test
    public void testPen(){
        TestSubscribeProtocol protocol = new TestSubscribeProtocol();
        protocol.setMessageType(8);//消息类型
        protocol.setDupFlag(false);//打开标志
        protocol.setQosLevel(1);//服务质量
        protocol.setRetain(false);//保持

        protocol.setRemainingLength(16);//剩余长度
        protocol.setMessageIdentifierSize(1);//报文标识符
        for (int i = 0; i < 2; i++) {
            TestSubscribeProtocol.SubscribeContent subscribeContent = protocol.new SubscribeContent();
            subscribeContent.setTopicNameData("Test");
            subscribeContent.setTopicQosLevel(1);
            protocol.getList().add(subscribeContent);
        }
        protocol.parseContentData(protocol.genContentData());
        System.out.println(protocol.toString());
    }

    public void testParse(){
        byte[] a = new byte[18];
        a[0] = -126;
        a[1] = 16;//剩余长度
        a[2] = 0;//报文标识符
        a[3] = 1;
        a[4] = 0;//主题过滤器开始 //48表示字符0
        a[5] = 4;              //52表示字符4
        a[6] = 84;//主题名字Test
        a[7] = 101;
        a[8] = 115;
        a[9] = 116;
        a[10] = 1;//请求质量    //49表示字符1
        a[11] = 0;//主题过滤器开始 //48表示字符0
        a[12] = 4;              //52表示字符4
        a[13] = 84;//主题名字Test
        a[14] = 101;
        a[15] = 115;
        a[16] = 116;
        a[17] = 1;//请求质量    //49表示字符1

        TestSubscribeProtocol protocol = new TestSubscribeProtocol();
        protocol.parseContentData(a);
        System.out.println(protocol.toString());
    }
}
