package com.test01;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.concurrent.ConcurrentLinkedQueue;

public class MyServerHandler implements Runnable{
    private Socket socket;
    private ReciveTask reciveTask;
    private SendTask sendTask;
    private volatile ConcurrentLinkedQueue<BasicProtocol> dataQueue = new ConcurrentLinkedQueue<>();
    public MyServerHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            //启用接收数据线程
            reciveTask = new ReciveTask(socket.getInputStream());
            reciveTask.start();
            //启用发送数据线程
            sendTask = new SendTask(socket.getOutputStream());
            sendTask.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //接收数据
    public class ReciveTask extends Thread{
        private InputStream inputStream;
        private boolean isCancle;
        public ReciveTask(InputStream inputStream) {
            this.inputStream = inputStream;
        }
        @Override
        public void run() {
            while (!isCancle){
                if(!isConnectedFail()){
                    isCancle = true;
                    break;
                }
                BasicProtocol dataProtocol = SocketUnit.redFromStream(inputStream);

                if (dataProtocol != null){
                    if (dataProtocol.getProtocolType() == 1){
                        System.out.println(dataProtocol.printProtocol());
                        ConnectAckProtocol connectAck = new ConnectAckProtocol();
                        connectAck.setMessageType(2);//消息类型
                        connectAck.setDupFlag(false);//打开标志
                        connectAck.setQosLevel(0);//服务质量
                        connectAck.setRetain(false);//保持
                        connectAck.setRemainingLength(2);//-------注意它设置
                        connectAck.setSession(false);//连接确认标志的当前会话
                        connectAck.setConnectReturnCode(0);//连接返回码
                        dataQueue.offer(connectAck);
                        toNotifyAll(dataQueue); //唤醒发送线程
                    }else if (dataProtocol.getProtocolType() == 12) {
                        System.out.println(dataProtocol.printProtocol());
                        PingAckProtocol pingAck = new PingAckProtocol();
                        pingAck.setMessageType(13);//消息类型
                        pingAck.setDupFlag(false);//打开标志
                        pingAck.setQosLevel(0);//服务质量
                        pingAck.setRetain(false);//保持
                        pingAck.setRemainingLength(0);//-------注意它设置
                        dataQueue.offer(pingAck);
                        toNotifyAll(dataQueue); //唤醒发送线程
                    }else if (dataProtocol.getProtocolType() == 14) {
                        System.out.println(dataProtocol.printProtocol());
                        System.out.println("请求关闭连接");
                    }else if (dataProtocol.getProtocolType() == 3) {
                        System.out.println(dataProtocol.printProtocol());
                        PublishProtocol publishProtocol = (PublishProtocol) dataProtocol;
                        int qosLevel = publishProtocol.getQosLevel();//取得服务质量
                        int messageIdentifierSize = publishProtocol.getMessageIdentifierSize();//取得消息标示符
                        if(qosLevel !=0) {
                            PublishAckProtocol publishAck = new PublishAckProtocol();
                            publishAck.setMessageType(4);//消息类型
                            publishAck.setDupFlag(false);//打开标志
                            publishAck.setQosLevel(0);//服务质量
                            publishAck.setRetain(false);//保持
                            publishAck.setRemainingLength(2);//-------注意它设置
                            publishAck.setMessageIdentifierSize(messageIdentifierSize);//报文标识符
                            dataQueue.offer(publishAck);
                            toNotifyAll(dataQueue); //唤醒发送线程
                        }
                    }else if (dataProtocol.getProtocolType() == 8) {
                        System.out.println(dataProtocol.printProtocol());
                        SubscribeProtocol subscribeProtocol = (SubscribeProtocol) dataProtocol;
                        int messageIdentifierSize = subscribeProtocol.getMessageIdentifierSize();//取得消息标示符
                        SubscribeAckProtocol subscribeAck = new SubscribeAckProtocol();
                        subscribeAck.setMessageType(9);//消息类型
                        subscribeAck.setDupFlag(false);//打开标志
                        subscribeAck.setQosLevel(0);//服务质量
                        subscribeAck.setRetain(false);//保持
                        int topicNumber = subscribeProtocol.getList().size();
                        subscribeAck.setRemainingLength(topicNumber + 2);//-------注意它是根据订阅主题过滤器个数设置3
                        subscribeAck.setMessageIdentifierSize(messageIdentifierSize);//报文标识符
                        SubscribeAckProtocol.GrantedQoS grantedQoS = subscribeAck.new GrantedQoS();
                        //根据订报文标识符和QoS做相应的回复
                        for (int i = 0; i < subscribeProtocol.getList().size(); i++) {
                            grantedQoS.setGrantedQoSReturnCode(subscribeProtocol.getList().get(i).getTopicQosLevel());
                            subscribeAck.getList().add(grantedQoS);
                        }
                        dataQueue.offer(subscribeAck);
                        toNotifyAll(dataQueue); //唤醒发送线程
                    }else if (dataProtocol.getProtocolType() == 10) {
                        System.out.println(dataProtocol.printProtocol());
                        UnSubscribeProtocol unSubscribeProtocol = (UnSubscribeProtocol) dataProtocol;
                        int messageIdentifierSize = unSubscribeProtocol.getMessageIdentifierSize();//取得消息标示符
                        UnSubscribeAckProtocol unSubscribeAck = new UnSubscribeAckProtocol();
                        unSubscribeAck.setMessageType(11);//消息类型
                        unSubscribeAck.setDupFlag(false);//打开标志
                        unSubscribeAck.setQosLevel(0);//服务质量
                        unSubscribeAck.setRetain(false);//保持
                        unSubscribeAck.setRemainingLength(2);//-------注意它个数设置2
                        unSubscribeAck.setMessageIdentifierSize(messageIdentifierSize);//报文标识符
                        dataQueue.offer(unSubscribeAck);
                        toNotifyAll(dataQueue); //唤醒发送线程
                    }

                }else {
                    System.out.println("client is offline...");
                    break;
                }
            }
            SocketUnit.closeInputStream(inputStream);
        }
    }
    //发送数据
    public class SendTask extends Thread{
        private OutputStream outputStream;
        private boolean isCancle;
        public SendTask(OutputStream outputStream) {
            this.outputStream = outputStream;
        }

        @Override
        public void run() {
            while (!isCancle){
                if(!isConnectedFail()){
                    isCancle = true;
                    break;
                }
                //无界线程安全队列轮询
                BasicProtocol procotol = dataQueue.poll();
                if (procotol == null) {
                    //进入队列待待
                    toWaitAll(dataQueue);
                } else if (outputStream != null) {
                    synchronized (outputStream) {
                        SocketUnit.write2Stream(procotol, outputStream);
                    }
                }
            }
            SocketUnit.closeOutputStream(outputStream);
        }
    }
    //队列等待
    public void toWaitAll(Object o) {
        synchronized (o) {
            try {
                o.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    //唤醒等待
    public void toNotifyAll(Object obj) {
        synchronized (obj) {
            obj.notifyAll();
        }
    }
    public void stopServerThread() {
        if (reciveTask != null) {
            reciveTask.isCancle = true;
            reciveTask.interrupt();
            if (reciveTask.inputStream != null) {
                SocketUnit.closeInputStream(reciveTask.inputStream);
                reciveTask.inputStream = null;
            }
            reciveTask = null;
        }

        if (sendTask != null) {
            sendTask.isCancle = true;
            sendTask.interrupt();
            if (sendTask.outputStream != null) {
                synchronized (sendTask.outputStream) {//防止写数据时停止，写完再停
                    sendTask.outputStream = null;
                }
            }
            sendTask = null;
        }
    }
    private boolean isConnectedFail() {
        if (socket.isClosed() || !socket.isConnected()) {
            MyServerHandler.this.stopServerThread();
            System.out.println("socket closed...");
            return false;
        }
        return true;
    }
}
