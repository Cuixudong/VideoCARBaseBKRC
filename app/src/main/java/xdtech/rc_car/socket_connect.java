package xdtech.rc_car;

/**
 * Created by Administrator on 2017/9/25.
 */
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import android.os.Handler;
import android.os.Message;
/*
*0X55 0XAA 0Xxx    0Xxx 0Xxx 0Xxx   0Xxx     0XBB
   包头    主指令      副指令       校验和   包尾
0X01 竞赛平台停止
0X02 竞赛平台前进
0X03 竞赛平台后退
0X04 竞赛平台左转
0X05 竞赛平台右转
0X06 竞赛平台循迹
0X07 码盘清零
0X10 前三字节红外数据
0X11 后三字节红外数据
0X12 发射六字节红外数据
0X20 指示灯
0X30 蜂鸣器
0X40 保留
0X50 相框照片上翻
0X51 相框照片下翻
0X61 光源档位加 1
0X62 光源档位加 2
0X63 光源档位加 3
0X80 竞赛平台上传 AGV 智能运输机器人数据
0X90 语音识别控制命令
0XA0 循迹板恢复到默认值
0XA1 循迹板功率设置
0XA2 循迹板单路比较阈值设置命令


主指令 副指令
0X01 0X00 0X00 0X00
0X02 速度值      码盘低八位  码盘高八位
0X03 速度值      码盘低八位  码盘高八位
0X04 速度值      0X00        0X00
0X05 速度值      0X00        0X00
0X06 速度值      0X00        0X00
0X07 0X00        0X00        0X00
0X10 红外数据[1] 红外数据[2] 红外数据[3]
0X11 红外数据[4] 红外数据[5] 红外数据[6]
0X12 0X00        0X00        0X00
0X20  0X01/0X00(开/关)   0X01/0X00(开/关)       0X00
           左灯               右灯

0X30 0X01/0X00(开/关)    0X00                  0X00
0X40 保留 保留 保留
0X50 0X00 0X00 0X00
0X51 0X00 0X00 0X00
0X60 0X00 0X00 0X00
0X61 0X00 0X00 0X00
0X62 0X00 0X00 0X00
0X63 0X00 0X00 0X00
0X80   0X01/0X00          0X00            0X00
      (允许/禁止)
0X90           0X01/0X00       0X00        0X00
             (开启/关闭)
0XA0 0X00 0X00 0X00
0XA1 功率高位 功率低位 0X00
0XA2 循迹灯编号 阈值高位 阈值低位
*
速度值：取值范围为 0~100；
码盘值：取值范围为 0~65635；
功率：取值范围为 0 ~ 1024，默认值为 800；
循迹灯编号：取值范围为 0 ~ 14，与循迹板上 LED 灯编号对应；
阈值：取值范围为 0 ~ 8192，默认值为 7000
*
* 车轮旋转圈数 电机旋转圈数 脉冲数 车轮直径(mm)  路程(mm)
       1             80      160       68        213.52
*
*
* */
public class socket_connect {
    private int port = 60000;
    private DataInputStream bInputStream;
    private DataOutputStream bOutputStream;
    private Socket socket;
    private byte[] rbyte = new byte[40];
    private Handler reHandler;
    public short TYPE=0xAA;
    public short MAJOR = 0x00;
    public short FIRST = 0x00;
    public short SECOND = 0x00;
    public short THRID = 0x00;
    public short CHECKSUM=0x00;

    public void onDestory(){
        try {
            if(socket!=null&&!socket.isClosed()){
                socket.close();
                bInputStream.close();
                bOutputStream.close();
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    public void connect(Handler handler, String IP) {
        try {
            this.reHandler=handler;
            socket = new Socket(IP, port);
            bInputStream = new DataInputStream(socket.getInputStream());
            bOutputStream = new DataOutputStream(socket.getOutputStream());
            reThread.start();
        } catch (UnknownHostException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private Thread reThread = new Thread(new Runnable() {
        @Override
        public void run() {
            // TODO Auto1-generated method stub
            while (socket != null && !socket.isClosed()) {
                try {
                    bInputStream.read(rbyte);
                    Message msg = new Message();
                    msg.what = 1;
                    msg.obj = rbyte;
                    reHandler.sendMessage(msg);
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    });

    private void send()
    {
        CHECKSUM=(short) ((MAJOR+FIRST+SECOND+THRID)%256);
        // 发送数据字节数组

        final byte[] sbyte = { 0x55, (byte) TYPE, (byte) MAJOR, (byte) FIRST, (byte) SECOND, (byte) THRID ,(byte) CHECKSUM,(byte) 0xBB};
        new Thread(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                try {
                    if(socket!=null&&!socket.isClosed()){
                        bOutputStream.write(sbyte, 0, sbyte.length);
                        bOutputStream.flush();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        System.out.println("指令已经发送");
    }

    //发送语音合成
    public void send_voice(final byte [] textbyte) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                try {
                    // 发送数据字节数组
                    if (socket != null && !socket.isClosed()) {
                        bOutputStream.write(textbyte, 0, textbyte.length);
                        bOutputStream.flush();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
    public void motorrun(int sp_l, int sp_r)
    {
        int vall,valr;
        vall = sp_l;
        valr = sp_r;
        if (sp_l<0)
        {
            vall = -vall;
            vall |= 0x80;
        }else if (sp_l > 0)
        {
            vall = sp_l;
        }
        if (sp_r<0)
        {
            valr = -valr;
            valr |= 0x80;
        }else if (sp_r > 0)
        {
            valr = sp_r;
        }
        MAJOR = 0x70;
        FIRST = (byte) (vall & 0xFF);
        SECOND = (byte) (valr & 0xff);
        THRID = (byte) 0x00;
        send();
    }
//    public void FF(int sp_l, int sp_r) {
//        MAJOR = 0x71;
//        FIRST = (byte) (sp_l & 0xFF);
//        SECOND = (byte) (sp_r & 0xff);
//        THRID = (byte) 0x00;
//        send();
//    }
//    public void BB(int sp_l, int sp_r) {
//        MAJOR = 0x72;
//        FIRST = (byte) (sp_l & 0xFF);
//        SECOND = (byte) (sp_r & 0xff);
//        THRID = (byte) 0x00;
//        send();
//    }
//    public void FB(int sp_l, int sp_r) {
//        MAJOR = 0x73;
//        FIRST = (byte) (sp_l & 0xFF);
//        SECOND = (byte) (sp_r & 0xff);
//        THRID = (byte) 0x00;
//        send();
//    }
//    public void BF(int sp_l, int sp_r) {
//        MAJOR = 0x74;
//        FIRST = (byte) (sp_l & 0xFF);
//        SECOND = (byte) (sp_r & 0xff);
//        THRID = (byte) 0x00;
//        send();
//    }

    // 停车
//    public void stop() {
//        MAJOR = 0x01;
//        FIRST = 0x00;
//        SECOND = 0x00;
//        THRID = 0x00;
//        send();
//    }

    // 循迹
    public void line(int sp_n) {
        MAJOR = 0x06;
        FIRST = (byte) (sp_n & 0xFF);
        SECOND = 0x00;
        THRID = 0x00;
        send();
    }
    //循迹恢复
    public void findlinereset()
    {
        MAJOR = 0xA0;
        FIRST = 0x00;
        SECOND = 0x00;
        THRID = 0x00;
        send();
    }
    public void vice(int i){//主从车状态转换
        if(i==1){//从车状态
            TYPE=0x02;
            MAJOR = 0x80;
            FIRST = 0x01;
            SECOND = 0x00;
            THRID = 0x00;
            send();
            yanchi(500);

            TYPE=(byte) 0xAA;
            MAJOR = 0x80;
            FIRST = 0x01;
            SECOND = 0x00;
            THRID = 0x00;
            send();
            TYPE= 0x02;
        }
        else if(i==2){//主车状态
            TYPE=0x02;
            MAJOR = 0x80;
            FIRST = 0x00;
            SECOND = 0x00;
            THRID = 0x00;
            send();
            yanchi(500);

            TYPE=(byte) 0xAA;
            MAJOR = 0x80;
            FIRST = 0x00;
            SECOND = 0x00;
            THRID = 0x00;
            send();
            TYPE= 0xAA;
        }
    }

    //清零码盘
    public void clearencode(){
        MAJOR = 0x07;
        FIRST = 0x00;
        SECOND = 0x00;
        THRID = 0x00;
        send();
    }
    // 红外
    public void infrared(byte [] message)
    {
        MAJOR = 0x10;
        FIRST = message[0];
        SECOND = message[1];
        THRID = message[2];
        send();
        yanchi(800);
        MAJOR = 0x11;
        FIRST = message[3];
        SECOND = message[4];
        THRID = message[5];
        send();
        yanchi(800);
        MAJOR = 0x12;
        FIRST = 0x00;
        SECOND = 0x00;
        THRID = 0x00;
        send();
        yanchi(800);
    }

    // 双色LED灯
    public void lamp(byte command) {
        MAJOR = 0x40;
        FIRST = command;
        SECOND = 0x00;
        THRID = 0x00;
        send();
    }

    // 指示灯
    public void light(int left, int right) {
            MAJOR = 0x20;
            FIRST = (byte)left;
            SECOND = (byte)right;
            THRID = 0x00;
            send();
    }

    // 蜂鸣器
    public void buzzer(int i) {
        if (i == 1)
            FIRST = 0x01;
        else if (i == 0)
            FIRST = 0x00;
        MAJOR = 0x30;
        SECOND = 0x00;
        THRID = 0x00;
        send();
    }

    public void floatlight()
    {
        MAJOR = 0x0A;
        FIRST = 0x01;
        SECOND = 0x01;
        THRID = 0x00;
        send();
    }

    public void agven(int i)
    {
        MAJOR = 0x80;
        FIRST = (byte)i;
        SECOND = 0x00;
        THRID = 0x00;
        send();
    }

    public void voiceen(int i)
    {
        MAJOR = 0x90;
        FIRST = (byte)i;
        SECOND = 0x00;
        THRID = 0x00;
        send();
    }

    public void picture(int i) {// 图片上翻和下翻
        if (i == 0)
            MAJOR = 0x50;
        else if (i == 1)
            MAJOR = 0x51;
        FIRST = 0x00;
        SECOND = 0x00;
        THRID = 0x00;
        send();
    }

    public void gear(int i) {// 光照档位加
        if (i == 1)
            MAJOR = 0x61;
        else if (i == 2)
            MAJOR = 0x62;
        else if (i == 3)
            MAJOR = 0x63;
        FIRST = 0x00;
        SECOND = 0x00;
        THRID = 0x00;
        send();
    }

    public void fan() {// 风扇
        MAJOR = 0x70;
        FIRST = 0x00;
        SECOND = 0x00;
        THRID = 0x00;
        send();
    }
    //立体显示
    public void infrared_stereo(byte [] data){
        MAJOR = 0x10;
        FIRST =  0xff;
        SECOND = data[0];
        THRID = data[1];
        send();
        yanchi(500);
        MAJOR = 0x11;
        FIRST = data[2];
        SECOND = data[3];
        THRID = data[4];
        send();
        yanchi(500);
        MAJOR = 0x12;
        FIRST = 0x00;
        SECOND = 0x00;
        THRID = 0x00;
        send();
        yanchi(500);
    }
    public void gate(int i) {// 闸门
        byte type=(byte) TYPE;
            TYPE = 0x03;
            MAJOR = 0x01;
            FIRST = (byte)i;
            SECOND = 0x00;
            THRID = 0x00;
            send();
        TYPE = type;
    }
//    //LCD显示标志物进入计时模式
//    public void digital_close(){//数码管关闭
//        byte type=(byte) TYPE;
//        TYPE = 0x04;
//        MAJOR = 0x03;
//        FIRST = 0x00;
//        SECOND = 0x00;
//        THRID = 0x00;
//        send();
//        TYPE = type;
//    }
//    public void digital_open(){//数码管打开
//        byte type=(byte) TYPE;
//        TYPE = 0x04;
//        MAJOR = 0x03;
//        FIRST = 0x01;
//        SECOND = 0x00;
//        THRID = 0x00;
//        send();
//        TYPE = type;
//    }
//    public void digital_clear(){//数码管清零
//        byte type=(byte) TYPE;
//        TYPE = 0x04;
//        MAJOR = 0x03;
//        FIRST = 0x02;
//        SECOND = 0x00;
//        THRID = 0x00;
//        send();
//        TYPE = type;
//    }
    public void digital_dic(int dis){//LCD显示标志物第二排显示距离
        byte type=(byte) TYPE;
        TYPE = 0x04;
        MAJOR = 0x04;
        FIRST = 0x00;
        SECOND = (byte) ((dis/100)&0xF);
        THRID = (byte) ((((dis%100)/10)<<4)|((dis%10)&0xF));
        send();
        TYPE = type;
    }
    public void digital(int i, int one, int two, int three) {// 数码管
        byte type=(byte) TYPE;
        TYPE = 0x04;
        if (i == 1) {//数据写入第一排数码管
            MAJOR = 0x01;
            FIRST = (byte) one;
            SECOND = (byte) two;
            THRID = (byte) three;
        } else if (i == 2) {//数据写入第二排数码管
            MAJOR = 0x02;
            FIRST = (byte) one;
            SECOND = (byte) two;
            THRID = (byte) three;
        }
        send();
        TYPE = type;
    }
    public void arm(int MAIN, int KIND, int COMMAD, int DEPUTY){
        MAJOR = (short) MAIN;
        FIRST = (byte)KIND ;
        SECOND = (byte) COMMAD;
        THRID = (byte) DEPUTY;
        send();
    }

    public void TFT_LCD(int MAIN, int KIND, int COMMAD, int DEPUTY)
    {
        byte type=(byte) TYPE;
        TYPE = (short)0x0B;
        MAJOR = (short) MAIN;
        FIRST = (byte)KIND ;
        SECOND = (byte) COMMAD;
        THRID = (byte) DEPUTY;
        send();
        TYPE = type;
    }

    public void mystruct(byte[] a)
    {
        TYPE = (byte)   a[0];
        MAJOR = (byte)  a[1];
        FIRST = (byte)  a[2] ;
        SECOND = (byte) a[3];
        THRID = (byte)  a[4];
        send();
    }

    public void magnetic_suspension(int MAIN, int KIND, int COMMAD, int DEPUTY)
    {
        byte type=(byte) TYPE;
        TYPE = (short)0x0A;
        MAJOR = (short) MAIN;
        FIRST = (byte)KIND ;
        SECOND = (byte) COMMAD;
        THRID = (byte) DEPUTY;
        send();
        TYPE = type;
    }

    public void TFTsendCarnum(char[] carnum)
    {
        TYPE = (short)0x0B;
        MAJOR = 0x20;
        FIRST =  (byte)carnum[0];
        SECOND = (byte)carnum[1];
        THRID = (byte)carnum[2];
        send();
        yanchi(100);
        MAJOR = 0x21;
        FIRST =  (byte)carnum[3];
        SECOND = (byte)carnum[4];
        THRID = (byte)carnum[5];
        send();
    }

    // 沉睡
    public void yanchi(int time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
