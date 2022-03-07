package xdtech.rc_car;

import android.app.Activity;
import android.content.Intent;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class MainActivity extends Activity {
    public static final String A_S = "com.a_s";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
    }
}

/*
public class MainActivity extends Activity {

	private TextView title_text,Data_show,voiceText;
	private ImageView image_show;
	private EditText speed_data, coded_disc_data;
	private ImageButton up_button,left_button,right_button,below_button,stop_button;
	private Button chief_status_button,chief_control_button;

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
	// 接受传感器
	long psStatus = 0;// 状态
	long UltraSonic = 0;// 超声波
	long Light = 0;// 光照
	long CodedDisk = 0;// 码盘值
	String Camera_show_ip = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        control_Init();								//控件初始化
        wifi_Init();								//WiFi初始化
        Camer_Init();								//摄像头初始化
        search();									//开启Service 搜索摄像头IP
        socket_connect = new socket_connect();		//实例化socket连接类
        connect_thread();							//开启网络连接线程
    }

    private void wifi_Init()
    {
		// 得到服务器的IP地址
		wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		dhcpInfo = wifiManager.getDhcpInfo();
		IPCar = Formatter.formatIpAddress(dhcpInfo.gateway);
    }

	// 搜索摄像cameraIP进度条
	private void search() {
		Intent intent = new Intent();
		intent.setClass(MainActivity.this, SearchService.class);
		startService(intent);
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

    private void Camer_Init()
    {
    	//广播接收器注册
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(A_S);
		registerReceiver(myBroadcastReceiver, intentFilter);
		// 搜索摄像头图片工具
		cameraCommandUtil = new CameraCommandUtil();
    }

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
	// 速度与码盘值
	private int sp_n, en_n;
	//前进按键功能标志位
	private boolean flag_multiplexing_up_button = true;
    private class ontouchlistener2  implements OnTouchListener
    {
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			// TODO Auto-generated method stub
			if(event.getAction() == MotionEvent.ACTION_DOWN)		//按键按下时
			{
				sp_n = getSpeed();
				en_n = getEncoder();
				switch(v.getId())
				{
				case R.id.up_button:
					flag_multiplexing_up_button = true;
					up_button.setImageResource(R.drawable.up_button_g);
					System.out.println("触发2");
					break;
				case R.id.left_button:
					left_button.setImageResource(R.drawable.left_button_g);
					break;
				case R.id.right_button:
					right_button.setImageResource(R.drawable.right_button_g);
					break;
				case R.id.below_button:
					below_button.setImageResource(R.drawable.below_button_g);
					break;
				case R.id.stop_button:
					stop_button.setImageResource(R.drawable.stop_button_g);
					break;
			   }
		  }
			if(v.getId() == R.id.up_button)
			{
			  if(event.getEventTime() - event.getDownTime() >= 900)
			  {
				  flag_multiplexing_up_button = false;
				  up_button.setImageResource(R.drawable.up_button_r);
			  }
			}
			if (event.getAction() == MotionEvent.ACTION_UP) {	 //按键离开时
			switch(v.getId())
			{
				case R.id.up_button:
					if(flag_multiplexing_up_button == false)
					{
						socket_connect.line(sp_n);
					} else if(flag_multiplexing_up_button == true) {
						socket_connect.go(sp_n, en_n);
					}
					up_button.setImageResource(R.drawable.up_button);
					break;
				case R.id.left_button:
					left_button.setImageResource(R.drawable.left_button);
					socket_connect.left(sp_n);
					break;
				case R.id.right_button:
					right_button.setImageResource(R.drawable.right_button);
					socket_connect.right(sp_n);
					break;
				case R.id.below_button:
					below_button.setImageResource(R.drawable.below_button);
					socket_connect.back(sp_n, en_n);
					break;
				case R.id.stop_button:
					stop_button.setImageResource(R.drawable.stop_button);
					socket_connect.stop();
					break;
			}
			}
	    return true;
    }
  }

    private boolean chief_status_flag = true;
    private boolean chief_control_flag = true;
    public void myonClick(View v)
    {
    	switch(v.getId())
    	{
    	case R.id.position_button:		//Preset bit
    		position_Dialog();
    		break;
    	case R.id.qr_button:		    //QR code
    		qrHandler.sendEmptyMessage(10);
    		System.out.println("二维码已经触发");
    		break;
    	case R.id.infrare_button:		//infrared
    		infrare_Dialog();
    		break;
    	case R.id.zigbee_button:		//zigbee
    		zigbee_Dialog();
    		break;
    	case R.id.buzzer_button:		//buzzer
    		buzzerController();
    		break;
    	case R.id.pilot_lamp_button:	//pilot lamp
    		lightController();
    		break;
    	case R.id.chief_status_button:   //主车转态
    		chief_status_flag = !chief_status_flag;
    		if(chief_status_flag == false)
    		{
    			chief_status_button.setText("从车状态");
    			socket_connect.vice(1);
    		}
    		if(chief_status_flag == true)
    		{
    			chief_status_button.setText("主车状态");
    			socket_connect.vice(2);
    		}
    		break;
    	case R.id.chief_control_button:  //主车控制
    		chief_control_flag = !chief_control_flag;
    		if(chief_control_flag == false)
    		{
    			chief_control_button.setText("从车控制");
    			socket_connect.TYPE = 0x02;
    		}
    		if(chief_control_flag == true)
    		{
    			chief_control_button.setText("主车控制");
    			socket_connect.TYPE = 0xAA;
    		}
    		break;
    	}
    }


	// 指示灯遥控器
	private void lightController() {
		AlertDialog.Builder lt_builder = new AlertDialog.Builder(
				MainActivity.this);
		lt_builder.setTitle("指示灯");
		String[] item = { "左亮", "全亮", "右亮", "全灭" };
		lt_builder.setSingleChoiceItems(item, -1,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						if (which == 0) {
							socket_connect.light(1, 0);
						} else if (which == 1) {
							socket_connect.light(1, 1);
						} else if (which == 2) {
							socket_connect.light(0, 1);
						} else if (which == 3) {
							socket_connect.light(0, 0);
						}
						dialog.dismiss();
					}
				});
		lt_builder.create().show();
	}


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

	// 蜂鸣器
	private void buzzerController() {
		AlertDialog.Builder build = new AlertDialog.Builder(MainActivity.this);
		build.setTitle("蜂鸣器");
		String[] im = { "开", "关" };
		build.setSingleChoiceItems(im, -1,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						if (which == 0) {
							// 打开蜂鸣器
							socket_connect.buzzer(1);
						} else if (which == 1) {
							// 关闭蜂鸣器
							socket_connect.buzzer(0);
						}
						dialog.dismiss();
					}
				});
		build.create().show();
	}


   private void infrare_Dialog()
   {
	   Builder builder = new Builder(MainActivity.this);
	   builder.setTitle("红外");
	   String[] infrare_item = { "报警器", "档位器", "风扇", "立体显示" };
	   builder.setSingleChoiceItems(infrare_item, -1, new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			// TODO 自动生成的方法存根
			switch(which)
			{
			case 0:
				policeController();
				break;
			case 1:
				gearController();
				break;
			case 2:
				//0x00,0xFF,0x45,~(0x45)
				socket_connect.infrared((byte) 0x03, (byte) 0x05,
						(byte) 0x14, (byte) 0x45, (byte) 0xDE,
						(byte) 0x92);
				//socket_connect.fan();
				break;
			case 3:
				threeDisplay();
				break;
			}
			dialog.cancel();
		}
	});
	   builder.create().show();
   }

	// 报警器
	private void policeController() {
		Builder builder = new Builder(MainActivity.this);
		builder.setTitle("报警器");
		String[] item2 = { "开", "关" };
		builder.setSingleChoiceItems(item2,-1,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						if (which == 0) {
							socket_connect.infrared((byte) 0x03, (byte) 0x05,
									(byte) 0x14, (byte) 0x45, (byte) 0xDE,
									(byte) 0x92);
						} else if (which == 1) {
							socket_connect.infrared((byte) 0x67, (byte) 0x34,
									(byte) 0x78, (byte) 0xA2, (byte) 0xFD,
									(byte) 0x27);
						}
						dialog.dismiss();
					}
				});
		builder.create().show();
	}

	private void gearController() {
		Builder builder = new AlertDialog.Builder(MainActivity.this);
		builder.setTitle("档位遥控器");
		String[] gr_item = { "光强加1档", "光强加2档", "光强加3档" };
		builder.setSingleChoiceItems(gr_item, -1,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						if (which == 0) {// 加一档
							socket_connect.gear(1);
						} else if (which == 1) {// 加二档
							socket_connect.gear(2);
						} else if (which == 2) {// 加三档
							socket_connect.gear(3);
						}
						dialog.dismiss();
					}
				});
		builder.create().show();
	}

	private short[] data = { 0x00, 0x00, 0x00, 0x00, 0x00 };

	private void threeDisplay() {
		Builder Builder = new Builder(MainActivity.this);
		Builder.setTitle("立体显示");
		String[] three_item = { "颜色信息", "图形信息", "距离信息", "车牌信息", "路况信息", "默认信息" };
		Builder.setSingleChoiceItems(three_item, -1,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						switch (which) {
						case 0:
							color();
							break;
						case 1:
							shape();
							break;
						case 2:
							dis();
							break;
						case 3:
							lic();
							break;
						case 4:
							road();
							break;
						case 5:
							data[0] = 0x15;
							data[1] = 0x01;
							socket_connect.infrared_stereo(data);
							break;
						default:
							break;
						}
						dialog.cancel();
					}
				});
		Builder.create().show();
	}

	private void color() {
		Builder colorBuilder = new AlertDialog.Builder(this);
		colorBuilder.setTitle("颜色信息");
		String[] lg_item = { "红色", "绿色", "蓝色", "黄色", "紫色", "青色", "黑色", "白色" };
		colorBuilder.setSingleChoiceItems(lg_item, -1,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						data[0] = 0x13;
						data[1] = (short) (which + 0x01);
						socket_connect.infrared_stereo(data);
						dialog.cancel();
					}
				});
		colorBuilder.create().show();
	}

	private void shape() {
		Builder shapeBuilder = new AlertDialog.Builder(this);
		shapeBuilder.setTitle("图形信息");
		String[] shape_item = { "矩形", "圆形", "三角形", "菱形", "梯形", "饼图", "靶图",
				"条形图" };
		shapeBuilder.setSingleChoiceItems(shape_item, -1,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						data[0] = 0x12;
						data[1] = (short) (which + 0x01);
						socket_connect.infrared_stereo(data);
						dialog.cancel();
					}
				});
		shapeBuilder.create().show();
	}

	private void road() {
		Builder roadBuilder = new AlertDialog.Builder(this);
		roadBuilder.setTitle("路况信息");
		String[] road_item = { "隧道有事故，请绕行", "前方施工，请绕行" };
		roadBuilder.setSingleChoiceItems(road_item, -1,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						data[0] = 0x14;
						data[1] = (short) (which + 0x01);
						socket_connect.infrared_stereo(data);
						dialog.cancel();
					}
				});
		roadBuilder.create().show();
	}


	private void dis() {
		Builder disBuilder = new AlertDialog.Builder(this);
		disBuilder.setTitle("距离信息");
		final String[] road_item = { "10cm", "15cm", "20cm", "28cm", "39cm" };
		disBuilder.setSingleChoiceItems(road_item, -1,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						int disNum = Integer.parseInt(road_item[which]
								.substring(0, 2));
						data[0] = 0x11;
						data[1] = (short) (disNum / 10 + 0x30);
						data[2] = (short) (disNum % 10 + 0x30);
						socket_connect.infrared_stereo(data);
						dialog.cancel();
					}
				});
		disBuilder.create().show();
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
	private int lic = -1;
	private String[] lic_item = { "N300Y7A4", "N600H5B4", "N400Y6G6",
			"J888B8C8" };

	private void lic() {
		Builder licBuilder = new AlertDialog.Builder(this);
		licBuilder.setTitle("车牌信息");
		licBuilder.setSingleChoiceItems(lic_item, lic,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						lic = which;
						licHandler.sendEmptyMessage(which);
						dialog.cancel();
					}
				});
		licBuilder.create().show();
	}

	private void zigbee_Dialog()
	{
		Builder builder = new AlertDialog.Builder(MainActivity.this);
		builder.setTitle("zigbee");
		String[] zg_item = { "闸门", "数码管","语音播报", "磁悬浮","TFT显示器"};
		builder.setSingleChoiceItems(zg_item, -1,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						switch(which)
						{
						case 0:
							gateController();		// 闸门
							break;
						case 1:
							digital();				// 数码管
							break;
						case 2:
							voiceController();	//语音播报
							break;
						case 3:
							magnetic_suspension();  //磁悬浮
							break;
						case 4:
							TFT_LCD();				//TFT液晶显示
							break;
						}
						dialog.dismiss();
					}
				});
		builder.create().show();
	}

	private boolean flag_voice;
	private void voiceController() {
		View view = LayoutInflater.from(MainActivity.this).inflate(
				R.layout.item_car, null);
		voiceText = (EditText) view.findViewById(R.id.voiceText);

		Builder voiceBuilder = new AlertDialog.Builder(MainActivity.this);
		voiceBuilder.setTitle("语音播报");
		voiceBuilder.setView(view);
		voiceBuilder.setPositiveButton("播报",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						String src = voiceText.getText().toString();
						if (src.equals("")) {
							src = "请输入你要播报的内容";
						}
						try {
							flag_voice=true;
							byte[] sbyte = bytesend(src.getBytes("GBK"));
							socket_connect.send_voice(sbyte);
						} catch (UnsupportedEncodingException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						dialog.cancel();
					}
				});
		voiceBuilder.setNegativeButton("取消", null);
		voiceBuilder.create().show();
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

	private void gateController() {
		Builder gt_builder = new AlertDialog.Builder(MainActivity.this);
		gt_builder.setTitle("闸门控制");
		String[] gt = { "开", "关" };
		gt_builder.setSingleChoiceItems(gt, -1,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						if (which == 0) {
							// 打开闸门
							socket_connect.gate(1);
						} else if (which == 1) {
							// 关闭闸门
							socket_connect.gate(2);
						}
						dialog.dismiss();
					}
				});
		gt_builder.create().show();
	}

	private void digital() {// 数码管
		AlertDialog.Builder dig_timeBuilder = new AlertDialog.Builder(
				MainActivity.this);
		dig_timeBuilder.setTitle("数码管");
		String[] dig_item = { "数码管显示", "数码管计时", "显示距离" };
		dig_timeBuilder.setSingleChoiceItems(dig_item, -1,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						if (which == 0) {// 数码管显示
							digitalController();

						} else if (which == 1) {// 数码管计时
							digital_time();

						} else if (which == 2) {// 显示距离
							digital_dis();

						}
						dialog.dismiss();
					}
				});
		dig_timeBuilder.create().show();
	}

	private int dgtime_index = -1;
	private void digital_time() {// 数码管计时
		AlertDialog.Builder dg_timeBuilder = new AlertDialog.Builder(
				MainActivity.this);
		dg_timeBuilder.setTitle("数码管计时");
		String[] dgtime_item = { "计时结束", "计时开始", "计时清零" };
		dg_timeBuilder.setSingleChoiceItems(dgtime_item, dgtime_index,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						if (which == 0) {// 计时结束
							socket_connect.digital_close();

						} else if (which == 1) {// 计时开启
							socket_connect.digital_open();

						} else if (which == 2) {// 计时清零
							socket_connect.digital_clear();

						}
						dialog.dismiss();
					}
				});
		dg_timeBuilder.create().show();
	}

	private int dgdis_index = -1;

	private void digital_dis() {
		AlertDialog.Builder dis_timeBuilder = new AlertDialog.Builder(
				MainActivity.this);
		dis_timeBuilder.setTitle("显示距离");
		final String[] dis_item = { "10cm", "20cm", "40cm" };
		dis_timeBuilder.setSingleChoiceItems(dis_item, dgdis_index,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						if (which == 0) {// 距离10cm
							socket_connect.digital_dic(Integer.parseInt(dis_item[which]
									.substring(0, 2)));
						} else if (which == 1) {// 距离20cm
							socket_connect.digital_dic(Integer.parseInt(dis_item[which]
									.substring(0, 2)));
						} else if (which == 2) {// 距离40cm
							socket_connect.digital_dic(Integer.parseInt(dis_item[which]
									.substring(0, 2)));
						}
						dialog.dismiss();
					}
				});
		dis_timeBuilder.create().show();
	}



	// 数码管显示方法
	private String[] itmes = { "1", "2" };
	int main, one, two, three;

	private void digitalController() {

		AlertDialog.Builder dg_Builder = new AlertDialog.Builder(
				MainActivity.this);
		View view = LayoutInflater.from(MainActivity.this).inflate(
				R.layout.item_digital, null);
		dg_Builder.setTitle("数码管显示");
		dg_Builder.setView(view);
		// 下拉列表
		Spinner spinner = (Spinner) view.findViewById(R.id.spinner);
		final EditText editText1 = (EditText) view.findViewById(R.id.editText1);
		final EditText editText2 = (EditText) view.findViewById(R.id.editText2);
		final EditText editText3 = (EditText) view.findViewById(R.id.editText3);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(
				MainActivity.this, android.R.layout.simple_spinner_item, itmes);
		spinner.setAdapter(adapter);
		// 下拉列表选择监听
		spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				main = position + 1;
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub

			}
		});
		dg_Builder.setPositiveButton("确定",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						String ones = editText1.getText().toString();
						String twos = editText2.getText().toString();
						String threes = editText3.getText().toString();
						// 显示数据，一个文本编译框最多两个数据显示数目管中两个数据
						if (ones.equals(""))
							one = 0x00;
						else
							one = Integer.parseInt(ones) / 10 * 16
									+ Integer.parseInt(ones) % 10;
						if (twos.equals(""))
							two = 0x00;
						else
							two = Integer.parseInt(twos) / 10 * 16
									+ Integer.parseInt(twos) % 10;
						if (threes.equals(""))
							three = 0x00;
						else
							three = Integer.parseInt(threes) / 10 * 16
									+ Integer.parseInt(threes) % 10;
						socket_connect.digital(main, one, two, three);
					}
				});

		dg_Builder.setNegativeButton("取消",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						dialog.cancel();
					}
				});
		dg_Builder.create().show();
	}


	private void magnetic_suspension()
	{
		Builder builder = new Builder(MainActivity.this);
		builder.setTitle("磁悬浮");
		String[] item2 = { "开", "关" };
		builder.setSingleChoiceItems(item2,-1,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						if (which == 0) {
							socket_connect.magnetic_suspension(0x01,0x01,0x00,0x00);
						} else if (which == 1) {
							socket_connect.magnetic_suspension(0x01,0x02,0x00,0x00);
						}
						dialog.dismiss();
					}
				});
		builder.create().show();
	}

	private void TFT_LCD()
	{
		Builder TFTbuilder = new Builder(MainActivity.this);
		TFTbuilder.setTitle("TFT显示器");
		String[] TFTitem = { "图片显示模式", "车牌显示","计时模式","距离显示","HEX显示模式" };
		TFTbuilder.setSingleChoiceItems(TFTitem,-1,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						switch(which)
						{
						case 0:
							TFT_Image();
							break;
						case 1:
							TFT_plate_number();
							break;
						case 2:
							TFT_Timer();
							break;
						case 3:
							Distance();
							break;
						case 4:
							Hex_show();
							break;
						}
						dialog.dismiss();
					}
				});
		TFTbuilder.create().show();
	}

	private void TFT_Image()
	{
		Builder TFT_Image_builder = new Builder(MainActivity.this);
		TFT_Image_builder.setTitle("图片显示模式");
		String[] TFT_Image_item = {"指定显示","上翻一页","下翻一页","自动翻页"};
		TFT_Image_builder.setSingleChoiceItems(TFT_Image_item, -1, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO 自动生成的方法存根
				switch(which)
				{
				case 0:
					LCD_vo_show();
					break;
				case 1:
					socket_connect.TFT_LCD(0x10, 0x01, 0x00, 0x00);
					break;
				case 2:
					socket_connect.TFT_LCD(0x10, 0x02, 0x00, 0x00);
					break;
				case 3:
					socket_connect.TFT_LCD(0x10, 0x03, 0x00, 0x00);
					break;
				}
				dialog.cancel();
			}
		});
		TFT_Image_builder.create().show();
	}

	private void LCD_vo_show()
	{
		Builder TFT_Image_builder = new Builder(MainActivity.this);
		TFT_Image_builder.setTitle("指定图片显示");
		String[] TFT_Image_item = {"1","2","3","4","5"};
		TFT_Image_builder.setSingleChoiceItems(TFT_Image_item, -1, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO 自动生成的方法存根
				switch(which)
				{
				case 0:
					socket_connect.TFT_LCD(0x00, 0x01, 0x00, 0x00);
					break;
				case 1:
					socket_connect.TFT_LCD(0x00, 0x02, 0x00, 0x00);
					break;
				case 2:
					socket_connect.TFT_LCD(0x00, 0x03, 0x00, 0x00);
					break;
				case 3:
					socket_connect.TFT_LCD(0x00, 0x04, 0x00, 0x00);
					break;
				case 4:
				    socket_connect.TFT_LCD(0x00, 0x05, 0x00, 0x00);
					break;
				}
				dialog.cancel();
			}
		});
		TFT_Image_builder.create().show();
	}


	int Car_one, Car_two, Car_three, Car_four, Car_five, Car_six;
	private void TFT_plate_number()
	{
		Builder TFT_plate_builder = new Builder(MainActivity.this);
		TFT_plate_builder.setTitle("车牌显示模式");
		final String[] TFT_Image_item = {"A123B4","B567C8","D910E1"};
		TFT_plate_builder.setSingleChoiceItems(TFT_Image_item, -1, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO 自动生成的方法存根
				switch(which)
				{
				case 0:
					socket_connect.TFT_LCD(0x20, 'A', '1', '2');
					socket_connect.TFT_LCD(0x20, '3', 'B', '4');
					break;
				case 1:
					socket_connect.TFT_LCD(0x20, 'B', '5', '6');
					socket_connect.TFT_LCD(0x20, '7', 'C', '8');
					break;
				case 2:
					socket_connect.TFT_LCD(0x20, 'D', '9', '1');
					socket_connect.TFT_LCD(0x20, '0', 'E', '1');
					break;
				}
				dialog.cancel();
			}
		});
		TFT_plate_builder.create().show();
	}

	private void TFT_Timer()
	{
		Builder TFT_Iimer_builder = new Builder(MainActivity.this);
		TFT_Iimer_builder.setTitle("计时模式");
		String[] TFT_Image_item = {"开始","关闭","停止"};
		TFT_Iimer_builder.setSingleChoiceItems(TFT_Image_item, -1, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO 自动生成的方法存根
				switch(which)
				{
				case 0:
					socket_connect.TFT_LCD(0x30, 0x01, 0x00, 0x00);
					break;
				case 1:
					socket_connect.TFT_LCD(0x30, 0x02, 0x00, 0x00);
					break;
				case 2:
					socket_connect.TFT_LCD(0x30, 0x00, 0x00, 0x00);
					break;
				}
				dialog.cancel();
			}
		});
		TFT_Iimer_builder.create().show();
	}

	private void Distance()
	{
		Builder TFT_Distance_builder = new Builder(MainActivity.this);
		TFT_Distance_builder.setTitle("距离显示模式");
		String[] TFT_Image_item = {"10cm","20cm","30cm"};
		TFT_Distance_builder.setSingleChoiceItems(TFT_Image_item, -1, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO 自动生成的方法存根
				if(which == 0)
				{
					socket_connect.TFT_LCD(0x50, 0x00, 0x01, 0x00);
				}
				if(which == 1)
				{
					socket_connect.TFT_LCD(0x50, 0x00, 0x02, 0x00);
				}
				if(which == 2)
				{
					socket_connect.TFT_LCD(0x50, 0x00, 0x03, 0x00);
				}
				dialog.cancel();
			}
		});
		TFT_Distance_builder.create().show();
	}

	private void Hex_show()
	{
		Builder TFT_Hex_builder = new Builder(MainActivity.this);
		TFT_Hex_builder.setTitle("HEX显示模式");
		String[] TFT_Image_item = {"暂定"};
		TFT_Hex_builder.setSingleChoiceItems(TFT_Image_item, -1, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO 自动生成的方法存根
			}
		});
		TFT_Hex_builder.create().show();
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		socket_connect.onDestory();
	}
}

*
* */