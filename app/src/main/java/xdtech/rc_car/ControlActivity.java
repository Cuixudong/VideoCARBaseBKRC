package xdtech.rc_car;

/**
 * Created by Administrator on 2017/9/26.
 */
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import com.bkrcl.control_car_video.camerautil.CameraCommandUtil;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.DecodeHintType;
import com.google.zxing.FormatException;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.DhcpInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.format.Formatter;
import android.text.format.Time;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Toast;

public class ControlActivity extends Activity implements SensorEventListener{

    private TextView voiceText,datatext;
    private ImageView image_show;
    private Button morefunc,getphoto,grabutton,scancode;
    private RockerView rockerView;
    private SeekBar seekBarleft,seekBarright;
    private int sls,srs;

    final String[] findlinesel = new String[]
            {
                    "第1路","第2路","第3路",
                    "第4路","第5路","第6路",
                    "第7路","第8路","第9路",
                    "第10路","第11路","第12路",
                    "第13路","第14路","第15路",
            };
    final String[] sendtestdata = new String[]
            {
                    "0x00", "0x01", "0x02", "0x03", "0x04", "0x05", "0x06", "0x07", "0x08", "0x09", "0x0a", "0x0b", "0x0c", "0x0d", "0x0e", "0x0f",
                    "0x10", "0x11", "0x12", "0x13", "0x14", "0x15", "0x16", "0x17", "0x18", "0x19", "0x1a", "0x1b", "0x1c", "0x1d", "0x1e", "0x1f",
                    "0x20","0x21","0x22","0x23","0x24","0x25","0x26","0x27","0x28","0x29","0x2a","0x2b","0x2c","0x2d","0x2e","0x2f",
                    "0x30","0x31","0x32","0x33","0x34","0x35","0x36","0x37","0x38","0x39","0x3a","0x3b","0x3c","0x3d","0x3e","0x3f",
                    "0x40","0x41","0x42","0x43","0x44","0x45","0x46","0x47","0x48","0x49","0x4a","0x4b","0x4c","0x4d","0x4e","0x4f",
                    "0x50","0x51","0x52","0x53","0x54","0x55","0x56","0x57","0x58","0x59","0x5a","0x5b","0x5c","0x5d","0x5e","0x5f",
                    "0x60","0x61","0x62","0x63","0x64","0x65","0x66","0x67","0x68","0x69","0x6a","0x6b","0x6c","0x6d","0x6e","0x6f",
                    "0x70","0x71","0x72","0x73","0x74","0x75","0x76","0x77","0x78","0x79","0x7a","0x7b","0x7c","0x7d","0x7e","0x7f",
                    "0x80","0x81","0x82","0x83","0x84","0x85","0x86","0x87","0x88","0x89","0x8a","0x8b","0x8c","0x8d","0x8e","0x8f",
                    "0x90","0x91","0x92","0x93","0x94","0x95","0x96","0x97","0x98","0x99","0x9a","0x9b","0x9c","0x9d","0x9e","0x9f",
                    "0xa0","0xa1","0xa2","0xa3","0xa4","0xa5","0xa6","0xa7","0xa8","0xa9","0xaa","0xab","0xac","0xad","0xae","0xaf",
                    "0xb0","0xb1","0xb2","0xb3","0xb4","0xb5","0xb6","0xb7","0xb8","0xb9","0xba","0xbb","0xbc","0xbd","0xbe","0xbf",
                    "0xc0","0xc1","0xc2","0xc3","0xc4","0xc5","0xc6","0xc7","0xc8","0xc9","0xca","0xcb","0xcc","0xcd","0xce","0xcf",
                    "0xd0","0xd1","0xd2","0xd3","0xd4","0xd5","0xd6","0xd7","0xd8","0xd9","0xda","0xdb","0xdc","0xdd","0xde","0xdf",
                    "0xe0","0xe1","0xe2","0xe3","0xe4","0xe5","0xe6","0xe7","0xe8","0xe9","0xea","0xeb","0xec","0xed","0xee","0xef",
                    "0xf0","0xf1","0xf2","0xf3","0xf4","0xf5","0xf6","0xf7","0xf8","0xf9","0xfa","0xfb","0xfc","0xfd","0xfe","0xff",
            };
    final String[] tftselpicdata = new String[]
            {
                    "第1张图片", "第2张图片", "第3张图片", "第4张图片", "第5张图片", "第6张图片",
                    "第7张图片", "第8张图片", "第9张图片", "第10张图片", "第11张图片", "第12张图片",
                    "第13张图片", "第14张图片", "第15张图片", "第16张图片", "第17张图片", "第18张图片",
                    "第19张图片", "第20张图片", "第21张图片", "第22张图片", "第23张图片", "第24张图片",
                    "第25张图片","第26张图片","第27张图片", "第28张图片","第29张图片","第30张图片","第31张图片",
            };
    final String[] itshowshape = new String[]
            {
                    "矩形","圆形","三角形","菱形","梯形","饼图","靶图","条形图",
            };
    final String[] itshowcolor = new String[]
            {
                    "红","绿","蓝","黄","紫","青","黑","白",
            };
    final String[] itshowroad = new String[]
            {
                    "隧道有事故，请绕行","前方施工，请绕行",
            };
    final String[] speakvoice = new String[]
            {
                    "语音驾驶","向右转弯","禁止右转","左侧行驶","左行被禁","原地掉头",
            };
    // 图片区域滑屏监听点击和弹起坐标位置
    private final int MINLEN = 30;
    private float x1 = 0;
    private float x2 = 0;
    private float y1 = 0;
    private float y2 = 0;
    private int state_camera;
    private WifiManager wifiManager;
    // 服务器管理器
    private DhcpInfo dhcpInfo;
    // 小车ip
    private String IPCar;
    // 摄像头IP
    private String IPCamera = "bkrcjk.eicp.net:88";
    private Sensor sensor;
    private SensorManager sensorManager;
    private byte[] mByte = new byte[11];
    public socket_connect socket_connect;
    private float touchx,touchy;
    private static boolean isexti=false;
    private static boolean hastask=false;
    private static boolean gravity = false;
    private static float sensorx,sensory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        image_show = (ImageView)findViewById(R.id.image_show);
        image_show.setOnTouchListener(new ontouchlistener1());
        morefunc = (Button)findViewById(R.id.morefunc);
        seekBarleft = (SeekBar)findViewById(R.id.seekBarleft);
        seekBarright = (SeekBar)findViewById(R.id.seekBarright);
        seekBarleft.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                sls = progress;
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                seekBarleft.setProgress(100);
            }
        });
        seekBarright.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                srs = progress;
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                seekBarright.setProgress(100);
            }
        });
        morefunc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ZigbeeController();
            }
        });
        getphoto = (Button)findViewById(R.id.cameraphoto);
        grabutton = (Button)findViewById(R.id.gra_but);
        scancode = (Button)findViewById(R.id.scancode);
        scancode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                qrHandler.sendEmptyMessage(10);
            }
        });
        getphoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bitmap != null)
                {
                    saveBitmap("abc",bitmap);
                }
            }
        });
        grabutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gravity=!gravity;
                if (gravity)
                {
                    grabutton.setText("重力:打开");
                }else
                {
                    grabutton.setText("重力:关闭");
                }
            }
        });
        datatext = (TextView)findViewById(R.id.Data_show);
        rockerView = (RockerView)findViewById(R.id.car_ctr);
        rockerView.setRockerChangeListener(new RockerView.RockerChangeListener() {
            @Override
            public void report(float x, float y) {
                touchx = x;
                touchy = y;
            }
        });
        sensorManager = (SensorManager) this.getSystemService(Service.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        // 得到服务器的IP地址
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        dhcpInfo = wifiManager.getDhcpInfo();
        IPCar = Formatter.formatIpAddress(dhcpInfo.gateway);

        //广播接收器注册
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(A_S);
        registerReceiver(myBroadcastReceiver, intentFilter);
        // 搜索摄像头图片工具
        cameraCommandUtil = new CameraCommandUtil();

        Intent intent = new Intent();
        intent.setClass(ControlActivity.this, SearchService.class);
        startService(intent);

        socket_connect = new socket_connect();		//实例化socket连接类
        new Thread(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                socket_connect.connect(rehHandler, IPCar);
            }
        }).start();
        timer_start();

    }

    private void timer_start()
    {
        final Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                Message msg =new Message();
                msg.what = 12;
                handler.sendMessage(msg);
            }
        }, 30, 20);
    }
    int tx,ty;int wheell,wheelr;int sx,sy;
    private boolean sendtypeflag = false;
    private Handler handler =new Handler()
    {
        public void handleMessage(Message msg)
        {
            if (msg.what == 12)
            {
                if (gravity)
                {
                    sx = (int) (sensorx*16)/2;
                    sy = (int) (sensory*16);
                    if (sx>=100)sx=100;
                    if (sy>=100)sy=100;
                    if (sx<=-100)sx=-100;
                    if (sy<=-100)sy=-100;
                    wheell = -(sy+sx);
                    wheelr = -sy+sx;
                }else
                {
                    tx = (int)touchx/2;
                    ty = (int)touchy;
                    if (tx>=100)tx=100;
                    if (ty>=100)ty=100;
                    if (tx<=-100)tx=-100;
                    if (ty<=-100)ty=-100;
                    wheell = -(ty)+tx;
                    wheelr = -(ty)-tx;
                }
                if ((wheell != 0)||(wheelr != 0))//&&与|| ？？？？哎
                {
                    sendtypeflag = false;
                }
                if (!sendtypeflag)
                {
                    if (wheell==0  &&  wheelr == 0)
                    {
                        socket_connect.motorrun(wheell,wheelr);
                        sendtypeflag = true;
                    }else
                    {
                        socket_connect.motorrun(wheell,wheelr);
                    }
                }else if (sendtypeflag) {}
            }

        }
    };
    public String getMyTime() {
        SimpleDateFormat format =new SimpleDateFormat("yyyy年MM月dd日 hh时：mm分：ss秒");
        String date =format.format(new Date());
        return date;
    }
    public void saveBitmap(String bitName,Bitmap mBitmap){

//        Time time  =  new Time();
//        time.setToNow();
//        String str_time = time.format("%Y-%m-%d %H:%M:%S");

        String str_time = getMyTime();

        File f = new File("/storage/emulated/0/BKRC/" + str_time + ".png");
        if (f.exists())
        {
            f.delete();
        }
        try {
            f.createNewFile();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            //DebugMessage.put("在保存图片时出错："+e.toString());
            Toast.makeText(this, "保存图片失败", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
        FileOutputStream fOut = null;
        try {
            fOut = new FileOutputStream(f);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        mBitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
        try {
            fOut.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            fOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Toast.makeText(this, "保存图片成功\n"+"/storage/emulated/0/百科融创科技/" + str_time + ".png", Toast.LENGTH_SHORT).show();
    }
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_UI);
    }
    public void onAccuracyChanged(Sensor sensor, int accuracy) { }
    public void onSensorChanged(SensorEvent event) {
        float[] values = event.values;
        sensorx = values[0] ;
        sensory = values[1] ;
    }
    // 广播名称
    public static final String A_S = "com.a_s";
    // 摄像头工具
    private CameraCommandUtil cameraCommandUtil;
    // 广播接收器接受SearchService搜索的摄像头IP地址加端口
    private BroadcastReceiver myBroadcastReceiver = new BroadcastReceiver() {
        public void onReceive(Context arg0, Intent arg1) {
            IPCamera = arg1.getStringExtra("IP");
            phThread.start();
        }
    };
    // 图片
    private Bitmap bitmap;
    // 得到当前摄像头的图片信息
    public void getBitmap() {
        bitmap = cameraCommandUtil.httpForImage(IPCamera);
        phHandler.sendEmptyMessage(10);
    }
    // 显示图片
    public Handler phHandler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == 10) {
                image_show.setImageBitmap(bitmap);
            }
        }
    };
    public boolean flag_camera;
    // 开启线程接受摄像头当前图片
    private Thread phThread = new Thread(new Runnable() {
        public void run() {
            while (true) {
                getBitmap();
                switch (state_camera) {
                    case 1:
                        cameraCommandUtil.postHttp(IPCamera, 0, 1);//抬头
                        break;
                    case 2:
                        cameraCommandUtil.postHttp(IPCamera, 2, 1);//低头
                        break;
                    case 3:
                        cameraCommandUtil.postHttp(IPCamera, 4, 1);//左滑
                        break;
                    case 4:
                        cameraCommandUtil.postHttp(IPCamera, 6, 1);//右滑
                        break;
                    // /预设位1到3
                    case 5:
                        cameraCommandUtil.postHttp(IPCamera, 30, 0);
                        break;
                    case 6:
                        cameraCommandUtil.postHttp(IPCamera, 32, 0);
                        break;
                    case 7:
                        cameraCommandUtil.postHttp(IPCamera, 34, 0);
                        break;
                    case 8:
                        cameraCommandUtil.postHttp(IPCamera, 31, 0);
                        break;
                    case 9:
                        cameraCommandUtil.postHttp(IPCamera, 33, 0);
                        break;
                    case 10:
                        cameraCommandUtil.postHttp(IPCamera, 35, 0);
                        break;
                    default:
                        break;
                }
                state_camera = 0;
            }

        }
    });
    // 接受传感器
    long psStatus = 0;// 状态
    long UltraSonic = 0;// 超声波
    long Light = 0;// 光照
    long CodedDisk = 0;// 码盘值
    String Camera_show_ip = null;
    // 接受显示小车发送的数据
    private Handler rehHandler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                mByte = (byte[]) msg.obj;
                if (mByte[0] == 0x55) {
                    // 光敏状态
                    psStatus = mByte[3] & 0xff;
                    // 超声波数据
                    UltraSonic = mByte[5] & 0xff;
                    UltraSonic = UltraSonic << 8;
                    UltraSonic += mByte[4] & 0xff;
                    // 光照强度
                    Light = mByte[7] & 0xff;
                    Light = Light << 8;
                    Light += mByte[6] & 0xff;
                    // 码盘
                    CodedDisk = mByte[9] & 0xff;
                    CodedDisk = CodedDisk << 8;
                    CodedDisk += mByte[8] & 0xff;
                    Camera_show_ip = IPCamera.substring(0, 14);
                    if (mByte[1] == (byte) 0xaa) {
                            datatext.setText("WIFIIP:"+IPCar+"\n"+"CameraIP:"+Camera_show_ip+"\n" + "主车各状态信息:\n超声波:" + UltraSonic
                                    +"mm\n光照:" + Light + "lx\n码盘:" + CodedDisk +"\n光敏状态:" + psStatus +"\n状态:" + (mByte[2]));
                    }
//                    if(mByte[1] == (byte) 0x02)
//                    {
//                            // 显示数据
//                            datatext.setText("WIFI模块IP:"+IPCar+"\n"+"从车各状态信息:" + "超声波:" + UltraSonic
//                                    + "mm 光照:" + Light + "lx" + "码盘:" + CodedDisk
//                                    + "光敏状态:" + psStatus + "状态:" + (mByte[2]));
//                    }
                }
            }
        }
    };

    private Timer timer;
    private String result_qr;
    // 二维码、车牌处理
    Handler qrHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 10:
                    timer = new Timer();
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            Result result = null;
                            RGBLuminanceSource rSource = new RGBLuminanceSource(bitmap);
                            try {
                                BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(rSource));
                                Map<DecodeHintType, String> hint = new HashMap<DecodeHintType, String>();
                                hint.put(DecodeHintType.CHARACTER_SET, "utf-8");
                                QRCodeReader reader = new QRCodeReader();
                                result = reader.decode(binaryBitmap, hint);
                                if (result.toString() != null) {
                                    result_qr = result.toString();
                                    timer.cancel();
                                    qrHandler.sendEmptyMessage(20);
                                }
                                System.out.println("正在识别");
                            } catch (NotFoundException e) {
                                e.printStackTrace();
                            } catch (ChecksumException e) {
                                e.printStackTrace();
                            } catch (FormatException e) {
                                e.printStackTrace();
                            }
                        }
                    }, 0, 200);
                    break;
                case 20:
                    Toast.makeText(ControlActivity.this, result_qr, Toast.LENGTH_LONG).show();
                    break;
                default:
                    break;
            }
        };
    };

    private class ontouchlistener1 implements OnTouchListener
    {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            // TODO 自动生成的方法存根
            switch (event.getAction() & MotionEvent.ACTION_MASK) {
                // 点击位置坐标
                case MotionEvent.ACTION_DOWN:
                    x1 = event.getX();y1 = event.getY();
                    break;
                // 弹起坐标
                case MotionEvent.ACTION_MOVE:
                    x2 = event.getX();y2 = event.getY();
                    float xx = x1 > x2 ? x1 - x2 : x2 - x1;
                    float yy = y1 > y2 ? y1 - y2 : y2 - y1;
                    // 判断滑屏趋势
                    if (xx > yy) {
                        if ((x1 > x2) && (xx > MINLEN)) {        // left
                            state_camera = 4;
                        } else if ((x1 < x2) && (xx > MINLEN)) { // right
                            state_camera = 3;
                        }
                    } else {
                        if ((y1 > y2) && (yy > MINLEN)) {        // down
                            state_camera = 2;
                        } else if ((y1 < y2) && (yy > MINLEN)) { // up
                            state_camera = 1;
                        }
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    x1 = 0;x2 = 0;y1 = 0;y2 = 0;
                    break;
            }
            return true;
        }
    }

    private class ontouchlistener2  implements OnTouchListener
    {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            // TODO Auto-generated method stub
            if(event.getAction() == MotionEvent.ACTION_DOWN)		//按键按下时
            {
                switch(v.getId())
                {
                }
            }
            if (event.getAction() == MotionEvent.ACTION_UP) {	 //按键离开时
                switch(v.getId())
                {
                }
            }
            return true;
        }
    }

    //从string中得到short数据数组
    private short[] StringToBytes(String licString) {
        if (licString == null || licString.equals("")) {
            return null;
        }
        licString = licString.toUpperCase();
        int length = licString.length();
        char[] hexChars = licString.toCharArray();
        short[] d = new short[length];
        for (int i = 0; i < length; i++) {
            d[i] = (short) hexChars[i];
        }
        return d;
    }
    public static boolean ispausevoice = false;
    private void ZigbeeController() {
        View view = LayoutInflater.from(ControlActivity.this).inflate(R.layout.zigbee, null);

        Builder ZigbeeBuilder = new AlertDialog.Builder(ControlActivity.this);
        ZigbeeBuilder.setTitle("Zigbee");
        ZigbeeBuilder.setView(view);
        final Button button1,button2,button3,button4,button5,button6,button7,button8,button9,button10,
                button11,button12,button13,button14,button15,button16,button17,button18,button19,button20,
                button21,button22,button23,button24,button25,button26,button27,button28,button29,button30,
                button31,button32,button33,button34,button35,button36,button37,button38,button39,button40,
                button41,button42,button43,button44,button45,button46,button47,button48;
        final EditText editText1,editText2,editText3,editText4,editText5,editText6,editText7,editText8,editText9;
        final Spinner spinner1,spinner2,spinner3,spinner4,spinner5,spinner6,spinner7,spinner8,spinner9,spinner10;
        final RadioButton radioButton1,radioButton2,radioButton3,radioButton4,radioButton5,radioButton6,radioButton7;
        final SeekBar seekBarpowerset;

        button1 = (Button)view.findViewById(R.id.leftled);
        button2 = (Button)view.findViewById(R.id.beep);
        button3 = (Button)view.findViewById(R.id.rightled);
        button4 = (Button)view.findViewById(R.id.findlinebut);
        button5 = (Button)view.findViewById(R.id.encode);
        button6 = (Button)view.findViewById(R.id.findlinereset);
        button7 = (Button)view.findViewById(R.id.setfindlinepower);
        button8 = (Button)view.findViewById(R.id.buttonsetfindnum);
        button9 = (Button)view.findViewById(R.id.buttonsendIR);
        button10 = (Button)view.findViewById(R.id.buttonLEDshow);
        button11 = (Button)view.findViewById(R.id.buttonstatimer);
        button12 = (Button)view.findViewById(R.id.buttonstoptimer);
        button13 = (Button)view.findViewById(R.id.buttontozerotimer);
        button14 = (Button)view.findViewById(R.id.showlenth);
        button15 = (Button)view.findViewById(R.id.light1);
        button16 = (Button)view.findViewById(R.id.light2);
        button17 = (Button)view.findViewById(R.id.light3);
        button18 = (Button)view.findViewById(R.id.photoup);
        button19 = (Button)view.findViewById(R.id.photodown);
        button20 = (Button)view.findViewById(R.id.agven);
        button21 = (Button)view.findViewById(R.id.voiceen);
        button22 = (Button)view.findViewById(R.id.dooropen);
        button23 = (Button)view.findViewById(R.id.doorclose);
        button24 = (Button)view.findViewById(R.id.floatlight);
        button25 = (Button)view.findViewById(R.id.tftphotoup);
        button26 = (Button)view.findViewById(R.id.tftphotoauto);
        button27 = (Button)view.findViewById(R.id.tftphotodown);
        button28 = (Button)view.findViewById(R.id.tftshowthephoto);
        button29 = (Button)view.findViewById(R.id.ltcp);
        button30 = (Button)view.findViewById(R.id.ltjl);
        button31 = (Button)view.findViewById(R.id.lttx);
        button32 = (Button)view.findViewById(R.id.ltys);
        button33 = (Button)view.findViewById(R.id.ltqt);
        button34 = (Button)view.findViewById(R.id.soundask);
        button35 = (Button)view.findViewById(R.id.soundpause);
        button36 = (Button)view.findViewById(R.id.soundstop);
        button37 = (Button)view.findViewById(R.id.soundsta);
        button38 = (Button)view.findViewById(R.id.soundthis);
        button39 = (Button)view.findViewById(R.id.soundrand);
        button40 = (Button)view.findViewById(R.id.zigcancel);
        button41 = (Button)view.findViewById(R.id.zigok);
        button42 = (Button)view.findViewById(R.id.tftsendcarnum);
        button43 = (Button)view.findViewById(R.id.tfttimeclear);
        button44 = (Button)view.findViewById(R.id.tfttimesta);
        button45 = (Button)view.findViewById(R.id.tfttimestop);
        button46 = (Button)view.findViewById(R.id.tftsdata);
        button47 = (Button)view.findViewById(R.id.tftsdist);
        button48 = (Button)view.findViewById(R.id.fastdatasend);

        editText1 = (EditText)view.findViewById(R.id.editTextsetfindnum);
        editText2 = (EditText)view.findViewById(R.id.editTextIR);
        editText3 = (EditText)view.findViewById(R.id.editLEDshowdata);
        editText4 = (EditText)view.findViewById(R.id.leddistance);
        editText5 = (EditText)view.findViewById(R.id.IRshow);
        editText6 = (EditText)view.findViewById(R.id.editvoicetext);
        editText7 = (EditText)view.findViewById(R.id.tftcarnumedit);
        editText8 = (EditText)view.findViewById(R.id.tftdataedit);
        editText9 = (EditText)view.findViewById(R.id.fastdataedit);

        spinner1 = (Spinner)view.findViewById(R.id.setfindnumspinner);
        spinner2 = (Spinner)view.findViewById(R.id.tftsetpic);
        spinner3 = (Spinner)view.findViewById(R.id.randshowfun);
        spinner4 = (Spinner)view.findViewById(R.id.setvoicenum);
        spinner5 = (Spinner)view.findViewById(R.id.typee);
        spinner6 = (Spinner)view.findViewById(R.id.mainsp);
        spinner7 = (Spinner)view.findViewById(R.id.firstsp);
        spinner8 = (Spinner)view.findViewById(R.id.secondsp);
        spinner9 = (Spinner)view.findViewById(R.id.thirdsp);

        radioButton1 = (RadioButton)view.findViewById(R.id.radioButtonLED1);
        radioButton2 = (RadioButton)view.findViewById(R.id.radioButtonLED2);
        radioButton3 = (RadioButton)view.findViewById(R.id.ltshow1);
        radioButton4 = (RadioButton)view.findViewById(R.id.ltshow2);
        radioButton5 = (RadioButton)view.findViewById(R.id.ltshow3);
        radioButton6 = (RadioButton)view.findViewById(R.id.ltshow4);
        radioButton7 = (RadioButton)view.findViewById(R.id.ltshow5);
        final ArrayAdapter<String> shapeadapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, itshowshape);
        final ArrayAdapter<String> coloradapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, itshowcolor);
        final ArrayAdapter<String> roadadapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, itshowroad);
        radioButton3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editText5.setText("R");
            }
        });
        radioButton4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editText5.setText("");
            }
        });
        radioButton5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spinner3.setAdapter(shapeadapter);
            }
        });
        radioButton6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spinner3.setAdapter(coloradapter);
            }
        });
        radioButton7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spinner3.setAdapter(roadadapter);
            }
        });

        seekBarpowerset = (SeekBar)view.findViewById(R.id.findlinepower) ;
//        seekBarpowerset.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//            @Override public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {}
//            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
//            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
//        });
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, findlinesel);
        spinner1.setAdapter(adapter);
        ArrayAdapter<String> adaptert = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, sendtestdata);
        spinner5.setAdapter(adaptert);
        spinner6.setAdapter(adaptert);
        spinner7.setAdapter(adaptert);
        spinner8.setAdapter(adaptert);
        spinner9.setAdapter(adaptert);
        ArrayAdapter<String> adaptertft = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, tftselpicdata);
        spinner2.setAdapter(adaptertft);
        ArrayAdapter<String> speak = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, speakvoice);
        spinner4.setAdapter(speak);
        if (leftledflag)button1.setText("左灯:开");else button1.setText("左灯:关");
        if (beepflag)button2.setText("喇叭:开");else button2.setText("喇叭:关");
        if (rightledflag)button3.setText("右灯:开");else button3.setText("右灯:关");
        button1.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {zigbee("leftled");
                if (leftledflag)button1.setText("左灯:开");else button1.setText("左灯:关");}});
        button2.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {zigbee("beep");
                if (beepflag)button2.setText("喇叭:开");else button2.setText("喇叭:关");}});
        button3.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {zigbee("rightled");
                if (rightledflag)button3.setText("右灯:开");else button3.setText("右灯:关");}});
        button4.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {zigbee("findline");}});
        button5.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {zigbee("clearencode");}});
        button6.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {zigbee("findlinereset");}});
        button15.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {zigbee("light1");}});
        button16.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {zigbee("light2");}});
        button17.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {zigbee("light3");}});
        button18.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {zigbee("photoup");}});
        button19.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {zigbee("photodown");}});
        button20.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {zigbee("agven");}});
        button21.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {zigbee("voiceen");}});
        button22.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {zigbee("dooropen");}});
        button23.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {zigbee("doorclose");}});
        button24.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {zigbee("floatlight");}});
        button37.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {voiceControl(editText6.getText().toString());}
        });
        button7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int val = seekBarpowerset.getProgress();
                byte[] temp = new byte[5];
                temp[0] = (byte)0xAA;
                temp[1] = (byte)0xA1;
                temp[2] = (byte)((val>>8)&0xFF);
                temp[3] = (byte)(val&0xFF);
                temp[4] = (byte)0x00;
                socket_connect.mystruct(temp);
            }
        });
        button8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int val1 = spinner1.getSelectedItemPosition();
                int val2 = (int) (Integer.parseInt(editText1.getText().toString()));
                byte[] temp = new byte[5];
                temp[0] = (byte)0xAA;
                temp[1] = (byte)0xA2;
                temp[2] = (byte)(val1);
                temp[3] = (byte)((val2>>8)&0xFF);
                temp[4] = (byte)(val2&0xFF);
                socket_connect.mystruct(temp);
            }
        });
        button9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                char[] zf = new char[14];
                zf = editText2.getText().toString().toCharArray();
//                byte[] temp = new byte[100];
//                temp = strtobyte(editText2.getText().toString());
                byte[] tempt = new byte[6];
                for (int i=0;i<6;i++)
                {
                    tempt[i] = (byte)((((get16fun(zf[2+2*i]))&0xF)<<4) | ((get16fun(zf[2+2*i+1]))&0xF));
                }
                socket_connect.infrared(tempt);
//                char[] temp = editText2.getText().toString().toCharArray();
//                if ((temp[0] == '0')&&(temp[1] == 'x')&&(temp[2] == '5')&&(temp[3] == '2')&&(temp[4] == 'n'))
//                {
//                    Toast.makeText(ControlActivity.this, "字符识别成功", Toast.LENGTH_SHORT).show();
//                }
            }
        });
        button10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                byte[] temp = new byte[5];
                temp[0] = (byte)0x04;
                if (radioButton1.isChecked())
                {
                    temp[1] = (byte)0x01;
                }else if (radioButton2.isChecked())
                {
                    temp[1] = (byte)0x02;
                }
                char[] zf = new char[8];
                zf = editText3.getText().toString().toCharArray();
                byte[] tempt = new byte[3];
                for (int i=0;i<3;i++)
                {
                    tempt[i] = (byte)((((get16fun(zf[2+2*i]))&0xF)<<4) | ((get16fun(zf[2+2*i+1]))&0xF));
                }
                temp[2] = tempt[0];
                temp[3] = tempt[1];
                temp[4] = tempt[2];
                socket_connect.mystruct(temp);
            }
        });
        button48.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                byte[] temp = new byte[5];
                char[] zf = new char[12];
                zf = editText9.getText().toString().toCharArray();
                byte[] tempt = new byte[5];
                for (int i=0;i<5;i++)
                {
                    tempt[i] = (byte)((((get16fun(zf[2+2*i]))&0xF)<<4) | ((get16fun(zf[2+2*i+1]))&0xF));
                    temp[i] = tempt[i];
                }
                socket_connect.mystruct(temp);
            }
        });
        button42.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                char[] zf = new char[12];
                zf = editText7.getText().toString().toCharArray();
                char[] temp = new char[6];
                for (int i=0;i<6;i++)
                {
                    temp[i] = zf[i+1];
                }
                socket_connect.TFTsendCarnum(temp);
            }
        });
        button29.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                char[] zf = new char[12];
                zf = editText5.getText().toString().toCharArray();
                byte[] temp = new byte[6];
                temp[0] = (byte) 0xff;temp[1] = (byte) 0x20;
                for (int i=0;i<4;i++)
                {
                    temp[i+2] = (byte) zf[i+1];
                }
                socket_connect.infrared(temp);
                temp[0] = (byte) 0xff;temp[1] = (byte) 0x10;
                for (int i=4;i<8;i++)
                {
                    temp[i-2] = (byte) zf[i+1];
                }
                socket_connect.infrared(temp);
            }
        });
        button30.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                byte[] temp = new byte[6];
                temp[0] = (byte)0xFF;
                temp[1] = (byte)0x11;
                int val = Integer.valueOf(editText5.getText().toString());
                temp[2] = (byte)((val/10) + '0');
                temp[3] = (byte)((val%10) + '0');
                temp[4] = 0x00;
                temp[5] = 0x00;
                socket_connect.infrared(temp);
            }
        });
        button31.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                byte[] temp = new byte[6];
                temp[0] = (byte)0xFF;
                temp[1] = (byte)0x12;
                temp[2] = (byte) (spinner3.getSelectedItemPosition()+1);
                temp[3] = 0x00;
                temp[4] = 0x00;
                temp[5] = 0x00;
                socket_connect.infrared(temp);
            }
        });
        button32.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                byte[] temp = new byte[6];
                temp[0] = (byte)0xFF;
                temp[1] = (byte)0x13;
                temp[2] = (byte) (spinner3.getSelectedItemPosition()+1);
                temp[3] = 0x00;
                temp[4] = 0x00;
                temp[5] = 0x00;
                socket_connect.infrared(temp);
            }
        });
        button33.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                byte[] temp = new byte[6];
                temp[0] = (byte)0xFF;
                temp[1] = (byte)0x14;
                temp[2] = (byte) (spinner3.getSelectedItemPosition()+1);
                temp[3] = 0x00;
                temp[4] = 0x00;
                temp[5] = 0x00;
                socket_connect.infrared(temp);
            }
        });
        button11.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                byte[] temp = new byte[5];
                temp[0] = (byte)0x04;
                temp[1] = (byte)0x03;
                temp[2] = (byte)0x01;
                temp[3] = (byte)0x00;
                temp[4] = (byte)0x00;
                socket_connect.mystruct(temp);
            }
        });
        button12.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                byte[] temp = new byte[5];
                temp[0] = (byte)0x04;
                temp[1] = (byte)0x03;
                temp[2] = (byte)0x00;
                temp[3] = (byte)0x00;
                temp[4] = (byte)0x00;
                socket_connect.mystruct(temp);
            }
        });
        button13.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                byte[] temp = new byte[5];
                temp[0] = (byte)0x04;
                temp[1] = (byte)0x03;
                temp[2] = (byte)0x02;
                temp[3] = (byte)0x00;
                temp[4] = (byte)0x00;
                socket_connect.mystruct(temp);
            }
        });
        button14.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int val = Integer.parseInt(editText4.getText().toString());
                socket_connect.digital_dic(val);
            }
        });
        button25.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                byte[] temp = new byte[5];
                temp[0] = (byte)0x0b;
                temp[1] = (byte)0x10;
                temp[2] = (byte)0x01;
                temp[3] = (byte)0x00;
                temp[4] = (byte)0x00;
                socket_connect.mystruct(temp);
            }
        });
        button26.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                byte[] temp = new byte[5];
                temp[0] = (byte)0x0b;
                temp[1] = (byte)0x10;
                temp[2] = (byte)0x03;
                temp[3] = (byte)0x00;
                temp[4] = (byte)0x00;
                socket_connect.mystruct(temp);
            }
        });
        button27.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                byte[] temp = new byte[5];
                temp[0] = (byte)0x0b;
                temp[1] = (byte)0x10;
                temp[2] = (byte)0x02;
                temp[3] = (byte)0x00;
                temp[4] = (byte)0x00;
                socket_connect.mystruct(temp);
            }
        });
        button28.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                byte[] temp = new byte[5];
                temp[0] = (byte)0x0B;
                temp[1] = (byte)0x10;
                temp[2] = (byte)0x00;
                temp[3] = (byte)(spinner2.getSelectedItemPosition()+1);
                temp[4] = (byte)0x00;
                socket_connect.mystruct(temp);
            }
        });
        button41.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                byte[] temp = new byte[5];
                temp[0] = (byte)((spinner5.getSelectedItemPosition())&0xFF);
                temp[1] = (byte)((spinner6.getSelectedItemPosition())&0xFF);
                temp[2] = (byte)((spinner7.getSelectedItemPosition())&0xFF);
                temp[3] = (byte)((spinner8.getSelectedItemPosition())&0xFF);
                temp[4] = (byte)((spinner9.getSelectedItemPosition())&0xFF);
                socket_connect.mystruct(temp);
            }
        });
        button43.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                byte[] temp = new byte[5];
                temp[0] = (byte)0x0B;
                temp[1] = (byte)0x30;
                temp[2] = (byte)0x02;
                temp[3] = (byte)0x00;
                temp[4] = (byte)0x00;
                socket_connect.mystruct(temp);
            }
        });
        button44.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                byte[] temp = new byte[5];
                temp[0] = (byte)0x0B;
                temp[1] = (byte)0x30;
                temp[2] = (byte)0x01;
                temp[3] = (byte)0x00;
                temp[4] = (byte)0x00;
                socket_connect.mystruct(temp);
            }
        });
        button45.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                byte[] temp = new byte[5];
                temp[0] = (byte)0x0B;
                temp[1] = (byte)0x30;
                temp[2] = (byte)0x00;
                temp[3] = (byte)0x00;
                temp[4] = (byte)0x00;
                socket_connect.mystruct(temp);
            }
        });
        button46.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                char[] zf = new char[8];
                zf = editText8.getText().toString().toCharArray();
                byte[] tempt = new byte[3];
                for (int i=0;i<3;i++)
                {
                    tempt[i] = (byte)((((get16fun(zf[2+2*i]))&0xF)<<4) | ((get16fun(zf[2+2*i+1]))&0xF));
                }
                byte[] temp = new byte[5];
                temp[0] = (byte)0x0B;
                temp[1] = (byte)0x40;
                temp[2] = (byte)tempt[0];
                temp[3] = (byte)tempt[1];
                temp[4] = (byte)tempt[2];
                socket_connect.mystruct(temp);
            }
        });
        button47.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                byte[] temp = new byte[5];
                temp[0] = (byte)0x0B;
                temp[1] = (byte)0x50;
                temp[2] = (byte)0x00;
                int val = Integer.valueOf(editText8.getText().toString());
                temp[3] = (byte) ((val/100)&0xF);;
                temp[4] = (byte)((((val%100)/10)<<4)|((val%10)&0xF));
                socket_connect.mystruct(temp);
            }
        });
        button38.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                byte[] temp = new byte[5];
                temp[0] = (byte)0x06;
                temp[1] = (byte)0x10;
                temp[2] = (byte)(spinner4.getSelectedItemPosition()+1);
                temp[3] = (byte)0x00;
                temp[4] = (byte)0x00;
                socket_connect.mystruct(temp);
            }
        });
        button39.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                byte[] temp = new byte[5];
                temp[0] = (byte)0x06;
                temp[1] = (byte)0x20;
                temp[2] = (byte)0x01;
                temp[3] = (byte)0x00;
                temp[4] = (byte)0x00;
                socket_connect.mystruct(temp);
            }
        });
        button34.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                byte[] sbyte = {
                        (byte)0xfd,
                        (byte)0x00,(byte)0x01,
                        (byte)0x21,
                };
                socket_connect.send_voice(sbyte);
            }
        });
        button35.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ispausevoice = !ispausevoice;
                if (ispausevoice)
                {
                    button35.setText("恢复合成");
                    byte[] sbyte = {
                            (byte)0xfd,
                            (byte)0x00,(byte)0x01,
                            (byte)0x03,//暂停 04 继续
                    };
                    socket_connect.send_voice(sbyte);
                }else
                {
                    button35.setText("暂停合成");
                    byte[] sbyte = {
                            (byte)0xfd,
                            (byte)0x00,(byte)0x01,
                            (byte)0x04,//继续
                    };
                    socket_connect.send_voice(sbyte);
                }
            }
        });
        button36.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                byte[] sbyte = {
                        (byte)0xfd,
                        (byte)0x00,(byte)0x01,
                        (byte)0x02,
                };
                socket_connect.send_voice(sbyte);
            }
        });
//        view.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                switch (v.getId())
//                {
//                    case R.id.leftled:
//                        zigbee("leftled");
//                        break;
//                    case R.id.beep:
//                        zigbee("beep");
//                        break;
//                    case R.id.rightled:
//                        zigbee("rightled");
//                        break;
//                    case R.id.findlinebut:
//                        zigbee("findline");
//                        break;
//                    case R.id.encode:
//                        zigbee("clearencode");
//                        break;
//                    case R.id.findlinereset:
//                        zigbee("findlinereset");
//                        break;
//                    case R.id.light1:
//                        zigbee("light1");
//                        break;
//                    case R.id.light2:
//                        zigbee("light2");
//                        break;
//                    case R.id.light3:
//                        zigbee("light3");
//                        break;
//                    case R.id.photoup:
//                        zigbee("photoup");
//                        break;
//                    case R.id.photodown:
//                        zigbee("photodown");
//                        break;
//                    case R.id.agven:
//                        zigbee("agven");
//                        break;
//                    case R.id.voiceen:
//                        zigbee("voiceen");
//                        break;
//                    case R.id.dooropen:
//                        zigbee("dooropen");
//                        break;
//                    case R.id.doorclose:
//                        zigbee("doorclose");
//                        break;
//                    case R.id.floatlight:
//                        zigbee("floatlight");
//                        break;
//                    case R.id.soundsta:
//                        voiceControl(editText6.toString());
//                        break;
//
//                    default:
//                        break;
//                }
//            }
//        });
        ZigbeeBuilder.setPositiveButton("退出",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                       dialog.cancel();
                    }
                });
        ZigbeeBuilder.create().show();
    }

    public byte[] strtobyte(String str)
    {
        byte[] temp = new byte[100];
        char[] val  = str.toCharArray();
        for (int i = 0;i<val.length/2;i+=1)
        {
            temp[i] = (byte)((((get16fun(val[2+2*i]))&0xF)<<4) | ((get16fun(val[2+2*i+1]))&0xF));
        }
        return temp;
    }

    public int get16fun(char temp)
    {
        int valt = 0;
        switch (temp)
        {
            case '0':valt = 0;break;
            case '1':valt = 1;break;
            case '2':valt = 2;break;
            case '3':valt = 3;break;
            case '4':valt = 4;break;
            case '5':valt = 5;break;
            case '6':valt = 6;break;
            case '7':valt = 7;break;
            case '8':valt = 8;break;
            case '9':valt = 9;break;
            case 'A':valt = 10;break;
            case 'a':valt = 10;break;
            case 'B':valt = 11;break;
            case 'b':valt = 11;break;
            case 'C':valt = 12;break;
            case 'c':valt = 12;break;
            case 'D':valt = 13;break;
            case 'd':valt = 13;break;
            case 'E':valt = 14;break;
            case 'e':valt = 14;break;
            case 'F':valt = 15;break;
            case 'f':valt = 15;break;
            default:break;
        }
        return valt;
    }

    private boolean flag_voice;
    private void voiceControl(String text) {
        String src = text;
        if (src.equals("")) {
            src = "内容为空";
        }
        try {
            flag_voice=true;
            byte[] sbyte = bytesend(src.getBytes("GBK"));
            socket_connect.send_voice(sbyte);
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private byte[] bytesend(byte[] sbyte) {
        byte[] textbyte = new byte[sbyte.length + 5];
        textbyte[0] = (byte) 0xFD;
        textbyte[1] = (byte) (((sbyte.length + 2) >> 8) & 0xff);
        textbyte[2] = (byte) ((sbyte.length + 2) & 0xff);
        textbyte[3] = 0x01;// 合成语音命令
        textbyte[4] = (byte) 0x01;// 编码格式
        for (int i = 0; i < sbyte.length; i++) {
            textbyte[i + 5] = sbyte[i];
        }
        return textbyte;
    }
    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
        socket_connect.onDestory();
    }

    Timer tim=new Timer();
    TimerTask timerTask=new TimerTask() {
        @Override
        public void run() {
            isexti=false;
            hastask=true;
        }
    };
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (keyCode == KeyEvent.KEYCODE_BACK)
        {
            if (isexti == false)
            {
                isexti=true;
                Toast.makeText(this, "不再看看了吗？亲，再按一次退出哦", Toast.LENGTH_SHORT).show();
                if (!hastask)
                {
                    tim.schedule(timerTask,2000);
                }
            }else {
                this.finish();
                System.exit(0);
            }
        }
        return false;
    }

    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater menuInflater=new MenuInflater(this);
        menuInflater.inflate(R.menu.layout,menu);
        return super.onCreateOptionsMenu(menu);
    }
    public boolean onOptionsItemSelected(MenuItem menuItem)
    {
        switch (menuItem.getItemId())
        {
            case R.id.system_seeting:
                Toast.makeText(this, "暂无设置项", Toast.LENGTH_SHORT).show();
                break;
            case R.id.encoura_for_author:
                Intent payinfo=new Intent(Intent.ACTION_VIEW, Uri.parse("https://ds.alipay.com/?from=mobilecodec&scheme=alipays%3A%2F%2Fplatformapi%2Fstartapp%3FsaId%3D10000007%26clientVersion%3D3.7.0.0718%26qrcode%3Dhttps%253A%252F%252Fqr.alipay.com%252FFKX07321H71FT6ZYMLEU62%253F_s%253Dweb-other"));
                startActivity(payinfo);
                break;
            case R.id.About_app:
                Toast.makeText(ControlActivity.this, "百科融创出品", Toast.LENGTH_SHORT).show();
                //startActivity(new Intent(this,webview.class));
                break;
            case R.id.share_APP:
                Intent intent=new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_SUBJECT,"分享APP");
                intent.putExtra(Intent.EXTRA_TEXT,"我发现了一个有趣的应用，你也来下载看看吧，点这里进来下载"+
                        "https://jq.qq.com/?_wv=1027&k=4BR7nWA");
                startActivity(intent.createChooser(intent,getTitle()));
                break;
            case R.id.version:
                Toast.makeText(ControlActivity.this, "当前版本:3.2", Toast.LENGTH_SHORT).show();
                break;
            case R.id.intoteam:
                Intent intoteam = new Intent(Intent.ACTION_VIEW, Uri.parse("https://jq.qq.com/?_wv=1027&k=4BR7nWA"));
                startActivity(intoteam);
                break;
            default:
                break;
        }
        return true;
    }

    public boolean leftledflag = false;
    public boolean rightledflag = false;
    public boolean beepflag = false;
    public boolean agvenflag = false;
    public boolean voiceflag = false;
    public void zigbee(String cmd) {
        switch (cmd)
        {
            case "leftled":
                leftledflag=!leftledflag;
                if (leftledflag)
                {
                    if (rightledflag)
                    {
                        socket_connect.light(1,1);
                    }else
                    {
                        socket_connect.light(1,0);
                    }
                }else
                {
                    if (rightledflag)
                    {
                        socket_connect.light(0,1);
                    }else
                    {
                        socket_connect.light(0,0);
                    }
                }
                break;
            case "rightled":
                rightledflag=!rightledflag;
                if (rightledflag)
                {
                    if (leftledflag)
                    {
                        socket_connect.light(1,1);
                    }else
                    {
                        socket_connect.light(0,1);
                    }
                }else
                {
                    if (leftledflag)
                    {
                        socket_connect.light(1,0);
                    }else
                    {
                        socket_connect.light(0,0);
                    }
                }
                break;
            case "beep":
                beepflag=!beepflag;
                if (beepflag)
                {
                    socket_connect.buzzer(1);
                }else
                {
                    socket_connect.buzzer(0);
                }
                break;
            case "clearencode":
                socket_connect.clearencode();
                break;
            case "findlinereset":
                socket_connect.findlinereset();
                break;
            case "findline":
                socket_connect.line(50);
                break;
            case "light1":
                socket_connect.gear(1);
                break;
            case "light2":
                socket_connect.gear(2);
                break;
            case "light3":
                socket_connect.gear(3);
                break;
            case "photoup":
                socket_connect.picture(0);
                break;
            case "photodown":
                socket_connect.picture(1);
                break;
            case "agven":
                agvenflag=!agvenflag;
                if (agvenflag)
                {
                    socket_connect.agven(1);
                }else
                {
                    socket_connect.agven(0);
                }
                break;
            case "voiceen":
                voiceflag=!voiceflag;
                if (voiceflag)
                {
                    socket_connect.voiceen(1);
                }else
                {
                    socket_connect.voiceen(0);
                }
                break;
            case "dooropen":
                socket_connect.gate(1);
                break;
            case "doorclose":
                socket_connect.gate(2);
                break;
            case "floatlight":
                socket_connect.floatlight();
                break;

            default:
                break;
        }
    }
}

/*
*
  if(msg.what == 12)
            {
                if (gravity)
                {
                    sx = (int) (sensorx*16);
                    sy = (int) (sensory*16);
                    if (sx>=100)sx=100;
                    if (sy>=100)sy=100;
                    if (sx<=-100)sx=-100;
                    if (sy<=-100)sy=-100;
                    wheell = -(sx+sy);
                    wheelr = sx-sy;
                    //datatext.setText("left:"+wheell+"  right:"+wheelr+"x:"+sx+"y:"+sy);
                    socket_connect.motorrun(wheell,wheelr);
//                    if((wheell>=0)&&(wheelr>=0))
//                    {
//                        socket_connect.FF(wheell,wheelr);
//                    }else if ((wheell<0)&&(wheelr<0))
//                    {
//                        socket_connect.BB(-wheell,-wheelr);
//                    }else if ((wheell>=0)&&(wheelr<0))
//                    {
//                        socket_connect.FB(wheell,-wheelr);
//                    }else if ((wheell<0)&&(wheelr>=0))
//                    {
//                        socket_connect.BF(-wheell,wheelr);
//                    }
                }else{
                    tx = (int)touchx;
                    ty = (int)touchy;
                    if (tx>=100)tx=100;
                    if (ty>=100)ty=100;
                    if (tx<=-100)tx=-100;
                    if (ty<=-100)ty=-100;
                    wheell = -(-(tx)+ty);
                    wheelr = -(tx)-ty;
                    //datatext.setText("left:"+wheell+"  right:"+wheelr);
                    socket_connect.motorrun(wheell,wheelr);
//                    if((wheell>=0)&&(wheelr>=0))
//                    {
//                        socket_connect.FF(wheell,wheelr);
//                    }else if ((wheell<0)&&(wheelr<0))
//                    {
//                        socket_connect.BB(-wheell,-wheelr);
//                    }else if ((wheell>=0)&&(wheelr<0))
//                    {
//                        socket_connect.FB(wheell,-wheelr);
//                    }else if ((wheell<0)&&(wheelr>=0))
//                    {
//                        socket_connect.BF(-wheell,wheelr);
//                    }
                }
            }
*
* */