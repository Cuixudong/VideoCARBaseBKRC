package xdtech.rc_car;

/**
 * Created by Administrator on 2017/9/28.
 */
import java.io.File;
import java.io.FileNotFoundException;

import com.googlecode.tesseract.android.TessBaseAPI;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

public class CodeActivity extends Activity {

    private static final int PHOTO_PICK = 0x11;// 拍照
    private static final int PHOTO_RESULT = 0x12;// 结果

    private static String LANGUAGE = "eng";
    private static String IMG_PATH = getSDPath() + java.io.File.separator + "ocrtest";

    private static TextView tvResult;
    private static ImageView ivSelected;
    private static ImageView ivTreated;
    private static Button btnCamera;
    private static Button btnSelect;
    private static CheckBox chPreTreat;
    private static RadioGroup radioGroup;
    private static String textResult;
    private static Bitmap bitmapSelected;
    private static Bitmap bitmapTreated;
    private static final int SHOWRESULT = 0x101;
    private static final int SHOWTREATEDIMG = 0x102;

    // 该handler用于处理修改结果的任务
    public static Handler myHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SHOWRESULT:
                    if (textResult.equals(""))
                        tvResult.setText("识别失败");
                    else
                        tvResult.setText(textResult);
                    break;
                case SHOWTREATEDIMG:
                    tvResult.setText("识别中......");
                    showPicture(ivTreated, bitmapTreated);
                    break;
            }
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 若文件夹不存在 首先创建文件夹
        File path = new File(IMG_PATH);
        if (!path.exists()) {
            path.mkdirs();
        }

        tvResult = (TextView) findViewById(R.id.tv_result);
        ivSelected = (ImageView) findViewById(R.id.iv_selected);
        ivTreated = (ImageView) findViewById(R.id.iv_treated);
        btnCamera = (Button) findViewById(R.id.btn_camera);
        btnSelect = (Button) findViewById(R.id.btn_select);
        chPreTreat = (CheckBox) findViewById(R.id.ch_pretreat);
        radioGroup = (RadioGroup) findViewById(R.id.radiogroup);

        btnCamera.setOnClickListener(new cameraButtonListener());
        btnSelect.setOnClickListener(new selectButtonListener());

        // 用于设置解析语言
        radioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_en:
                        LANGUAGE = "eng";
                        break;
                    case R.id.rb_ch:
                        LANGUAGE = "chi_sim";
                        break;
                }
            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == Activity.RESULT_CANCELED)
            return;

        // 处理结果
        if (requestCode == PHOTO_RESULT || requestCode == PHOTO_PICK) {
            if (requestCode == PHOTO_RESULT) {
                //bitmapSelected = BitmapUtils.getSmallBitmap(new File(IMG_PATH, "temp_cropped.jpg"));
            } else {
                //bitmapSelected = BitmapUtils.getSmallBitmap(decodeUriAsBitmap(data.getData()));
            }
            if (chPreTreat.isChecked())
                tvResult.setText("预处理中......");
            else
                tvResult.setText("识别中......");
            // 显示选择的图片
            showPicture(ivSelected, bitmapSelected);

            // 新线程来处理识别
            new Thread(new Runnable() {
                @Override
                public void run() {
                    if (chPreTreat.isChecked()) {
                        bitmapTreated = ImgPretreatment
                                .doPretreatment(bitmapSelected);
                        bitmapSelected.recycle();
                        bitmapSelected = null;                //为什么屏蔽这里
                        Message msg = new Message();
                        msg.what = SHOWTREATEDIMG;
                        myHandler.sendMessage(msg);
                        textResult = doOcr(bitmapTreated, LANGUAGE);
                    } else {
                        bitmapTreated = ImgPretreatment
                                .converyToGrayImg(bitmapSelected);
//						bitmapSelected.recycle();
//						bitmapSelected = null;
                        Message msg = new Message();
                        msg.what = SHOWTREATEDIMG;
                        myHandler.sendMessage(msg);
                        textResult = doOcr(bitmapTreated, LANGUAGE);
                    }
                    Message msg2 = new Message();
                    msg2.what = SHOWRESULT;
                    myHandler.sendMessage(msg2);
                }
            }).start();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    // 拍照识别
    class cameraButtonListener implements OnClickListener {

        @Override
        public void onClick(View arg0) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT,
                    Uri.fromFile(new File(IMG_PATH, "temp_cropped.jpg")));
            intent.putExtra("outputFormat",
                    Bitmap.CompressFormat.JPEG.toString());               //屏蔽盒里实现的效果是什么
            startActivityForResult(intent, PHOTO_RESULT);
        }
    };

    // 从相册选取照片并裁剪
    class selectButtonListener implements OnClickListener {

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("image/*");                                        //删掉了intent.putExtra所有语句是否实现的是图片切割
            startActivityForResult(intent, PHOTO_PICK);
        }
    }

    // 将图片显示在view中
    public static void showPicture(ImageView iv, Bitmap bmp){
        iv.setImageBitmap(bmp);
    }
    /**
     * 进行图片识别
     *
     * @param bitmap
     *            待识别图片
     * @param language
     *            识别语言
     * @return 识别结果字符串
     */
    public String doOcr(Bitmap bitmap, String language) {
        TessBaseAPI baseApi = new TessBaseAPI();
        baseApi.init(getSDPath(), language);
        System.gc();
        // 必须加此行，tess-two要求BMP必须为此配置
        bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        baseApi.setImage(bitmap);
        String text = baseApi.getUTF8Text();
        baseApi.clear();
        baseApi.end();
        return text;
    }

    /**
     * 获取sd卡的路径
     *
     * @return 路径的字符串
     */
    public static String getSDPath() {
        File sdDir = null;
        boolean sdCardExist = Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED); // 判断sd卡是否存在
        if (sdCardExist) {
            sdDir = Environment.getExternalStorageDirectory();// 获取外存目录
        }
        return sdDir.toString();
    }

    /**
     * 根据URI获取位图
     *
     * @param uri
     * @return 对应的位图
     */
    private Bitmap decodeUriAsBitmap(Uri uri) {
        Bitmap bitmap = null;
        try {
            bitmap = BitmapFactory.decodeStream(getContentResolver()
                    .openInputStream(uri));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
        return bitmap;
    }
}
