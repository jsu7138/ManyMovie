package com.example.a219.myapplication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

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
import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity {
    Button btn;
    EditText edt;
    String url;
    Bitmap bitmap;

    ProgressDialog dialog;

    RecyclerView recyclerView;
    RecyclerView.Adapter adapter;
    RecyclerView.LayoutManager layoutManager;
    ArrayList<Item_card_search> items;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        btn = (Button) findViewById(R.id.search_btn);
        edt = (EditText) findViewById(R.id.search_edt);

        dialog = ProgressDialog.show(SearchActivity.this, "",
                "검색 중 입니다..", true);


        items = new ArrayList<>();
        recyclerView = (RecyclerView) findViewById(R.id.recycler_search);
        recyclerView.setHasFixedSize(true);
        layoutManager = new StaggeredGridLayoutManager(1,StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new MysearchAdapter(items);
        recyclerView.setAdapter(adapter);
        items.add(new Item_card_search(null,"검색 정보가 없습니다","","","","",""));
        adapter.notifyDataSetChanged();

        Intent i = getIntent();
        url = i.getStringExtra("url");
        final NaverAPI_search tesk = new NaverAPI_search();
        tesk.execute(url);
        edt.setText("");

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog = ProgressDialog.show(SearchActivity.this, "",
                        "검색 중 입니다...", true);
                items.add(new Item_card_search(null,"검색 정보가 없습니다","","","","",""));
                adapter.notifyDataSetChanged();

                tesk.cancel(true);
                String key = edt.getText().toString();
                url = String.format("https://openapi.naver.com/v1/search/movie.xml?query=%s&display=30",key);

                NaverAPI_search tesk = new NaverAPI_search();
                tesk.execute(url);
                edt.setText("");
            }
        });

    }
    //---------------------------리사이클러뷰 어댑터 ------------------------------------------------------------------------------------

    public class MysearchAdapter extends RecyclerView.Adapter<MysearchAdapter.ViewHolder>{
        private ArrayList<Item_card_search> mDataset;

        public class ViewHolder extends RecyclerView.ViewHolder {
            public ImageView mImageView;
            public TextView TextView;
            public TextView eTextView;
            public TextView pTextView;
            public TextView dTextView;
            public CardView cardView;

            public ViewHolder(View view) {
                super(view);
                mImageView = (ImageView)view.findViewById(R.id.card_img_search);
                TextView = (TextView)view.findViewById(R.id.card_title_search);
                eTextView = (TextView)view.findViewById(R.id.card_etitle_search);
                pTextView = (TextView)view.findViewById(R.id.card_ptitle_search);
                dTextView = (TextView)view.findViewById(R.id.card_dtitle_search);
                cardView = (CardView)view.findViewById(R.id.cardview_search);
            }
        }

        public MysearchAdapter(ArrayList<Item_card_search> i ) {
            mDataset = i;
        }

        @Override
        public MysearchAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cardview_search, parent, false);
            ViewHolder vh = new ViewHolder(v);
            return vh;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, final int position) {
            holder.mImageView.setImageBitmap(mDataset.get(position).bitmap);
            holder.TextView.setText(mDataset.get(position).TItle);
            holder.eTextView.setText(mDataset.get(position).eTitle);
            holder.pTextView.setText(mDataset.get(position).pTitle);
            holder.dTextView.setText(mDataset.get(position).dTitle);

            holder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(SearchActivity.this,info_Activity.class);
                    i.putExtra("title",mDataset.get(position).TItle);
                    i.putExtra("etitle",mDataset.get(position).eTitle);
                    i.putExtra("ptitle",mDataset.get(position).pTitle);
                    i.putExtra("dtitle",mDataset.get(position).dTitle);
                    i.putExtra("atitle",mDataset.get(position).aTitle);
                    i.putExtra("rtitle",mDataset.get(position).rTitle);
                    i.putExtra("bitmap",mDataset.get(position).bitmap);
                    startActivity(i);
                }
            });
        }

        @Override
        public int getItemCount() {
            return mDataset.size();
        }
    }




    //-----------------------------네이버 파싱---------------------------------------------------------------------------------------------

    public class NaverAPI_search extends AsyncTask<String, Void, String> {

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
        //User Define Method
        private String downloadUrl(String myurl) throws IOException{ //HTTP 로 URL 접속을 위한 객체 생성
            HttpURLConnection conn = null;
            try{
//URL 객체 생성
                URL url = new URL(myurl);
//URL 객체를 이용한 HTTP 연결
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestProperty("X-Naver-Client-Id", "4rS4wvjLnsQe8rq0pxkG");
                conn.setRequestProperty("X-Naver-Client-Secret", "jshCOMQFUM");
//버퍼에 문서 다운로드 & 버퍼 내용을 UTF-8 형식으로 변환
                InputStream is = conn.getInputStream();
                BufferedInputStream bufIs = new BufferedInputStream(is); InputStreamReader isReader = new InputStreamReader(bufIs, "utf-8"); BufferedReader bufReader = new BufferedReader(isReader);
                String line = null;
                StringBuilder builder = new StringBuilder();
//버퍼 내용을 행 단위로 읽음
                while (null != (line = bufReader.readLine())){ builder.append(line);
                }
//추출한 웹 문서의 내용을 반환
                return builder.toString(); } finally {
//HTTP 연결 해제
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
                items.clear();

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

                            final String finalCUrl = cUrl;
                            Thread mThread = new Thread(){      //이미지를 설정한다.
                                @Override
                                public void run() {
                                    try {
                                        URL imgurl = new URL(finalCUrl);
                                        URLConnection imgconn = imgurl.openConnection();
                                        imgconn.setDoInput(true);
                                        imgconn.connect();
                                        BufferedInputStream bis = new BufferedInputStream(imgconn.getInputStream());
                                        bitmap = BitmapFactory.decodeStream(bis);
                                    }catch (Exception e){}
                                }
                            };

                            mThread.start();
                            try{
                                mThread.join();
                                if(!"".equals(cUrl)) {
                                    items.add(new Item_card_search(bitmap, cTitle, cArtist, cPTitle, cDTitle,cATitle,cRTitle));
                                }else{
                                    items.add(new Item_card_search(null,cTitle,cArtist,cPTitle,cDTitle,cATitle,cRTitle));
                                }
                                adapter.notifyDataSetChanged();
                            }catch(InterruptedException e){
                            }

                            cTitle = "";
                            cArtist = "";
                            cPTitle = "";
                            cUrl = "";
                            cDTitle = "";
                            cArtist = "";
                            cRTitle = "";
                            bitmap = null;
                        }
                    }
                    eventType = parser.next();
                }
                dialog.dismiss();
            } catch (Exception ex) {
                dialog.dismiss();
            }
        }
    }
}
