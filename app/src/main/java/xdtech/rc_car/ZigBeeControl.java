package xdtech.rc_car;

/**
 * Created by Administrator on 2017/9/27.
 */
import java.io.UnsupportedEncodingException;
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
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.format.Formatter;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
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

public class ZigBeeControl extends Activity{
    private Spinner spinner1,spinner2,spinner3;
    private Button leftled,rightled,beep,encode,findline,light1,light2,light3,photoup,photodown,agven,voice,dooropen,doorclose,floatlight;
    String[] headstr={
            "主车",
            "道闸",
            "LED显示",
            "立体显示",
            "磁悬浮",
            "TFT显示",
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.zigbee);

        leftled = (Button)findViewById(R.id.leftled);
        rightled = (Button)findViewById(R.id.rightled);
        beep = (Button)findViewById(R.id.beep);
        encode = (Button)findViewById(R.id.encode);
        findline = (Button)findViewById(R.id.findlinebut);
        light1 = (Button)findViewById(R.id.light1);
        light2 = (Button)findViewById(R.id.light2);
        light3 = (Button)findViewById(R.id.light3);
        photoup = (Button)findViewById(R.id.photoup);
        photodown = (Button)findViewById(R.id.photodown);
        agven = (Button)findViewById(R.id.agven);
        voice = (Button)findViewById(R.id.voiceen);
        dooropen = (Button)findViewById(R.id.dooropen);
        doorclose = (Button)findViewById(R.id.doorclose);
        floatlight = (Button)findViewById(R.id.floatlight);

        ArrayAdapter<String> AD1=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_multiple_choice,headstr);
        spinner1.setAdapter(AD1);
    }


    public void BClick (View v)
    {
        switch(v.getId())
        {
            case R.id.leftled:
                break;
            case R.id.rightled:
                break;
            default:
                break;
        }
    }
}
