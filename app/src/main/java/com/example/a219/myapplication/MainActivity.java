package com.example.a219.myapplication;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.InterruptedIOException;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    public static ArrayList<Item_card_later> lateritems;    //최근 검색 기록

    Button btn_ok;  //검색관련
    EditText edt;   //검색관련
    String url;     //검색관련
    ProgressDialog dialog;
    Button btn_later;

//리사이클러뷰를 위한 모든 변수
//-------------------------------------------------------------------------------------------------------------------
    int order;                                      //박스오피스 다양성을 위해 우선순위
    TextView office_tv[];                           //박스오피스 다양성 텍스트
    int c;                                           //박스오피스 다양성을 위한 카운터

    int boxcount;                                   //박스오피스 카운트(이미지 박스오피스)
    String []boxtitle;                              //박스 오피스를 설정할 영화이름
    Bitmap []bitmap;                                //박스 오피스의 이미지를 저장할 변수
    String []boxURL;                                //박스오피스 이미지의 URL를 저장할 변수

    private RecyclerView mRecyclerView;                     //리사이클러뷰
    private RecyclerView.Adapter mAdapter;                  //리사이클러뷰 어댑터
    private RecyclerView.LayoutManager mLayoutManager;      //리사이클러뷰 레이아웃 매니저
    private ArrayList<Item_card> items;                     //리사이클러뷰 리스트
//----------------------------------------------------------------------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lateritems = new ArrayList<>();
        btn_later = (Button) findViewById(R.id.later_btn_main);
        dialog = ProgressDialog.show(MainActivity.this, "","로딩 중 입니다...", true);

        btn_ok = (Button) findViewById(R.id.search_btn_main);   //검색 화면 이동
        edt = (EditText) findViewById(R.id.search_edt_main);     //검색창 연결
        office_tv = new TextView[10];
        c = 0;
        office_tv[0] = (TextView) findViewById(R.id.text_office1);
        office_tv[1] = (TextView) findViewById(R.id.text_office2);
        office_tv[2] = (TextView) findViewById(R.id.text_office3);
        office_tv[3] = (TextView) findViewById(R.id.text_office4);
        office_tv[4] = (TextView) findViewById(R.id.text_office5);
        office_tv[5] = (TextView) findViewById(R.id.text_office6);
        office_tv[6] = (TextView) findViewById(R.id.text_office7);
        office_tv[7] = (TextView) findViewById(R.id.text_office8);
        office_tv[8] = (TextView) findViewById(R.id.text_office9);
        office_tv[9] = (TextView) findViewById(R.id.text_office10);


        //최근 검색 기록 이벤트
        btn_later.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this,LaterActivity.class);
                startActivity(i);
            }
        });

        //검색 클릭 이벤트
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String key = edt.getText().toString();
                edt.setText("");
                url = String.format("https://openapi.naver.com/v1/search/movie.xml?query=%s&display=50",key);
                Intent i = new Intent(MainActivity.this,SearchActivity.class);
                i.putExtra("url",url);
                startActivity(i);
            }
        });
        //------------------------------------------------------------------------------------------------------------------------------------------
        //박스 오피스 10순위를 저장할 배열 초기화
        order = 0;
        boxcount = 0;
        bitmap = new Bitmap[10];
        boxtitle = new String[10];
        boxURL = new String[10];
        //-------------------------------------------------------------------------------------------------------------------------------------------

        items = new ArrayList<>();                                                                                              //박스 오피스를 저장할 어레이배열
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_main);                                                    //리사이클러뷰 설정
        mRecyclerView.setHasFixedSize(true);                                                                                 //리사이클러뷰 설정
        mLayoutManager = new StaggeredGridLayoutManager(1,StaggeredGridLayoutManager.HORIZONTAL);                           //레이아웃 설정
        mRecyclerView.setLayoutManager(mLayoutManager);                                                                    //리사이클러뷰 레이아웃 연결
        mAdapter = new MyAdapter(items);                                                                                      //어댑터 연결
        mRecyclerView.setAdapter(mAdapter);                                                                                 //리사이클러뷰 어댑터 설정

        //------------------------------------------------------------------------------------------------------------------------------------------
        //박스 오피스 순위와 이미지 링크를 연속으로 파싱한다. (오피스 1~10순위 이름을 가져옴 -> 그 이름으로 다시 파싱하여 이미지 URL 검색 )
        String url1 = String.format("http://www.kobis.or.kr/kobisopenapi/webservice/rest/boxoffice/searchDailyBoxOfficeList.xml?" +
                "key=42a9f81555d9f66b90a6b811c66c367c&targetDt=%s",getDate());
        DownloadTask_Rank boxTask = new DownloadTask_Rank();
        boxTask.execute(url1);


    }





    //리사이클러뷰 어댑터------------------------------------------------------------------------------------------------------------------------------
    public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
        private ArrayList<Item_card> mDataset;

        //뷰홀더 설정
        public class ViewHolder extends RecyclerView.ViewHolder {

            public ImageView mImageView;
            public TextView mTextView;
            public CardView cardView;

            public ViewHolder(View view) {
                super(view);
                mImageView = (ImageView)view.findViewById(R.id.card_img);
                mTextView = (TextView)view.findViewById(R.id.card_title);
                cardView = (CardView)view.findViewById(R.id.cardview);
               }
        }

        //생성자
        public MyAdapter(ArrayList<Item_card> myDataset) {
            mDataset = myDataset;
        }

        @Override
        public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cardview_horizontal, parent, false);
            ViewHolder vh = new ViewHolder(v);
            return vh;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, final int position) {
            // - get element from your dataset at this position
            // - replace the contents of the view with that element
            holder.mTextView.setText(mDataset.get(position).imagetitle);
            holder.mImageView.setImageBitmap(mDataset.get(position).image);
            holder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(MainActivity.this,info_Activity.class);
                    i.putExtra("title",mDataset.get(position).imagetitle);
                    i.putExtra("etitle",mDataset.get(position).eTitle);
                    i.putExtra("ptitle",mDataset.get(position).pTitle);
                    i.putExtra("dtitle",mDataset.get(position).dTitle);
                    i.putExtra("atitle",mDataset.get(position).aTitle);
                    i.putExtra("rtitle",mDataset.get(position).rTitle);
                    i.putExtra("bitmap",mDataset.get(position).image);
                    startActivity(i);
                }
            });

        }

        @Override
        public int getItemCount() {
            return mDataset.size();
        }
    }


    //시스템 날짜를 구하는 메소드 (오피스 순위를 구하기 위해 어제 날짜를 리턴)--------------------------------------------------------------------------
    private String getDate(){
        long now = System.currentTimeMillis();                                                //현재 시간을 msec으로 구한다.
        Date date = new Date(now);                                                            //현재 시간 저장
        SimpleDateFormat CurDateFormat = new SimpleDateFormat("yyyyMMdd");                  //현재 시간을 나타낼 방식 포맷
        date.setTime ( date.getTime ( ) - ( (long) 1000 * 60 * 60 * 24 ) );                   //현재시간을 어제 시간으로 계산
        String current_data = CurDateFormat.format(date);                                      //구한 어제 날짜 String 변환
        return  current_data;
    }


    //오피스박스 테스크---------------------------------------------------------------------------------------------------------------------------------
    public class DownloadTask_Rank extends AsyncTask<String, Void, String> {

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected String doInBackground(String... params) {
            if (this.isCancelled()) {
                return null;
            }
            try{
                return (String)downloadUrl(params[0]);
            }
            catch (IOException ex){
                return "인터넷 연결을 확인해주세요";
            }
        }
        @Override
        protected void onPostExecute(String s) {
            parseXml(s);

        }
        @Override
        protected void onProgressUpdate(Void... values) { super.onProgressUpdate(values);
        }

        private String downloadUrl(String myurl) throws IOException{ //HTTP 로 URL 접속을 위한 객체 생성
            HttpURLConnection conn = null;
            try{
                URL url = new URL(myurl);
                conn = (HttpURLConnection) url.openConnection();
                InputStream is = conn.getInputStream();
                BufferedInputStream bufIs = new BufferedInputStream(is); InputStreamReader isReader = new InputStreamReader(bufIs, "utf-8"); BufferedReader bufReader = new BufferedReader(isReader);
                String line = null;
                StringBuilder builder = new StringBuilder();
                while (null != (line = bufReader.readLine())){ builder.append(line);
                }
                return builder.toString(); } finally {
                conn.disconnect();
            }
        }
        private void parseXml(String result) {
            try {
                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                factory.setNamespaceAware(true);
                XmlPullParser parser = factory.newPullParser();

                parser.setInput(new StringReader(result));

                int eventType = parser.getEventType();
                StringBuilder builer = new StringBuilder();

                String cTitle = "";
                String cArtist = "";
                int i = 0;

                boolean bTitle = false;
                boolean bArtist = false;

                while (eventType != XmlPullParser.END_DOCUMENT) {
                    if (eventType == XmlPullParser.START_DOCUMENT) {
                        ;
                    } else if (eventType == XmlPullParser.START_TAG) {
                        String tag_name = parser.getName();
                        if (tag_name.equals("movieNm")) {
                            bTitle = true;
                        } else if (tag_name.equals("openDt")) {
                            bArtist = true;
                        }
                    } else if (eventType == XmlPullParser.TEXT) {
                        if (bTitle) {
                            cTitle = parser.getText();
                            bTitle = false;
                        } else if (bArtist) {
                            cArtist = parser.getText();
                            bArtist = false;
                        }
                    } else if (eventType == XmlPullParser.END_TAG) {
                        String tag_name = parser.getName();
                        if (tag_name.equals("dailyBoxOffice")) {

                            if(order == 0) {
                                boxtitle[i] = cTitle;
                                String url2 = String.format("https://openapi.naver.com/v1/search/movie.xml?query=%s&display=1", boxtitle[i]);
                                DownloadTask_Rank_IMG itesk = new DownloadTask_Rank_IMG();
                                itesk.execute(url2);
                                i++;
                                cTitle = "";
                            }else{
                                office_tv[c].setText(cTitle);
                                c++;
                            }
                        }
                    }
                    eventType = parser.next();
                }
            } catch (Exception ex) {

            }
        }
    }

    //네이버 테스크--------------------------------------------------------------------------------------------------------------------------------
    public class DownloadTask_Rank_IMG extends AsyncTask<String, Void, String> {

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected String doInBackground(String... params) {

            if (this.isCancelled()) {
                return null;
            }
            try{
                return (String)downloadUrl(params[0]);
            }
            catch (IOException ex){
                return "인터넷 연결을 확인해주세요";
            }

        }
        @Override
        protected void onPostExecute(String s) {
            parseXml(s);
        }
        @Override
        protected void onProgressUpdate(Void... values) { super.onProgressUpdate(values);
        }
        private String downloadUrl(String myurl) throws IOException{ //HTTP 로 URL 접속을 위한 객체 생성
            HttpURLConnection conn = null;
            try{
                URL url = new URL(myurl);
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestProperty("X-Naver-Client-Id", "4rS4wvjLnsQe8rq0pxkG");
                conn.setRequestProperty("X-Naver-Client-Secret", "jshCOMQFUM");
                InputStream is = conn.getInputStream();
                BufferedInputStream bufIs = new BufferedInputStream(is); InputStreamReader isReader = new InputStreamReader(bufIs, "utf-8"); BufferedReader bufReader = new BufferedReader(isReader);
                String line = null;
                StringBuilder builder = new StringBuilder();
                while (null != (line = bufReader.readLine())){ builder.append(line);
                }
                return builder.toString(); } finally {
                conn.disconnect();
            }
        }
        private void parseXml(String result){
            try {
                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                factory.setNamespaceAware(true);
                XmlPullParser parser = factory.newPullParser();

                parser.setInput(new StringReader(result));

                int eventType = parser.getEventType();

                String cTitle = "";     //제목
                String cArtist = "";    //영어제목
                String cUrl = "";       //비트맵URL
                String cPTitle = "";     //개봉일자
                String cDTitle = "";     //감독
                String cATitle = "";    //출연자
                String cRTitle = "";    //평점

                boolean bTitle = false;
                boolean bArtist = false;
                boolean bUrl = false;
                boolean bPtitle = false;
                boolean bDtitle = false;
                boolean bAtitle = false;
                boolean bRtitle = false;

                while (eventType != XmlPullParser.END_DOCUMENT){
                    if(eventType == XmlPullParser.START_DOCUMENT){
                        ;
                    }else if(eventType == XmlPullParser.START_TAG){
                        String tag_name = parser.getName();
                        if(tag_name.equals("title")){
                            bTitle = true;
                        }else if(tag_name.equals("subtitle")){
                            bArtist = true;
                        }else if(tag_name.equals("image")){
                            bUrl = true;
                        }else if(tag_name.equals("pubDate")){
                            bPtitle = true;
                        }else if(tag_name.equals("director")){
                            bDtitle = true;
                        }else if(tag_name.equals("actor")){
                            bAtitle = true;
                        }else if(tag_name.equals("userRating")){
                            bRtitle = true;
                        }
                    }else if(eventType == XmlPullParser.TEXT){
                        if(bTitle){
                            cTitle = parser.getText();
                            bTitle = false;
                            bPtitle = false;
                            bDtitle = false;
                            bUrl = false;
                            bArtist = false;
                            bAtitle = false;
                            bRtitle = false;
                        }else if(bArtist){
                            cArtist = parser.getText();
                            bTitle = false;
                            bPtitle = false;
                            bDtitle = false;
                            bUrl = false;
                            bArtist = false;
                            bAtitle = false;
                            bRtitle = false;
                        }else if(bUrl){
                            cUrl = parser.getText();
                            bTitle = false;
                            bPtitle = false;
                            bDtitle = false;
                            bUrl = false;
                            bArtist = false;
                            bAtitle = false;
                            bRtitle = false;
                        }else if(bAtitle){
                            cATitle = parser.getText();
                            bTitle = false;
                            bPtitle = false;
                            bDtitle = false;
                            bUrl = false;
                            bArtist = false;
                            bAtitle = false;
                            bRtitle = false;
                        }else if(bRtitle){
                            cRTitle = parser.getText();
                            bTitle = false;
                            bPtitle = false;
                            bDtitle = false;
                            bUrl = false;
                            bArtist = false;
                            bAtitle = false;
                            bRtitle = false;
                        }else if(bDtitle){
                            cDTitle = parser.getText();
                            bTitle = false;
                            bPtitle = false;
                            bDtitle = false;
                            bUrl = false;
                            bArtist = false;
                            bAtitle = false;
                            bRtitle = false;
                        }else if(bPtitle){
                            cPTitle = parser.getText();
                            bTitle = false;
                            bPtitle = false;
                            bDtitle = false;
                            bUrl = false;
                            bArtist = false;
                            bAtitle = false;
                            bRtitle = false;
                        }
                    }else if (eventType == XmlPullParser.END_TAG){
                        String tag_name = parser.getName();
                        if(tag_name.equals("item")){
                            cTitle = cTitle.replace("<b>","");
                            cTitle = cTitle.replace("</b>","");
                            cArtist = cArtist.replace("<b>","");
                            cArtist = cArtist.replace("</b>","");
                            cDTitle  = cDTitle.replace("|",",");
                            cPTitle = cPTitle.replace("</b>","");
                            cPTitle = cPTitle.replace("<b>","");
                            cATitle = cATitle.replace("|",",");


                            boxURL[boxcount]= cUrl;
                            Thread mThread = new Thread(){      //이미지를 설정한다.
                                @Override
                                public void run() {
                                    try {
                                        URL imgurl = new URL(boxURL[boxcount]);
                                        URLConnection imgconn = imgurl.openConnection();
                                        imgconn.setDoInput(true);
                                        imgconn.connect();
                                        BufferedInputStream bis = new BufferedInputStream(imgconn.getInputStream());
                                        bitmap[boxcount] = BitmapFactory.decodeStream(bis);
                                    }catch (Exception e){}
                                }
                            };

                            mThread.start();
                            try{
                                mThread.join();

                                items.add(new Item_card(bitmap[boxcount],boxtitle[boxcount],cArtist,cPTitle,cDTitle,cATitle,cRTitle));
                                mAdapter.notifyDataSetChanged();
                            }catch(InterruptedException e){
                            }

                            boxcount++;
                            cTitle = "";
                            cArtist = "";
                            cPTitle = "";
                            cUrl = "";
                            cDTitle = "";
                            cArtist = "";
                            cRTitle = "";
                        }
                    }
                    eventType = parser.next();
                }
            } catch (Exception ex) {
            }

            dialog.dismiss();
            String url2 = String.format("http://www.kobis.or.kr/kobisopenapi/webservice/rest/boxoffice/searchDailyBoxOfficeList.xml?key=42a9f81555d9f66b90a6b811c66c367c&targetDt=%s&multiMovieYn=Y",getDate());
            DownloadTask_Rank boxTask2 = new DownloadTask_Rank();
            boxTask2.execute(url2);
            order = 1;
        }
    }
    //----------------------------------------------------------------------------------------------------------------------------------------
    @Override

    public void onBackPressed() {
        AlertDialog.Builder arBuilder = new AlertDialog.Builder(MainActivity.this);
        arBuilder.setMessage("앱을 종료하시겠습니까?");

        arBuilder.setPositiveButton("네", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        arBuilder.setNegativeButton("아니요",null);
        arBuilder.show();
    }
}
