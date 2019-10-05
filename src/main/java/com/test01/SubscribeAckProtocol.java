package com.test01;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class SubscribeAckProtocol extends BasicProtocol {
    private final int fixHeaderLEN = 1;
    private int fixHeader; //固定头部
    private int messageType; //消息类型
    private boolean dupFlag; //打开标志
    private int qosLevel; //服务质量
    private boolean retain; //保持

    private int remainingLength; //可变头部+消息体

    //报文标识符
    private final int messageIdentifierLEN = 2;
    private int messageIdentifierSize;
    private int messageIdentifierMSB;
    private int messageIdentifierLSB;
    //返回码
    private List<GrantedQoS> list = new ArrayList<>();

    @Override
    public int getProtocolType() {
        return messageType;
    }

    public class GrantedQoS{
        private final int grantedQoSReturnCodeLEN = 1;
        private int grantedQoSReturnCode; //请求服务质量byte后2位
        //-------
        public int getGrantedQoSReturnCodeLEN() {
            return grantedQoSReturnCodeLEN;
        }

        public int getGrantedQoSReturnCode() {
            return grantedQoSReturnCode;
        }

        public void setGrantedQoSReturnCode(int grantedQoSReturnCode) {
            this.grantedQoSReturnCode = grantedQoSReturnCode;
        }
        //-------
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

        //报文标识符
        byte messageIdentifierByteMSB = (byte) ((messageIdentifierSize >> 8) & 0xFF);
        byte messageIdentifierByteLSB = (byte) (messageIdentifierSize & 0xFF);

        //写到要发送的协议数据中
        ByteArrayOutputStream baos = new ByteArrayOutputStream(fixHeaderLEN + remainingLEN + remainingLength);
        baos.write(fixHeaderByte);
        baos.write(remainingLenBytes, 0, remainingLEN);
        baos.write(messageIdentifierByteMSB);
        baos.write(messageIdentifierByteLSB);
        //连接返回码
        for (int i = 0; i < list.size(); i++) {
            byte topicQosLevel= (byte)(list.get(i).getGrantedQoSReturnCode() & 0xFF);
            baos.write(topicQosLevel);
        }
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
        int remainingNumber = 0;
        do{
            digit = data[pos]; //一个字节的有符号或者无符号，转换转换为四个字节有符号 int类型
            remainingLenTemp += (digit & 0x7f) * multiplier;
            multiplier *= 128;
            pos++;
            remainingNumber++;
        }while ((digit & 0x80) != 0);
        this.remainingLength = remainingLenTemp;
        //报文标识符
        this.messageIdentifierMSB = data[pos];
        this.messageIdentifierLSB = data[pos + 1];
        this.messageIdentifierSize = (((data[pos] & 0xFF) << 8)
                | (data[pos + 1] & 0xFF)
                | ((0 & 0xFF) << 16)
                | ((0 & 0xFF) << 24));
        pos += 2;
        //连接返回码
        do{
            GrantedQoS grantedQoS = new GrantedQoS();
            int grantedQoSReturnCode = data[pos] & 0xFF;
            grantedQoS.setGrantedQoSReturnCode(grantedQoSReturnCode);
            pos += 1;
            //把数据加入list中
            list.add(grantedQoS);
        }while (pos <= (remainingLenTemp + remainingNumber));
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

    public int getMessageIdentifierLEN() {
        return messageIdentifierLEN;
    }

    public int getMessageIdentifierSize() {
        return messageIdentifierSize;
    }

    public void setMessageIdentifierSize(int messageIdentifierSize) {
        this.messageIdentifierSize = messageIdentifierSize;
    }

    public int getMessageIdentifierMSB() {
        return messageIdentifierMSB;
    }

    public void setMessageIdentifierMSB(int messageIdentifierMSB) {
        this.messageIdentifierMSB = messageIdentifierMSB;
    }

    public int getMessageIdentifierLSB() {
        return messageIdentifierLSB;
    }

    public void setMessageIdentifierLSB(int messageIdentifierLSB) {
        this.messageIdentifierLSB = messageIdentifierLSB;
    }

    public List<GrantedQoS> getList() {
        return list;
    }

    public void setList(List<GrantedQoS> list) {
        this.list = list;
    }

    @Override
    public String toString() {
        return "SubscribeAckProtocol{" +
                "fixHeaderLEN=" + fixHeaderLEN +
                ", fixHeader=" + fixHeader +
                ", messageType=" + messageType +
                ", dupFlag=" + dupFlag +
                ", qosLevel=" + qosLevel +
                ", retain=" + retain +
                ", remainingLength=" + remainingLength +
                ", messageIdentifierLEN=" + messageIdentifierLEN +
                ", messageIdentifierSize=" + messageIdentifierSize +
                ", messageIdentifierMSB=" + messageIdentifierMSB +
                ", messageIdentifierLSB=" + messageIdentifierLSB +
                ", list=" + list +
                '}';
    }

    //-------------------------------------------
    @Override
    public String printProtocol() {
        return toString();
    }
}
