package xdtech.rc_car;

/**
 * Created by Administrator on 2017/9/25.
 */
import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;

public class InitActivity extends Activity{

    private TextView init_text_number;
    private TextView por,lan;
    AutoCompleteTextView auto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_init);
        por = (TextView) findViewById(R.id.porp);
        lan = (TextView) findViewById(R.id.landp);
        por.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(InitActivity.this,ControlActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                InitActivity.this.finish();
            }
        });
        lan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(InitActivity.this,RockerActivity.class);
                startActivity(intent);
                InitActivity.this.finish();
            }
        });
        first_test();
        //timer_start();
        //init_text_number = (TextView)findViewById(R.id.init_text_number);
    }
    public void first_test()
    {
        File F = new File("/storage/emulated/0/百科融创科技");
        if (!F.exists())
        {
            F.mkdirs();
            LayoutInflater inflater = null;
            AlertDialog.Builder bulide = new AlertDialog.Builder(InitActivity.this);
            bulide.setTitle("欢迎欢迎，热烈欢迎");
            if(inflater==null){
                inflater = LayoutInflater.from(InitActivity.this);
            }
            View view = (View) inflater.inflate(R.layout.firstwellcome, null);
            auto = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView);
            bulide.setView(view);
            bulide.setPositiveButton("哦哦", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ;
                }
            });
            bulide.setNegativeButton("额...", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });
            bulide.create();
            bulide.show();
        }
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
                msg.what = 1;
                msg.obj = timer_flag;
                handler.sendMessage(msg);
                timer_flag++;
                if(timer_flag == 2)
                {
                    timer_flag = 0;
                    timer.cancel();
                    Intent intent = new Intent(InitActivity.this,ControlActivity.class);
                    startActivity(intent);
                    InitActivity.this.finish();
                }
            }
        }, 3, 1000);
    }

    private Handler handler =new Handler()
    {
        public void handleMessage(Message msg)
        {
            if(msg.what == 1)
            {
                //init_text_number.setText(""+msg.obj);
            }
        }
    };

}
/*

   private void  position_Dialog()
   {
	   final Builder builder = new Builder(MainActivity.this);
	   builder.setTitle("预设位设置");
	   String[] set_item = { "set1", "set2" ,"set3","call1","call2","call3"};
	   builder.setSingleChoiceItems(set_item, -1, new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			// TODO 自动生成的方法存根
			state_camera = which+5;
			dialog.cancel();
		}
	});
   builder.create().show();
   }

	private Handler licHandler=new Handler(){
		public void handleMessage(Message msg) {
			short [] li=StringToBytes(lic_item[msg.what]);
			data[0] = 0x20;
			data[1] = (short) (li[0]);
			data[2]=(short) (li[1]);
			data[3]=(short) (li[2]);
			data[4]=(short) (li[3]);
			socket_connect.infrared_stereo(data);
			data[0] = 0x10;
			data[1] = (short) (li[4]);
			data[2]=(short) (li[5]);
			data[3]=(short) (li[6]);
			data[4]=(short) (li[7]);
			socket_connect.infrared_stereo(data);
		};
	};

* */