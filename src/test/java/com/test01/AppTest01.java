package com.test01;

import org.junit.Test;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class AppTest01 {
    @Test
    public void test(){
        Map<Integer, Object> map = new HashMap<Integer, Object>();
        map.put(0, 0000);
        map.put(1, 1111);
        map.put(2, 2222);
        map.put(3, 3333);
        //Map集合循环遍历方式一  
        System.out.println("第一种：通过Map.keySet()遍历key和value：");
        for(Integer key:map.keySet()){//keySet获取map集合key的集合  然后在遍历key即可
            String value = map.get(key).toString();
            System.out.println("key:"+key+" vlaue:"+value);
        }
    }
    public void test7(){
        int a = 12;
        int b = -64;

        int c = a & 0xff;
        int d = b & 0xff;
        System.out.println(c);
        System.out.println(d);

    }
    public void test06(){
        byte[] a = new byte[2];
        a[0] = 10;
        a[1] = 11;
        byte[] b = new byte[3];
        b[0] = 20;
        b[1] = 21;
        b[2] = 22;
        byte[] c = new byte[5];
        System.arraycopy(a,0,c,0,2);
        System.arraycopy(b,0,c,2,3);
        for (int i = 0; i < c.length; i++) {
            System.out.println("c["+ i +"]" + c[i]);

        }
    }
    public void test05() throws Exception{
        // 模拟客户端写入
        ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(arrayOutputStream);
        dataOutputStream.write(0xff);
        dataOutputStream.write(0xff);
        dataOutputStream.write(0xff);
        dataOutputStream.write(0x7f);

        InputStream arrayInputStream = new ByteArrayInputStream(arrayOutputStream.toByteArray());

        // 模拟服务器/客户端解析
        System. out.println( "result is " + bytes2Length(arrayInputStream));
    }

    /**
     * 转化字节为 int类型长度
     * @param in
     * @return
     * @throws IOException
     */
    private static int bytes2Length(InputStream in) throws IOException {
        int multiplier = 1;
        int length = 0;
        int digit = 0;
        do {
            digit = in.read(); //一个字节的有符号或者无符号，转换转换为四个字节有符号 int类型
            length += (digit & 0x7f) * multiplier;
            multiplier *= 128;
        } while ((digit & 0x80) != 0);

        return length;
    }
    public void test04(){
//目标：构选一个byte 0110,01,10 表示它是邮三个负正数切割拼成
//负整a: 11111111,11111111,1 .0110. 000,01111111      (a = -20353)
                                                    //它的求法 .0110. 000,01111111
                                                    //从符号位后一位开始取后.1001. 111,10000000  (反码)
                                                    //再加1就是补码.1001. 111,10000001         (补码)
                                                    //.1001. 111,10000001 就是20353
//负整b: 11111111,11111111,1 .01. 00000,00000000      (b = -24576)
//负整c: 11111111,11111111,.10. 000000,01000000       (c = -32704)
int a = -20353;
int b = -24576;
int c = -32704;
System.out.println(Integer.toBinaryString(a));
System.out.println(Integer.toBinaryString(b));
System.out.println(Integer.toBinaryString(c));
//思路
//总知不管是取数据还是织装数据就都先&与后>><<移的思路
//1.数据模型xxxx|--------|xxxxx
//2.为了防止前部的xx数据进来8位区一定不能先>>移动,所以第一步先求 & 与同时把后面的都变成0
//3.目标是0110. 000,00000000 所以a取.0110. 000,01111111  实现:a & (.1111. 000,00000000) (十六进制0x7800)
//4.如果3的目标是大于byte8位直接右移动>>让它以头开始剩8位
//5.(a & 0x7FFF) >> 7;  //成了0110. 000,0
//6.同样的办法对b操作 (b & 0x6000) >> 11; 右移动>>让它以头开始剩8-前面a占的4位剩4位
//6.同样的办法对c操作 (c & 0xC000) >> 14; 右移动>>让它以头开始剩8-前面a占的4位前面b占的2位剩2位
int d = (a & 0x7FFF) >> 7 | (b & 0x6000) >> 11 | (c & 0xC000) >> 14;
System.out.println(d);

    }
    public void test03(){
        byte[] a = new byte[2];
        a[0] = (byte) 0;
        a[1] = (byte) 6;
        int value;
        value =  (((a[0] & 0xFF)<<8)
                | (a[1] & 0xFF)
                | ((0 & 0xFF)<<16)
                | ((0 & 0xFF)<<24));
        System.out.println(value);
    }
    public void test02(){
        int val = 59137;
        byte[] a;
        int digit;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        do {
            digit = val % 128;
            val = val / 128;
            if (val > 0)
                digit = digit | 0x80;
            out.write((byte)digit);
            //System.out.println("----test:" + digit);
            //System.out.println(Integer.toBinaryString(digit));
        } while (val > 0);
        a = out.toByteArray();
        System.out.println("------------------------------");
        byte[] b = new byte[3];
        for (int i = 0; i < a.length; i++) {
            b[i] = a[i];
            System.out.println((int)(a[i] & 0xff));
            System.out.println(Integer.toBinaryString(a[i]));
        }
        System.out.println("------------------------------");
        for (int i = 0; i < b.length; i++) {
            System.out.println(b[i]);
            System.out.println(Integer.toBinaryString(b[i]));
        }


    }

    public void test1(){
//        int a = 3;
//        boolean b = true; //保持
//        int myInt = b ? 1 : 0;
//        int c = 1;
//        int d = 1;
        //int e = ((a & 0xF) & 0xFF) <<4 | ((b & 0x1) & 0xF) <<3  | ((c & 0x3) & 0x7) <<1  | (b & 0x1);
        int a = 1;
        int b = 1;
        int c = 1;
        int d = 3;
        int e = 1;
        int f = 1;
        int g = 1;
        int k = (((a & 0x1) & 0xFF) <<7 | ((b & 0x1) & 0x7F) <<6  | ((c & 0x1) & 0x3F) <<5  | ((d & 0x3) & 0x1F) <<3 | ((e & 0x1) & 0x7) <<2 | ((f & 0x1) & 0x3) <<1 | (g & 0x1));

        System.out.println(k);
        byte tt = (byte) 2;

//        int b = 1;
//        (b & 0x1);
//        int c = 2;
//        (c & 0x3);
//        int d = 2;
//        (b & 0x1);


    }
}
