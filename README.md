### MQTT协议测试案例纯Java IO
## 说明
* test01为服务器端

* 测试的客户端用paho软件

* test是用来测试协议解释与组合



特别的注意关建点Remaining Length长度是4个字节Eclipse Paho MQTT就是那样,虽然它是可变的，但设计软件时一定要设计一个长度要不是没法知道一个请求的终止点，在没有结束的标志下

测试数据开始----------------------------------------------------------------  
a[0] = 16;   
a[1] = 29;//剩余长度 特别注意现在字节小正好为29是和一个byte相等,如果可变头部+消息体>=128，它就是不等于实际了是由两位字节以上表示    
a[2] = 0;//协议名称开始   
a[3] = 4;   
a[4] = 77;   
a[5] = 81;   
a[6] = 84;   
a[7] = 84;   
a[8] = 4;//协议版本号   
a[9] = 2;//连接标志    
a[10] = 0;//保持存活计数器开始    
a[11] = 60;    
a[12] = 0;//消息体开始   
a[13] = 17;    
a[14] = 112;    
a[15] = 97;    
a[16] = 104;    
a[17] = 111;    
a[18] = 56;    
a[19] = 49;    
a[20] = 50;    
a[21] = 54;    
a[22] = 55;    
a[23] = 48;    
a[24] = 48;   
a[25] = 51;    
a[26] = 50;   
a[27] = 49;    
a[28] = 50;     
a[29] = 54;    
a[30] = 50;     
测试数据结束----------------------------------------------------------------

说明开始---------------------------------------------------      
固定头部 fixHeader:16    
*	消息类型 messageType:1    
	打开标志 dupFlag:false   
	服务质量 qosLevel:0   
	保持 retain:false   

剩余长度 remainingLength:29    

协议名称内容protocolName:MQTT   
*	 protocolNameMSB:0    
	 protocolNameLSB:4   
  
协议版本号 version:4   

连接标志 connectFlags:2    
*	用户名标志 userNameFlag:false   
	密码标志 passwordFlag:false   
	将保留 willRetain:false    
	将保留 willQoS:0    
	将标志 willFlag:false   
	清空Session标志 cleanSession:true   
	保留的标志 reserved:false   

保持存活计数器aliveTimeSize:60   
*	 keepAliveTimeMSB:0   
	 keepAliveTimeLSB:60   

消息体内容contentData:paho8126700321262   
*	 messageIdMSB:0    
	 messageIdLSB:17   

说明结束---------------------------------------------------   