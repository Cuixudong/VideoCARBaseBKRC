package xdtech.rc_car;

/**
 * Created by Administrator on 2017/9/25.
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
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Toast;

public class RockerActivity extends Activity implements SensorEventListener {

    private static final String TAG = "MainActivity";
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
    private byte[] mByte = new byte[11];
    private socket_connect socket_connect;
    private float touchx,touchy;
    void doLog(String log) {
        Log.e(TAG, log);
    }

    private Button isgra,getphoto;
    private RockerView rockerView1;
    private RockerView rockerView2;
    private TextView dataview;
    int screenWidth;
    int screenHeight;
    private Sensor sensor;
    private SensorManager sensorManager;
    private String xy1,xy2;
    private ImageView imageView;
    private Bitmap bitmap;
    private static boolean gravity = false;
    private static boolean isexti=false;
    private static boolean hastask=false;
    private static float sensorx,sensory;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_FULLSCREEN | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);// 设置全屏,屏幕长亮
        setContentView(R.layout.rocker);

        DisplayMetrics dm = getResources().getDisplayMetrics();
        screenWidth = dm.widthPixels;
        screenHeight = dm.heightPixels;

        rockerView1 = (RockerView) findViewById(R.id.rockerView1);
        rockerView2 = (RockerView) findViewById(R.id.rockerView2);
        dataview = (TextView)findViewById(R.id.control_data);
        imageView = (ImageView)findViewById(R.id.imageView) ;
        isgra = (Button)findViewById(R.id.isgra);
        getphoto = (Button)findViewById(R.id.getphoto);
        getphoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                bitmap = cameraCommandUtil.httpForImage(IPCamera);
                if (bitmap != null)
                {
                    saveMyBitmap("abc",bitmap);
                }
            }
        });
        isgra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gravity = !gravity;
                if (gravity)
                {
                    isgra.setText("重力模式");
                }else
                {
                    isgra.setText("摇杆模式");
                }
            }
        });
        rockerView1.setRockerChangeListener(new RockerView.RockerChangeListener() {

            @Override
            public void report(float x, float y) {
                // TODO Auto-generated method stub
                // doLog(x + "/" + y);
//                xy1 = "左:"+(int)x+"  "+(int)y;
                //setLayout(rockerView2, (int)x, (int)y);
//                dataview.setText(xy1+"\n"+xy2);
                if (x>30)
                {
                    state_camera = 4;
                }
                if (x<(-30))
                {
                    state_camera = 3;
                }
                if (y>=30)
                {
                    state_camera = 2;
                }
                if (y<(-30))
                {
                    state_camera = 1;
                }
            }
        });

        rockerView2.setRockerChangeListener(new RockerView.RockerChangeListener() {

            @Override
            public void report(float x, float y) {
                // TODO Auto-generated method stub
                // doLog(x + "/" + y);
//                xy2 = "右:"+(int)x+"  "+(int)y;
//                dataview.setText(xy1+"\n"+xy2);
                touchx = x;
                touchy = y;
                //setLayout(rockerView1, (int)x, (int)y);
            }
        });
        sensorManager = (SensorManager) this.getSystemService(Service.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        // 得到服务器的IP地址
        wifiManager = (WifiManager)  getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        dhcpInfo = wifiManager.getDhcpInfo();
        IPCar = Formatter.formatIpAddress(dhcpInfo.gateway);

        //广播接收器注册
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(A_S);
        registerReceiver(myBroadcastReceiver, intentFilter);
        // 搜索摄像头图片工具
        cameraCommandUtil = new CameraCommandUtil();

        Intent intent = new Intent();
        intent.setClass(RockerActivity.this, SearchService.class);
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
    private int timer_flag =0;
    private void timer_start()
    {
        final Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                Message msg =new Message();
                msg.what = 12;
                msg.obj = timer_flag;
                handler.sendMessage(msg);
                timer_flag++;
                if(timer_flag == 100)
                {
                    timer_flag = 0;
                }
            }
        }, 30, 20);
    }
    int tx,ty;int wheell,wheelr;int sx,sy;
    private Handler handler =new Handler()
    {
        public void handleMessage(Message msg)
        {
            if(msg.what == 12)
            {
                if (gravity)
                {
                    sy = (int) (sensorx*16);
                    sx = (int) ((-sensory)*16);
                    if (sx>=100)sx=100;
                    if (sy>=100)sy=100;
                    if (sx<=-100)sx=-100;
                    if (sy<=-100)sy=-100;
                    wheell = -(sx+sy);
                    wheelr = sx-sy;
                    socket_connect.motorrun(wheell,wheelr);
                    //datatext.setText("left:"+wheell+"  right:"+wheelr+"x:"+sx+"y:"+sy);
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
        }
    };
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

    // 得到当前摄像头的图片信息
    public void getBitmap() {
        bitmap = cameraCommandUtil.httpForImage(IPCamera);
        phHandler.sendEmptyMessage(10);
    }
    // 显示图片
    public Handler phHandler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == 10) {
                imageView.setImageBitmap(bitmap);
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
                Toast.makeText(RockerActivity.this, "百科融创出品", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(RockerActivity.this, "当前版本:3.2", Toast.LENGTH_SHORT).show();
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
                        dataview.setText("WIFIIP:"+IPCar+"\n"+"CameraIP:"+Camera_show_ip+"\n"+"主车各状态信息:\n" + "超声波:" + UltraSonic
                                + "\n"+"mm 光照:" + Light + "lx\n" + " 码盘:" + CodedDisk
                                +"\n"+ "光敏状态:" + psStatus +"\n"+ "状态:" + (mByte[2]));
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
    public void saveMyBitmap(String bitName,Bitmap mBitmap){

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
    public String getMyTime() {
        SimpleDateFormat format =new SimpleDateFormat("yyyy年MM月dd日 hh时：mm分：ss秒");
        String date =format.format(new Date());
        return date;
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
    public void setLayout(View v, int dx, int dy) {
        int left = v.getLeft() + dx;
        int top = v.getTop() + dy;
        int right = v.getRight() + dx;
        int bottom = v.getBottom() + dy;
        if (left < 0) {
            left = 0;
            right = left + v.getWidth();
        }
        if (right > screenWidth) {
            right = screenWidth;
            left = right - v.getWidth();
        }
        if (top < 0) {
            top = 0;
            bottom = top + v.getHeight();
        }
        if (bottom > screenHeight) {
            bottom = screenHeight;
            top = bottom - v.getHeight();
        }
        v.layout(left, top, right, bottom);
    }
}