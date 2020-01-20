package com.example.a219.myapplication;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class info_Activity extends AppCompatActivity {
    private static int ONE_MINUTE = 5626;

    ImageView imgview;
    Button btnRegister,btnPush;
    TextView title,etitle,ptitle,dtitle,atitle,rtitle;
    String getTitle[];
    Bitmap bitmap;
    DBHelper dbHelper;
    int min,hour;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_);

        //데이터베이스 가져오기
        dbHelper = new DBHelper(info_Activity.this, "MYLIST.db", null, 1);

        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy년 MM월 dd일");
        final String Cdate = simpleDateFormat.format(date);


        getTitle = new String[6];
        Intent i = getIntent();
        getTitle[0] = i.getStringExtra("title");
        getTitle[1] = i.getStringExtra("etitle");
        getTitle[2] = i.getStringExtra("ptitle");
        getTitle[3] = i.getStringExtra("dtitle");
        getTitle[4] = i.getStringExtra("atitle");
        getTitle[5] = i.getStringExtra("rtitle");
        bitmap = (Bitmap) i.getParcelableExtra("bitmap");

        ArrayList<Item_card_later> arr = MainActivity.lateritems;
        arr.add(new Item_card_later(bitmap,getTitle[0],getTitle[1],getTitle[2],getTitle[3],getTitle[4],getTitle[5]));

        //기본처리
        for(int k = 0; k < 6 ; k++){
            if(getTitle[k].equals("")){
                getTitle[k] = "정보가 없습니다";
            }
        }
        //예외처리
        if(getTitle[4].equals("0.00")){
            getTitle[4] = "정보가 없습니다";}
        if(getTitle[5].equals("정보가 없습니다")){
            getTitle[5] = "0.00";
        }

        imgview = (ImageView)findViewById(R.id.info_img);
        title = (TextView)findViewById(R.id.info_title);
        etitle = (TextView)findViewById(R.id.info_etitle);
        ptitle = (TextView)findViewById(R.id.info_ptitle);
        dtitle = (TextView)findViewById(R.id.info_dtitle);
        atitle = (TextView)findViewById(R.id.info_atitle);
        rtitle = (TextView)findViewById(R.id.info_rtitle);

        imgview.setImageBitmap(bitmap);
        title.setText(getTitle[0]);
        etitle.setText(getTitle[1]);
        ptitle.setText("출시 : "+ getTitle[2]+"년");
        dtitle.setText("감독 : "+ getTitle[3]);
        atitle.setText("출연 : "+ getTitle[4]);
        rtitle.setText("평점 : "+ getTitle[5]+"점");

        btnRegister = (Button) findViewById(R.id.info_myRegister_input);
        btnPush = (Button) findViewById(R.id.info_push_btn);

        btnPush.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View v) {
                DialogTimePicker();
                new AlarmHATT(getApplicationContext()).Alarm();
            }
        });


        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dbHelper.insert(getTitle[0],getTitle[3],Cdate);
                Toast.makeText(info_Activity.this,"즐겨찾기 등록 완료!",Toast.LENGTH_SHORT).show();
            }
        });

    }

    public class AlarmHATT {
        private Context context;
        public AlarmHATT(Context context) {
            this.context=context;
        }
        public void Alarm() {
            AlarmManager am = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(info_Activity.this, BroadcastD.class);
            intent.putExtra("title",getTitle[0]);
            intent.putExtra("dtitle",getTitle[3]);

            PendingIntent sender = PendingIntent.getBroadcast(info_Activity.this, 0, intent, 0);

            Calendar calendar = Calendar.getInstance();
            //알람시간 calendar에 set해주기



            calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE), hour, min, 00);

            //알람 예약
            am.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), sender);
        }
    }
    private void DialogTimePicker(){
        TimePickerDialog.OnTimeSetListener mTimeSetListener =
                new TimePickerDialog.OnTimeSetListener() {
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        hour = hourOfDay;
                        min = minute;
                    }
                };
        TimePickerDialog alert = new TimePickerDialog(this,
                mTimeSetListener, 0, 0, false);
        alert.show();
    }

}
