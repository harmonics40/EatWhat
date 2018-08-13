package tw.com.flag.eatwhat;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.support.annotation.IntegerRes;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatTextView;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import android.util.Log;
public class StoreAct extends AppCompatActivity {
    private GlobalVariable globalVariable;
    private JSONObject json_read, json_write;
    private TextView storeaddr,storecell;
    private TableLayout storeLayout;
    private TableRow[] row;
    private int sp=14,sid;
    private int[] mid;
    private Gps gps3;
    private Button btn;
    private String tmp;
    double geoLatitude, geoLongitude;
    private RatingBar rb;
    private ScrollView sc;
    private TableLayout storecommit;

    private Button submit;
    RatingBar storerate;
    ScrollView sc2;
    Bundle b;

    private ConstraintLayout showInf;
    private NestedScrollView showComment,showMenu;

    private TabLayout mTabLayout;
    private static final String TAG="LogDemo";
    private EditText ed1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store);

        getSupportActionBar().setElevation(0);
        centerTitle();
        showComment=findViewById(R.id.showComment);
        showInf=findViewById(R.id.showInf);
        showMenu=findViewById(R.id.showMenu);
        sc = findViewById(R.id.sc);
        ed1 = findViewById(R.id.ed1);
        submit = findViewById(R.id.submit);
        ed1.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {
                if (ed1.hasFocus()) {
                    v.getParent().requestDisallowInterceptTouchEvent(true);
                    switch (event.getAction() & MotionEvent.ACTION_MASK){
                        case MotionEvent.ACTION_SCROLL:
                            v.getParent().requestDisallowInterceptTouchEvent(false);
                            return true;
                    }
                }
                return false;
            }
        });
        mTabLayout = findViewById(R.id.mTabLayout);
        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() { //用戶推薦or追蹤用戶推薦
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch(tab.getPosition()){
                    case 0:
                        showInf.setVisibility(View.VISIBLE);
                        showComment.setVisibility(View.INVISIBLE);
                        showMenu.setVisibility(View.INVISIBLE);
                        break;
                    case 1:
                        showInf.setVisibility(View.INVISIBLE);
                        showComment.setVisibility(View.INVISIBLE);
                        showMenu.setVisibility(View.VISIBLE);
                        break;
                    case 2:
                        showInf.setVisibility(View.INVISIBLE);
                        showComment.setVisibility(View.VISIBLE);
                        showMenu.setVisibility(View.INVISIBLE);
                        break;
                }
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }
            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });


        globalVariable = (GlobalVariable) getApplicationContext().getApplicationContext();
        storeaddr = (TextView)findViewById(R.id.storeaddr);
        storecell = (TextView)findViewById(R.id.storecell);
        rb = (RatingBar)findViewById(R.id.store_ratingbar);
        gps3 = new Gps(this);

        DisplayMetrics dm = new DisplayMetrics();   //取得螢幕寬度並設定ScrollView尺寸
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        if(dm.widthPixels<=480){
            sp=12;
        }

        try {
        json_write = new JSONObject();
        b = this.getIntent().getExtras();
        sid = Integer.parseInt(b.getString("datanum"));//取得店號
        if(b.getBoolean("mode")){//從搜尋近店家頁面
            json_write.put("action", "Store2");
            json_write.put("Id", sid);
            globalVariable.c.send(json_write);
            tmp = globalVariable.c.receive();
            json_read = new JSONObject(tmp);
            if(tmp!=null) {
                JSONArray j1 = json_read.getJSONArray("Store");
                JSONArray j2;
                for (int i = 0; i < j1.length(); i++) { //拆解接收的JSON包
                    j2 = j1.getJSONArray(i);
                    setTitle(j2.get(0).toString());//店名
                    storeaddr.setText("   "+j2.get(1).toString());
                    storecell.setText("   "+j2.get(2).toString());
                    float starmum = Float.valueOf((j2.get(3).toString()));//星星數
                    rb.setRating(starmum);//設定星星數
                }
            }
        }
        if(!b.getBoolean("mode")){//從隨機、提問進店家頁面
            setTitle(b.getString("data"));//店名
            storeaddr.setText("   "+ b.getString("dataddr"));
            storecell.setText("   "+b.getString("datacell"));
            float starmum = Float.valueOf(b.getString("datastar"));
            rb.setRating(starmum);
            json_write.put("action", "Store");
            json_write.put("Id", sid);
            globalVariable.c.send(json_write);
            tmp = globalVariable.c.receive();
            json_read = new JSONObject(tmp);
        }
        if(tmp!=null) {
            storeaddr.setOnClickListener(new View.OnClickListener() {//點擊地址導向Google map 事件
                @Override
                public void onClick(View v) {
                    String[] ad = storeaddr.getText().toString().split(":");
                    openMaptw(ad[1]);
                }
            });

            storeLayout = (TableLayout) findViewById(R.id.storeLayout);
            storeLayout.setColumnShrinkable(0, true);
            storeLayout.setColumnShrinkable(1, true);
            storeLayout.setColumnStretchable(0, true);
            storeLayout.setColumnStretchable(1, true);

            JSONArray j1 = json_read.getJSONArray("Menu");
            JSONArray j2;
            mid = new int[j1.length()];
            row = new TableRow[j1.length()];
            for (int i = 0; i < j1.length(); i++) { //動態產生TableRow
                row[i] = new TableRow(this);
                row[i].setId(i);
                storeLayout.addView(row[i]);
            }
            for (int i = 0; i < j1.length(); i++) { //拆解接收的JSON包並製作表格顯示
                j2 = j1.getJSONArray(i);

                TextView tw = new TextView(this);
                tw.setText(j2.get(0).toString());//菜名
                tw.setTextSize(TypedValue.COMPLEX_UNIT_SP, sp);
                tw.setTextColor(Color.rgb(110, 110, 110));
                row[i].addView(tw);

                tw = new TextView(this);
                String s = j2.get(1).toString();
                tw.setText(s+" 元");//價格
                tw.setTextColor(Color.rgb(110, 110, 110));
                row[i].addView(tw);


                mid[i]=Integer.parseInt(j2.get(2).toString());

                btn=new Button(this, null, android.R.attr.buttonStyleSmall);
                btn.setText("推薦");
                btn.setBackgroundTintList(getResources().getColorStateList(R.color.pink));
                btn.setTextColor(Color.WHITE);
                btn.setTextSize(TypedValue.COMPLEX_UNIT_SP, sp);
                    final int ii = i;
                    btn.setOnClickListener(new View.OnClickListener() {//推薦按鍵事件
                        @Override
                        public void onClick(View v) {
                            if( globalVariable.recmdtime <2) {//推薦次數每日2次
                                AlertDialog.Builder b = new AlertDialog.Builder(StoreAct.this);
                                b.setTitle("確認")
                                        .setMessage("確定要推薦這道菜嗎?")
                                        .setPositiveButton("GO", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                json_write = new JSONObject();
                                                try {
                                                    json_write.put("action", "Recommend");
                                                    json_write.put("Mid", mid[ii]);
                                                    globalVariable.c.send(json_write);
                                                    tmp = globalVariable.c.receive();
                                                    if (tmp != null) {
                                                        json_read = new JSONObject(tmp);
                                                        String reason = json_read.getString("data");
                                                        if (!json_read.getBoolean("check")) {//接收失敗原因
                                                        } else {
                                                            btn.setEnabled(false);
                                                            globalVariable.recmdtime++;
                                                        }
                                                        Toast.makeText(StoreAct.this, reason, Toast.LENGTH_SHORT).show();
                                                    }
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        });
                                b.setNegativeButton("Cancel", null);
                                b.show();
                            }else{
                                Toast.makeText(StoreAct.this, "本日推薦次數已用完", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                row[i].addView(btn);
            }
        }else{
            Toast.makeText(this, "連線逾時", Toast.LENGTH_LONG).show();
        }
        } catch (Exception e) {
            e.printStackTrace();
        }

        //回饋意見=========================================================================


        globalVariable = (GlobalVariable) getApplicationContext().getApplicationContext();


        storecommit = (TableLayout) findViewById(R.id.storecommit);
        storecommit.setColumnShrinkable(0, false);
        storecommit.setColumnShrinkable(1, true);
        storecommit.setColumnStretchable(0, false);
        storecommit.setColumnStretchable(1, true);
        try {
            //json_read = new JSONObject(b.getString("data"));
            JSONArray j1 = json_read.getJSONArray("Evaluation");
            JSONArray j2 = new JSONArray();
            row = new TableRow[j1.length()];
            for (int i = 0; i < j1.length(); i++) { //動態產生TableRow
                row[i] = new TableRow(this);
                row[i].setId(i);
                storecommit.addView(row[i]);
            }
            for (int i = 0; i < j1.length(); i++) { //拆解接收的JSON包並製作表格顯示
                j2 = j1.getJSONArray(i);
                TextView tw = new TextView(this);
                tw.setText(j2.get(0).toString());
                tw.setTextSize(TypedValue.COMPLEX_UNIT_SP, sp);
                tw.setTextColor(Color.BLACK);
                row[i].addView(tw);
                tw = new TextView(this);
                tw.setText(j2.get(1).toString());
                tw.setTextColor(Color.BLACK);
                row[i].addView(tw);
            }
            submit = (Button)findViewById(R.id.submit);
            ed1 = (EditText)findViewById(R.id.ed1);
            storerate = (RatingBar)findViewById(R.id.store_rate);
            if (json_read.getBoolean("check")) {
                //JSONObject json_read2 = new JSONObject("myEvaluation");
                JSONArray j3 = json_read.getJSONArray("myEvaluation");
                JSONArray j4 = new JSONArray();
                for (int i = 0; i < j3.length(); i++) { //動態產生TableRow
                    j4 = j3.getJSONArray(i);
                }
                ed1.setText(j4.get(0).toString());
                storerate.setRating(Float.valueOf(j4.get(1).toString()));
                Commented();
            }else{
                sub();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("Exception", "StoreError=" + e.toString());
        }
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)//按返回頁面關閉
    {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            this.finish();
        }
        return super.onKeyDown(keyCode, event);
    }
    public  void openMaptw(String storeaddr){//google map 路徑
        getGPFromAddress(storeaddr);
        Uri uri = Uri.parse("http://maps.google.com/maps?f=d&saddr="+String.valueOf(gps3.getGPSLatitude())+","+String.valueOf(gps3.getGPSLongitude())+"&daddr="+geoLatitude+","+geoLongitude+"&hl=tw");
        Intent it = new Intent(Intent.ACTION_VIEW);
        it.setData(uri);
        if (it.resolveActivity(getPackageManager()) != null) {
            startActivity(it);
        }
    }
    public void getGPFromAddress(String addr) {//地址轉經緯
        if (!addr.equals("")) {
            Geocoder geocoder = new Geocoder(this);
            List<Address> addresses = null;
            Address address = null;
            try {
                addresses = geocoder.getFromLocationName(addr, 1);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (addresses == null || addresses.isEmpty()) {
                Toast.makeText(this, "找不到該地址", Toast.LENGTH_SHORT).show();
            } else {
                address = addresses.get(0);
                geoLatitude = address.getLatitude();
                geoLongitude = address.getLongitude();
            }
        } else {
            Toast.makeText(this, "未輸入地址", Toast.LENGTH_SHORT).show();
        }
    }
    private void centerTitle() {
        ArrayList<View> textViews = new ArrayList<>();

        getWindow().getDecorView().findViewsWithText(textViews, getTitle(), View.FIND_VIEWS_WITH_TEXT);

        if(textViews.size() > 0) {
            AppCompatTextView appCompatTextView = null;
            if(textViews.size() == 1) {
                appCompatTextView = (AppCompatTextView) textViews.get(0);
            } else {
                for(View v : textViews) {
                    if(v.getParent() instanceof Toolbar) {
                        appCompatTextView = (AppCompatTextView) v;
                        break;
                    }
                }
            }

            if(appCompatTextView != null) {
                ViewGroup.LayoutParams params = appCompatTextView.getLayoutParams();
                params.width = ViewGroup.LayoutParams.MATCH_PARENT;
                appCompatTextView.setLayoutParams(params);
                appCompatTextView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            }
        }
    }
    public void sub(){
        storerate.setIsIndicator(false);
        ed1.setBackgroundColor(Color.WHITE);
        ed1.setFocusable(true);
        ed1.setFocusableInTouchMode(true);
        submit.setText("提交");
        submit.setTextColor(Color.rgb(247,115,59));
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float sr=storerate.getRating();
                if(sr!=0) {
                    json_write = new JSONObject();
                    try {
                        json_write.put("action", "Comment");
                        //json_write.put("Sid",b.getInt("sid"));
                        json_write.put("Evaluation", ed1.getText().toString());
                        json_write.put("Escore", storerate.getRating());
                        globalVariable.c.send(json_write);
                        String tmp = globalVariable.c.receive();
                        json_read = new JSONObject(tmp);
                        if (!json_read.getBoolean("check")) {//接收失敗原因
                            //String reason = json_read.getString("data");
                            //Toast.makeText(this, reason, Toast.LENGTH_SHORT).show();
                        } else {
                            Commented();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                else Toast.makeText(StoreAct.this, "星星評分不得為空", Toast.LENGTH_SHORT).show();

            }
        });
    }
    public void info(){
        try {
            json_write = new JSONObject();

            if(storecommit!=null) storecommit.removeAllViews();
            json_write.put("action", "Store");
            json_write.put("Id", sid);
            globalVariable.c.send(json_write);
            String tmp = globalVariable.c.receive();
            json_read = new JSONObject(tmp);
            JSONArray j1 = json_read.getJSONArray("Evaluation");
            JSONArray j2 = new JSONArray();
            row = new TableRow[j1.length()];
            for (int i = 0; i < j1.length(); i++) { //動態產生TableRow
                row[i] = new TableRow(this);
                row[i].setId(i);
                storecommit.addView(row[i]);
            }
            for (int i = 0; i < j1.length(); i++) { //拆解接收的JSON包並製作表格顯示
                j2 = j1.getJSONArray(i);
                TextView tw = new TextView(this);
                tw.setText(j2.get(0).toString());
                tw.setTextSize(TypedValue.COMPLEX_UNIT_SP, sp);
                tw.setTextColor(Color.BLACK);
                row[i].addView(tw);
                tw = new TextView(this);
                tw.setText(j2.get(1).toString());
                tw.setTextColor(Color.BLACK);
                row[i].addView(tw);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("Exception", "StoreError=" + e.toString());
        }
    }
    public void Commented(){
        ed1.setFocusable(false);
        info();
        //ed1.setFocusableInTouchMode(true);
        storerate.setIsIndicator(true);
        submit.setText("編輯");
        submit.setTextColor(Color.rgb(189,146,86));
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sub();
            }
        });
    }
}