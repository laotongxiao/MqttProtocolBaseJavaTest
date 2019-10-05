package com.test01;

import java.io.ByteArrayOutputStream;

public class ConnectAckProtocol extends BasicProtocol {
    private final int fixHeaderLEN = 1;
    private int fixHeader; //固定头部
    private int messageType; //消息类型
    private boolean dupFlag; //打开标志
    private int qosLevel; //服务质量
    private boolean retain; //保持

    private int remainingLength; //可变头部+消息体

    private final int ConnectAckFlagsLEN = 1;
    private boolean session; //连接确认标志的当前会话只占一位就是0位

    private final int connectReturnCodeLEN = 1;
    private int connectReturnCode; //连接返回码占8位

    @Override
    public int getProtocolType() {
        return messageType;
    }

    //发送字符串拼接
    @Override
    public byte[] genContentData() {
        //messageType; //消息类型
        int dupFlagInt = dupFlag ? 1 : 0; //打开标志
        //qosLevel; //服务质量
        int retainInt = retain ? 1 : 0; //保持
        fixHeader = (((messageType & 0xF) & 0xFF) <<4 | ((dupFlagInt & 0x1) & 0xF) <<3  | ((qosLevel & 0x3) & 0x7) <<1  | (retainInt & 0x1));
        byte fixHeaderByte = (byte) fixHeader;
        //剩余长度开始
        int tempLen = remainingLength;
        int tempLenData = remainingLength;
        //对剩余长度进行编码所要的数组长度
        int remainingLEN = 0;
        do {
            int digit = tempLen % 128;
            tempLen = tempLen / 128;
            if (tempLen > 0){
                digit = digit | 0x80;
            }
            remainingLEN++;
        } while (tempLen > 0);
        byte[] remainingLenBytes = new byte[remainingLEN];
        //对剩余长度int进行编码
        int k = 0;
        do {
            int digitData = tempLenData % 128;
            tempLenData = tempLenData / 128;
            if (tempLenData > 0){
                digitData = tempLenData | 0x80;
            }
            remainingLenBytes[k] = (byte)digitData;
            k++;
        } while (tempLenData > 0);

        //剩余长度结束

        //连接确认标志
        int reservedInt = session ? 1 : 0;
        byte sessionByte = (byte) (reservedInt & 0x1);
        //连接返回码
        byte connectReturnCodeByte = (byte) connectReturnCode;
        //写到要发送的协议数据中
        ByteArrayOutputStream baos = new ByteArrayOutputStream(fixHeaderLEN + remainingLEN + remainingLength);
        baos.write(fixHeaderByte);
        baos.write(remainingLenBytes, 0, remainingLEN);
        baos.write(sessionByte);
        baos.write(connectReturnCodeByte);
        return baos.toByteArray();
    }

    //接收数据解析成协议
    @Override
    public void parseContentData(byte[] data) {
        int pos = 0;
        this.fixHeader = data[pos];
        this.messageType = (data[pos] >> 4) & 0xF; //消息类型
        this.dupFlag = (data[pos] & 8) > 0; //打开标志
        this.qosLevel = ((data[pos] & 0x6) >> 1); //服务质量
        this.retain = (data[pos] & 1) > 0; //保持
        pos += 1;
        //剩余长度
        int multiplier = 1;
        int remainingLenTemp = 0;
        int digit = 0;
        do{
            digit = data[pos]; //一个字节的有符号或者无符号，转换转换为四个字节有符号 int类型
            remainingLenTemp += (digit & 0x7f) * multiplier;
            multiplier *= 128;
            pos++;
        }while ((digit & 0x80) != 0);
        this.remainingLength = remainingLenTemp;
        //连接确认标志
        this.session = (data[pos] & 1) > 0; //保持
        pos += 1;
        //连接返回码
        this.connectReturnCode = data[pos];
        pos += 1;
    }
    //-------------------------------------------

    public int getFixHeaderLEN() {
        return fixHeaderLEN;
    }

    public int getFixHeader() {
        return fixHeader;
    }

    public void setFixHeader(int fixHeader) {
        this.fixHeader = fixHeader;
    }

    public int getMessageType() {
        return messageType;
    }

    public void setMessageType(int messageType) {
        this.messageType = messageType;
    }

    public boolean isDupFlag() {
        return dupFlag;
    }

    public void setDupFlag(boolean dupFlag) {
        this.dupFlag = dupFlag;
    }

    public int getQosLevel() {
        return qosLevel;
    }

    public void setQosLevel(int qosLevel) {
        this.qosLevel = qosLevel;
    }

    public boolean isRetain() {
        return retain;
    }

    public void setRetain(boolean retain) {
        this.retain = retain;
    }

    public int getRemainingLength() {
        return remainingLength;
    }

    public void setRemainingLength(int remainingLength) {
        this.remainingLength = remainingLength;
    }

    public int getConnectAckFlagsLEN() {
        return ConnectAckFlagsLEN;
    }

    public boolean isSession() {
        return session;
    }

    public void setSession(boolean session) {
        this.session = session;
    }

    public int getConnectReturnCodeLEN() {
        return connectReturnCodeLEN;
    }

    public int getConnectReturnCode() {
        return connectReturnCode;
    }

    public void setConnectReturnCode(int connectReturnCode) {
        this.connectReturnCode = connectReturnCode;
    }

    @Override
    public String toString() {
        return "ConnectAckProtocol{" +
                "fixHeaderLEN=" + fixHeaderLEN +
                ", fixHeader=" + fixHeader +
                ", messageType=" + messageType +
                ", dupFlag=" + dupFlag +
                ", qosLevel=" + qosLevel +
                ", retain=" + retain +
                ", remainingLength=" + remainingLength +
                ", ConnectAckFlagsLEN=" + ConnectAckFlagsLEN +
                ", session=" + session +
                ", connectReturnCodeLEN=" + connectReturnCodeLEN +
                ", connectReturnCode=" + connectReturnCode +
                '}';
    }
    //-------------------------------------------
    @Override
    public String printProtocol() {
        return toString();
    }
}
