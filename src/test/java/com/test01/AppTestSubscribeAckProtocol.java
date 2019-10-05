package com.test01;

import org.junit.Test;



public class AppTestSubscribeAckProtocol {
    @Test
    public void testPen(){
        TestSubscribeAckProtocol protocol = new TestSubscribeAckProtocol();
        protocol.setMessageType(9);//消息类型
        protocol.setDupFlag(false);//打开标志
        protocol.setQosLevel(0);//服务质量
        protocol.setRetain(false);//保持

        protocol.setRemainingLength(5);//剩余长度

        protocol.setMessageIdentifierSize(1);//报文标识符
        TestSubscribeAckProtocol.GrantedQoS grantedQoS = protocol.new GrantedQoS();
        for (int i = 0; i < 3; i++) {

            if(i == 0){
                grantedQoS.setGrantedQoSReturnCode(0);
            }
            if(i == 1){
                grantedQoS.setGrantedQoSReturnCode(1);
            }
            if(i == 2){
                grantedQoS.setGrantedQoSReturnCode(2);
            }

            protocol.getList().add(grantedQoS);
        }
        protocol.parseContentData(protocol.genContentData());
        System.out.println(protocol.toString());
    }

    public void testParse(){
        byte[] a = new byte[7];
        a[0] = -112;
        a[1] = 5;//剩余长度
        a[2] = 0;//报文标识符
        a[3] = 1;
        a[4] = 0;//返回码
        a[5] = 1;
        a[6] = 2;
        TestSubscribeAckProtocol protocol = new TestSubscribeAckProtocol();
        protocol.parseContentData(a);
        System.out.println(protocol.toString());
    }
}
