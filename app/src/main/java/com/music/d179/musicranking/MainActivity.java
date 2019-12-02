package com.music.d179.musicranking;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.os.AsyncTask;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private ListView musicListView;

    int melonCnt = 1;
    String melonCssQuery = "tr.lst50 td div.rank01 span a";
    int genieCnt = 1;
    int naverCnt = 1;
    int mnetCnt = 1;


    private TextView searchTextTv;
    private Button searchBtn;

    private ArrayList melonArr;
    private ArrayList genieArr;
    private ArrayList naverArr;
    private ArrayList bugsArr;
    private ArrayList soribadaArr;
    private ArrayList mnetArr;

    private ArrayList rankingList;
    private ArrayList imgList;

    private AppCompatDialog progressDialog;
    private String searchText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        melonArr = new ArrayList();
        genieArr = new ArrayList();
        naverArr = new ArrayList();
        bugsArr = new ArrayList();
        soribadaArr = new ArrayList();
        mnetArr = new ArrayList();

        rankingList = new ArrayList();
        imgList = new ArrayList();

        Bitmap bm1 = BitmapFactory.decodeResource(getResources(), R.drawable.melon);
        Bitmap bm2 = BitmapFactory.decodeResource(getResources(), R.drawable.genie);
        Bitmap bm3 = BitmapFactory.decodeResource(getResources(), R.drawable.naver);
        Bitmap bm4 = BitmapFactory.decodeResource(getResources(), R.drawable.bugs);
        Bitmap bm5 = BitmapFactory.decodeResource(getResources(), R.drawable.soribada);
        Bitmap bm6 = BitmapFactory.decodeResource(getResources(), R.drawable.mnet);
        imgList.add(bm1);
        imgList.add(bm2);
        imgList.add(bm3);
        imgList.add(bm4);
        imgList.add(bm5);
        imgList.add(bm6);

        musicListView = findViewById(R.id.musicListView);

        searchTextTv = (TextView)findViewById(R.id.searchTextTv);
        searchBtn = (Button)findViewById(R.id.searchBtn);

        //resultTv.setMovementMethod(new ScrollingMovementMethod()); //스크롤 가능한 텍스트뷰로 만들기

        ArrayList<Bitmap> imgList = new ArrayList<Bitmap>();
        ArrayList<String> rankingList = new ArrayList<String>();

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                searchText  = searchTextTv.getText().toString().replaceAll(" ","").toLowerCase();

                genieCnt=1;
                naverCnt=1;
                mnetCnt=1;
                melonCnt = 1;
                melonCssQuery = "tr.lst50 td div.rank01 span a";

                //크롤링
                JsoupAsyncTask jsoupAsyncTask = new JsoupAsyncTask();
                jsoupAsyncTask.execute();

                progressON(MainActivity.this, "Loading...");

            }
        });
    }

    public void progressON(Activity activity, String message) {

        progressDialog = new AppCompatDialog(activity);;

        if (activity == null || activity.isFinishing()) {
            return;
        }

        if (progressDialog != null && progressDialog.isShowing()) {progressSET(message);
        } else {

            //progressDialog = new AppCompatDialog(activity);
            progressDialog.setCancelable(false);
            progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            progressDialog.setContentView(R.layout.progress_loading);
            progressDialog.show();

        }


        final ImageView img_loading_frame = (ImageView) progressDialog.findViewById(R.id.iv_frame_loading);

        //Gif 용
        final GlideDrawableImageViewTarget gifImage = new GlideDrawableImageViewTarget(img_loading_frame);
        Glide.with(this).load(R.drawable.heart_gif).into(gifImage);

        //일반 이미지용
        //final AnimationDrawable frameAnimation = (AnimationDrawable) img_loading_frame.getBackground();

        img_loading_frame.post(new Runnable() {
            @Override
            public void run() {
                gifImage.onStart();
            }
        });

        TextView tv_progress_message = (TextView) progressDialog.findViewById(R.id.tv_progress_message);
        if (!TextUtils.isEmpty(message)) {
            tv_progress_message.setText(message);
        }


    }

    public void progressOFF() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    public void progressSET(String message) {

        if (progressDialog == null || !progressDialog.isShowing()) {
            return;
        }

        TextView tv_progress_message = (TextView) progressDialog.findViewById(R.id.tv_progress_message);
        if (!TextUtils.isEmpty(message)) {
            tv_progress_message.setText(message);
        }

    }

    private class JsoupAsyncTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        public void getMelonList(){
            //멜론
            try {

                String melonHomePageUrl = "https://www.melon.com/chart/index.htm#params%5Bidx%5D="+melonCnt; //파싱할 홈페이지의 URL주소
                //Log.e("melonHomePageUrl : ", melonHomePageUrl);
                Document doc2 = Jsoup.connect(melonHomePageUrl).get();
                Elements titles2 = doc2.select(melonCssQuery);

                for(Element e: titles2){
                    String lowerTitle = e.text().toLowerCase();
                    String trimTitle =lowerTitle.replaceAll(" ","");

                    int deleteFromIndex = trimTitle.indexOf("(");
                    String lastTitle ="";
                    //괄호가 있을 경우 삭제해서 넣기 위한 if문
                    if(deleteFromIndex != -1){
                        lastTitle = trimTitle.substring(0, deleteFromIndex);
                    }else{
                        lastTitle=trimTitle;
                    }
                    //Log.e("melon Title :", lastTitle);
                    melonArr.add(lastTitle);
                }
                melonCnt = 51;
                melonCssQuery = "tr.lst100 td div.rank01 span a";
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void getGinieList(){
            try {

                long now = System.currentTimeMillis();

                Date date = new Date(now);

                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHH");

                String todayTime = sdf.format(date);

                String today  = todayTime.substring(0,8);
                String time = todayTime.substring(8,10);

                //지니
                String ginieHomePageUrl = "https://www.genie.co.kr/chart/top200?ditc=D&ymd="+today+"&hh="+time+"&rtm=Y&pg="+genieCnt; //파싱할 홈페이지의 URL주소
                Log.e("ginieHomePageUrl : ", ginieHomePageUrl);
                Document doc = Jsoup.connect(ginieHomePageUrl).get();

                //테스트1
                Elements titles= doc.select("table.list-wrap tr.list td.info a.title");


                /*Elements ranking = doc.select("table.list-wrap tr.list td.number");

                //크롤링 자료 순위로 정렬
                Collections.sort(ranking, new Comparator<Element>() {
                    @Override
                    public int compare(Element e1, Element e2) {
                        
                        return e1.text().compareTo(e2.text());
                    }
                });*/

                for(Element e: titles){

                    //Log.e("title: ", e.text().trim());
                    String lowerTitle = e.text().toLowerCase();
                    String trimTitle =lowerTitle.replaceAll(" ","");

                    int deleteFromIndex = trimTitle.indexOf("(");
                    String lastTitle ="";
                    //괄호가 있을 경우 삭제해서 넣기 위한 if문
                    if(deleteFromIndex != -1){
                        lastTitle = trimTitle.substring(0, deleteFromIndex);
                    }else{
                        lastTitle=trimTitle;
                    }
                    Log.e("lastTitle: ", lastTitle);
                    genieArr.add(lastTitle);
                }
                genieCnt=2;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void getNaverList(){
            try {

                //네이버
                String naverHomePageUrl = "https://music.naver.com/listen/top100.nhn?domain=TOTAL_V2&duration=1h&page="+naverCnt; //파싱할 홈페이지의 URL주소

                //Log.e("naverHomePageUrl : ", naverHomePageUrl);
                Document doc = Jsoup.connect(naverHomePageUrl).get();

                //테스트1
                Elements titles= doc.select("table tbody tr._tracklist_move a._title  span.ellipsis");

                for(Element e: titles){

                    String lowerTitle = e.text().toLowerCase();
                    String trimTitle =lowerTitle.replaceAll(" ","");

                    int deleteFromIndex = trimTitle.indexOf("(");
                    String lastTitle ="";
                    //괄호가 있을 경우 삭제해서 넣기 위한 if문
                    if(deleteFromIndex != -1){
                        lastTitle = trimTitle.substring(0, deleteFromIndex);
                    }else{
                        lastTitle=trimTitle;
                    }
                    naverArr.add(lastTitle);
                }
                naverCnt=2;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void getBugsList(){
            try {
                //벅스
                String bugsHomePageUrl = "https://music.bugs.co.kr/chart"; //파싱할 홈페이지의 URL주소
                //Log.e("bugsHomePageUrl : ", bugsHomePageUrl);
                Document doc = Jsoup.connect(bugsHomePageUrl).get();

                //테스트1
                Elements titles= doc.select("section.sectionPadding table.list tbody tr th p.title a");

                for(Element e: titles){

                    String lowerTitle = e.text().toLowerCase();
                    String trimTitle =lowerTitle.replaceAll(" ","");

                    int deleteFromIndex = trimTitle.indexOf("(");
                    String lastTitle ="";
                    //괄호가 있을 경우 삭제해서 넣기 위한 if문
                    if(deleteFromIndex != -1){
                        lastTitle = trimTitle.substring(0, deleteFromIndex);
                    }else{
                        lastTitle=trimTitle;
                    }

                    bugsArr.add(lastTitle);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void getSoribadaList(){

            try {

                //Log.e("소리바다 ","시작");

                URL url = null;
                String str ="";
                String receiveMsg ="";
                try {
                    url = new URL("http://sbapi.soribada.com/charts/songs/realtime/json/?callback=jQuery111206558298669777636_1560426890118&page=1&size=100&vid=0&version=2.5&device=web&favorite=true&cachetype=charts_songs_realtime&authKey=");

                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                    if (conn.getResponseCode() == conn.HTTP_OK) {
                        InputStreamReader tmp = new InputStreamReader(conn.getInputStream(), "UTF-8");
                        BufferedReader reader = new BufferedReader(tmp);
                        StringBuffer buffer = new StringBuffer();
                        while ((str = reader.readLine()) != null) {
                            buffer.append(str);
                        }
                        receiveMsg = buffer.toString();

                        reader.close();



                        int jsonStart = receiveMsg.indexOf("(");

                        //Log.e("jsonStart : ", jsonStart+"");
                        receiveMsg = receiveMsg.substring(jsonStart+1, receiveMsg.length() -1 );
                        //Log.e("receiveMsg : ", receiveMsg);


                        JSONObject jsonObject = new JSONObject(receiveMsg);

                        //Log.e("jsonOBJ", jsonObject.toString());

                        JSONObject jsonObject1 =(JSONObject) jsonObject.get("SoribadaApiResponse");
                        JSONObject jsonObject2 =(JSONObject) jsonObject1.get("Songs");


                        JSONArray jarray =jsonObject2.getJSONArray("Song");

                        //JSONArray jarray = new JSONObject(receiveMsg).getJSONArray("Song");

                        //Log.e("jsonArray length", jarray.length()+"");

                        for (int i = 0; i < jarray.length(); i++) {
                            HashMap map = new HashMap<>();
                            JSONObject jObject = jarray.getJSONObject(i);

                            String soribadaTitle = jObject.optString("Name");

                            String lowerTitle = soribadaTitle.toLowerCase();
                            String trimTitle =lowerTitle.replaceAll(" ","");

                            int deleteFromIndex = trimTitle.indexOf("(");
                            String lastTitle ="";
                            //괄호가 있을 경우 삭제해서 넣기 위한 if문
                            if(deleteFromIndex != -1){
                                lastTitle = trimTitle.substring(0, deleteFromIndex);
                            }else{
                                lastTitle=trimTitle;
                            }

                            //Log.e("soribadaTitle : ", soribadaTitle);
                            soribadaArr.add(lastTitle);
                        }

                    } else {
                        Log.i("통신 결과", conn.getResponseCode() + "에러");
                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                /*Connection.Response response = Jsoup.connect("http://sbapi.soribada.com/charts/songs/realtime/json/?callback=jQuery111206558298669777636_1560426890118&page=1&size=100&vid=0&version=2.5&device=web&favorite=true&cachetype=charts_songs_realtime&authKey=")
                        .method(Connection.Method.GET)
                        .execute();
                Document document = response.parse();

                String text = document.text();

                Log.e("소리바다 text : ", text);*/




            } catch (Exception e) {
                e.printStackTrace();
                Log.e("soribada error:", e.toString());
            }
        }

        public void getMnetList(){
            try {
                //mnet
                long now = System.currentTimeMillis();

                Date date = new Date(now);

                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHH");

                String today = sdf.format(date);

                //Log.e("today :", today);

                String mnetHomePageUrl = "http://www.mnet.com/chart/TOP100/"+today+"?pNum="+mnetCnt; //파싱할 홈페이지의 URL주소
                //Log.e("mnetHomePageUrl : ", mnetHomePageUrl);
                Document doc = Jsoup.connect(mnetHomePageUrl).get();

                //Elements titles = doc.select("#chartsContents ul.music-list li.listen");
                Elements titles = doc.select(".MMLI_Song");

                for(Element e: titles){

                    String lowerTitle = e.text().toLowerCase();
                    String trimTitle =lowerTitle.replaceAll(" ","");

                    int deleteFromIndex = trimTitle.indexOf("(");
                    String lastTitle ="";
                    //괄호가 있을 경우 삭제해서 넣기 위한 if문
                    if(deleteFromIndex != -1){
                        lastTitle = trimTitle.substring(0, deleteFromIndex);
                    }else{
                        lastTitle=trimTitle;
                    }

                    mnetArr.add(lastTitle);
                }
                mnetCnt=2;
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("mnet error:", e.toString());
            }
        }

        @Override
        protected Void doInBackground(Void... params) {

            genieArr.clear();
            melonArr.clear();
            naverArr.clear();
            bugsArr.clear();
            soribadaArr.clear();
            mnetArr.clear();

            for(int i=0; i< 2; i++) {
                getGinieList();
                getMelonList();
                getNaverList();
                getMnetList();
            }
            getSoribadaList();
            getBugsList();

            Log.e("melon Size : ", melonArr.size()+"");

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

            rankingList.clear();

            Log.e("genie cnt", genieCnt+"");
            Log.e("mnet cnt", mnetCnt+"");
            Log.e("melon cnt", melonCnt+"");

            int melonRanking = melonArr.indexOf(searchText) + 1;
            int ginieRanking = genieArr.indexOf(searchText) + 1;
            int naverRanking = naverArr.indexOf(searchText) + 1;
            int bugsRanking = bugsArr.indexOf(searchText) + 1;
            int soribadaRanking = soribadaArr.indexOf(searchText) + 1;
            int mnetRanking = mnetArr.indexOf(searchText) + 1;

            rankingList.add(melonRanking + " 위");
            rankingList.add(ginieRanking + " 위");
            rankingList.add(naverRanking + " 위");
            rankingList.add(bugsRanking + " 위");
            rankingList.add(soribadaRanking + " 위");
            rankingList.add(mnetRanking + " 위");

            CustomTokenListAdapter adapter = new CustomTokenListAdapter(rankingList, imgList);
            musicListView.setAdapter(adapter);

            progressOFF();


            //Log.e("ginieArr size : ", genieArr.size()+"");
        }
    }
}
