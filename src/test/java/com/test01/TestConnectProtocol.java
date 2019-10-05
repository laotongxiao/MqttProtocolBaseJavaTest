package com.test01;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;

public class TestConnectProtocol{
    private final int fixHeaderLEN = 1;
    private int fixHeader; //固定头部
    private int messageType; //消息类型
    private boolean dupFlag; //打开标志
    private int qosLevel; //服务质量
    private boolean retain; //保持

    private int remainingLength; //可变头部+消息体

    private final int protocolNameLEN = 2;
    private int protocolNameSize;
    private int protocolNameMSB;
    private int protocolNameLSB;
    private String protocolName; //协议名称
    private final int versionLEN = 1;
    private int version; //协议版本号

    private final int connectFlagsLEN = 1;
    private int connectFlags; //连接标志
    private boolean userNameFlag; //用户名标志
    private boolean passwordFlag; //密码标志
    private boolean willRetain; //将保留
    private int willQos; //将保留
    private boolean willFlag; //将标志
    private boolean cleanSession; //清空Session标志
    private boolean reserved; //保留的标志


    private final int aliveTimeLEN = 2;
    private int aliveTimeSize;
    private int aliveTimeMSB;
    private int aliveTimeLSB;
    //private int aliveTime; //保持存活计数器

    private final int contentDataHeadLEN = 2;
    private int contentDataSize;
    private int contentDataMSB;
    private int contentDataLSB;
    private String contentData; //暂时消息内容

    //发送字符串拼接
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
        byte protocolNameByteMSB = (byte)((protocolName.getBytes().length >> 8) & 0xFF);
        byte protocolNameByteLSB = (byte)(protocolName.getBytes().length & 0xFF);
        byte[] protocolNameBytes = protocolName.getBytes();
        byte versionByte = (byte) version;
        int userNameFlagInt =  userNameFlag ? 1 : 0; //用户名标志
        int passwordFlagInt = passwordFlag ? 1 : 0; //密码标志
        int willRetainInt =  willRetain ? 1 : 0; //将保留
        //willQos; //将保留
        int willFlagInt = willFlag ? 1 : 0; //将标志
        int cleanSessionInt = cleanSession ? 1 : 0; //清空Session标志
        int reservedInt = reserved ? 1 : 0; //保留的标志
        connectFlags = (((userNameFlagInt & 0x1) & 0xFF) <<7 | ((passwordFlagInt & 0x1) & 0x7F) <<6  | ((willRetainInt & 0x1) & 0x3F) <<5  | ((willQos & 0x3) & 0x1F) <<3 | ((willFlagInt & 0x1) & 0x7) <<2 | ((cleanSessionInt & 0x1) & 0x3) <<1 | (reservedInt & 0x1));
        byte connectFlagsByte = (byte) connectFlags;
        byte aliveTimeByteMSB = (byte)((aliveTimeSize >> 8) & 0xFF);
        byte aliveTimeByteLSB = (byte)(aliveTimeSize & 0xFF);
        byte contentDataByteMSB = (byte)((contentData.getBytes().length >> 8) & 0xFF);
        byte contentDataByteLSB = (byte)(contentData.getBytes().length & 0xFF);
        byte[] contentDataBytes = contentData.getBytes();
        //写到要发送的协议数据中
        ByteArrayOutputStream baos = new ByteArrayOutputStream(fixHeaderLEN + remainingLEN + remainingLength);
        baos.write(fixHeaderByte);
        baos.write(remainingLenBytes, 0, remainingLEN);
        baos.write(protocolNameByteMSB);
        baos.write(protocolNameByteLSB);
        baos.write(protocolNameBytes, 0, protocolName.getBytes().length);
        baos.write(versionByte);
        baos.write(connectFlagsByte);
        baos.write(aliveTimeByteMSB);
        baos.write(aliveTimeByteLSB);
        baos.write(contentDataByteMSB);
        baos.write(contentDataByteLSB);
        baos.write(contentDataBytes, 0 , contentData.getBytes().length);
        return baos.toByteArray();
    }

    //接收数据解析成协议
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
        //协议名
        this.protocolNameMSB = data[pos];
        this.protocolNameLSB = data[pos + 1];
        this.protocolNameSize = (((data[pos] & 0xFF)<<8)
                | (data[pos + 1] & 0xFF)
                | ((0 & 0xFF)<<16)
                | ((0 & 0xFF)<<24));
        pos += 2;
        try {
            this.protocolName = new String(data, pos, protocolNameSize, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        pos += protocolNameSize;
        //协议版本号
        this.version = data[pos];
        pos += 1;
        //连接标志
        this.connectFlags = data[pos]; //连接标志
        this.userNameFlag = (data[pos] & 0x40) > 0; //用户名标志
        this.passwordFlag = (data[pos] & 0x20) > 0; //密码标志
        this.willRetain = (data[pos] & 0x10) > 0; //将保留
        this.willQos = (data[pos] >> 3) & 0x3; //将保留
        this.willFlag = (data[pos] & 0x4) > 0; //将标志
        this.cleanSession = (data[pos] & 0x2) > 0; //清空Session标志
        this.reserved = (data[pos] & 0x1) > 0; //保留的标志
        pos += 1;
        //保持存活计数器
        this.aliveTimeMSB = data[pos];
        this.aliveTimeLSB = data[pos + 1];
        this.aliveTimeSize = (((data[pos] & 0xFF)<<8)
                | (data[pos + 1] & 0xFF)
                | ((0 & 0xFF)<<16)
                | ((0 & 0xFF)<<24));
        pos += 2;
        //暂时消息内容
        this.contentDataMSB = data[pos];
        this.contentDataLSB = data[pos + 1];
        this.contentDataSize = (((data[pos] & 0xFF)<<8)
                | (data[pos + 1] & 0xFF)
                | ((0 & 0xFF)<<16)
                | ((0 & 0xFF)<<24));
        pos += 2;
        try {
            this.contentData = new String(data, pos, contentDataSize, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
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

    public int getProtocolNameLEN() {
        return protocolNameLEN;
    }

    public int getProtocolNameSize() {
        return protocolNameSize;
    }

    public void setProtocolNameSize(int protocolNameSize) {
        this.protocolNameSize = protocolNameSize;
    }

    public int getProtocolNameMSB() {
        return protocolNameMSB;
    }

    public void setProtocolNameMSB(int protocolNameMSB) {
        this.protocolNameMSB = protocolNameMSB;
    }

    public int getProtocolNameLSB() {
        return protocolNameLSB;
    }

    public void setProtocolNameLSB(int protocolNameLSB) {
        this.protocolNameLSB = protocolNameLSB;
    }

    public String getProtocolName() {
        return protocolName;
    }

    public void setProtocolName(String protocolName) {
        this.protocolName = protocolName;
    }

    public int getVersionLEN() {
        return versionLEN;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public int getConnectFlagsLEN() {
        return connectFlagsLEN;
    }

    public int getConnectFlags() {
        return connectFlags;
    }

    public void setConnectFlags(int connectFlags) {
        this.connectFlags = connectFlags;
    }

    public boolean isUserNameFlag() {
        return userNameFlag;
    }

    public void setUserNameFlag(boolean userNameFlag) {
        this.userNameFlag = userNameFlag;
    }

    public boolean isPasswordFlag() {
        return passwordFlag;
    }

    public void setPasswordFlag(boolean passwordFlag) {
        this.passwordFlag = passwordFlag;
    }

    public boolean isWillRetain() {
        return willRetain;
    }

    public void setWillRetain(boolean willRetain) {
        this.willRetain = willRetain;
    }

    public int getWillQos() {
        return willQos;
    }

    public void setWillQos(int willQos) {
        this.willQos = willQos;
    }

    public boolean isWillFlag() {
        return willFlag;
    }

    public void setWillFlag(boolean willFlag) {
        this.willFlag = willFlag;
    }

    public boolean isCleanSession() {
        return cleanSession;
    }

    public void setCleanSession(boolean cleanSession) {
        this.cleanSession = cleanSession;
    }

    public boolean isReserved() {
        return reserved;
    }

    public void setReserved(boolean reserved) {
        this.reserved = reserved;
    }

    public int getAliveTimeLEN() {
        return aliveTimeLEN;
    }

    public int getAliveTimeSize() {
        return aliveTimeSize;
    }

    public void setAliveTimeSize(int aliveTimeSize) {
        this.aliveTimeSize = aliveTimeSize;
    }

    public int getAliveTimeMSB() {
        return aliveTimeMSB;
    }

    public void setAliveTimeMSB(int aliveTimeMSB) {
        this.aliveTimeMSB = aliveTimeMSB;
    }

    public int getAliveTimeLSB() {
        return aliveTimeLSB;
    }

    public void setAliveTimeLSB(int aliveTimeLSB) {
        this.aliveTimeLSB = aliveTimeLSB;
    }

    public int getContentDataHeadLEN() {
        return contentDataHeadLEN;
    }

    public int getContentDataSize() {
        return contentDataSize;
    }

    public void setContentDataSize(int contentDataSize) {
        this.contentDataSize = contentDataSize;
    }

    public int getContentDataMSB() {
        return contentDataMSB;
    }

    public void setContentDataMSB(int contentDataMSB) {
        this.contentDataMSB = contentDataMSB;
    }

    public int getContentDataLSB() {
        return contentDataLSB;
    }

    public void setContentDataLSB(int contentDataLSB) {
        this.contentDataLSB = contentDataLSB;
    }

    public String getContentData() {
        return contentData;
    }

    public void setContentData(String contentData) {
        this.contentData = contentData;
    }

    //-------------------------------------------

    @Override
    public String toString() {
        return "TestConnectProtocol{" +
                "fixHeaderLEN=" + fixHeaderLEN +
                ", fixHeader=" + fixHeader +
                ", messageType=" + messageType +
                ", dupFlag=" + dupFlag +
                ", qosLevel=" + qosLevel +
                ", retain=" + retain +
                ", remainingLength=" + remainingLength +
                ", protocolNameLEN=" + protocolNameLEN +
                ", protocolNameSize=" + protocolNameSize +
                ", protocolNameMSB=" + protocolNameMSB +
                ", protocolNameLSB=" + protocolNameLSB +
                ", protocolName='" + protocolName + '\'' +
                ", versionLEN=" + versionLEN +
                ", version=" + version +
                ", connectFlagsLEN=" + connectFlagsLEN +
                ", connectFlags=" + connectFlags +
                ", userNameFlag=" + userNameFlag +
                ", passwordFlag=" + passwordFlag +
                ", willRetain=" + willRetain +
                ", willQos=" + willQos +
                ", willFlag=" + willFlag +
                ", cleanSession=" + cleanSession +
                ", reserved=" + reserved +
                ", aliveTimeLEN=" + aliveTimeLEN +
                ", aliveTimeSize=" + aliveTimeSize +
                ", aliveTimeMSB=" + aliveTimeMSB +
                ", aliveTimeLSB=" + aliveTimeLSB +
                ", contentDataHeadLEN=" + contentDataHeadLEN +
                ", contentDataSize=" + contentDataSize +
                ", contentDataMSB=" + contentDataMSB +
                ", contentDataLSB=" + contentDataLSB +
                ", contentData='" + contentData + '\'' +
                '}';
    }
}
