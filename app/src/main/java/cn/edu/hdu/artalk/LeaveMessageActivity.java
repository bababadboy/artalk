package cn.edu.hdu.artalk;

import androidx.appcompat.app.AppCompatActivity;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.sql.Date;
import java.text.SimpleDateFormat;

//AR界面_放置留言
public class LeaveMessageActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private TextView lm_date,lm_time;
    private ImageView lm_start;
    private ImageButton lm_mic;
    private EditText lm_et;


    private boolean isStart = false;
    private MediaRecorder mr = null;
    private MediaPlayer mp=null;
    private File dir;
    private File soundFile;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_leave_message);

        lm_date = findViewById(R.id.lm_tv1);
        lm_time=findViewById(R.id.lm_tv3);
        lm_mic = findViewById(R.id.lm_mic);
        lm_et = findViewById(R.id.lm_et);
        lm_start=findViewById(R.id.lm_start);

        //显示时间
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd   HH:mm:ss");
        Date curDate = new Date(System.currentTimeMillis());
        lm_date.setText(simpleDateFormat.format(curDate));
//缓存声音
        dir = new File(Environment.getExternalStorageDirectory(),"sounds");
        if(!dir.exists()){
            dir.mkdirs();
        }
        soundFile = new File(dir,System.currentTimeMillis()+".amr");
        if(!soundFile.exists()){
            try {
                soundFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        lm_mic.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        startRecord();
                        updateMicStatus();
                        lm_time.setText("松开停止录制");
                        isStart=true;
                        break;
                    case MotionEvent.ACTION_UP:
                        stopRecord();
                        lm_time.setText("录音完毕");
                        lm_mic.setImageDrawable(getResources().getDrawable(R.drawable.leavemessage_mic_finitsh));
                        isStart = false;
                        lm_start.setVisibility(View.VISIBLE);
                        break;
                    default:
                         break;
                }
                return false;
            }
        });
        lm_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mp=new MediaPlayer();
                try {
                    mp.setDataSource(soundFile.getPath());
                    mp.prepare();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                mp.start();
            }
        });



    }
    //开始录制
    private void startRecord(){

        if(mr == null){

            mr = new MediaRecorder();
            mr.setAudioSource(MediaRecorder.AudioSource.MIC);  //音频输入源
            mr.setOutputFormat(MediaRecorder.OutputFormat.AMR_WB);   //设置输出格式
            mr.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_WB);   //设置编码格式
            mr.setOutputFile(soundFile.getAbsolutePath());
            try {
                mr.prepare();
                mr.start();  //开始录制

            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        else mr.reset();
    }


    private void stopRecord(){
        if(mr != null){
            mr.stop();//停止录制
            mr.release();//资源释放
            mr = null;
        }
    }

    private final Handler mHandler = new Handler();
    private Runnable mUpdateMicStatusTimer = new Runnable() {
        public void run() {
            updateMicStatus();
        }
    };
    /**
     * 更新话筒状态
     *
     */
    private int BASE = 1;
    private int SPACE = 100;// 间隔取样时间

    private void updateMicStatus() {
        if (mr != null) {
            double ratio = (double)mr.getMaxAmplitude() /BASE;
            double db = 0;// 分贝
            if (ratio > 1)
                db = 20 * Math.log10(ratio);
            Log.d(TAG,"分贝值："+db);
            lm_mic.getDrawable().setLevel((int) (3000 + 6000 * db / 100));
            mHandler.postDelayed(mUpdateMicStatusTimer, SPACE);
        }
    }
    //释放资源
    private void releaseRecord(){

    }

}