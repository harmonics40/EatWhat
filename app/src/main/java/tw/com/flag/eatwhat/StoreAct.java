package tw.com.flag.eatwhat;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.net.Uri;
import android.support.annotation.IntegerRes;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatTextView;
import android.text.Editable;
import android.text.TextWatcher;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.util.Log;

public class StoreAct extends AppCompatActivity
        implements DialogInterface.OnClickListener{
    static Activity ActivityS;
    private GlobalVariable globalVariable;
    private JSONObject json_read, json_write;
    private TextView storeaddr,storecell,storetime,textView12,textView13;
    private TableLayout storeLayout,storeLayout2;
    private TableRow[] row,row2;
    private int sp=14,sid,limittext = 100;
    private int[] mid;
    private Gps gps3;
    private Button btn;
    private String tmp;
    private boolean linkout=false;
    double geoLatitude, geoLongitude;
    private RatingBar rb;
    private LocationManager status;
    private ScrollView sc;
    private TableLayout storecommit;
    TableRow.LayoutParams textViewParam = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT,1.0f);
    private Button submit;
    RatingBar storerate;
    ScrollView sc2;
    Bundle b;

    private NestedScrollView showComment,showMenu,showInf;

    private TabLayout mTabLayout;
    private static final String TAG="LogDemo";
    private EditText ed1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store);
        ActivityS=this;

        getSupportActionBar().setElevation(0);
        centerTitle();
        gps3 = new Gps(this);
        showComment=findViewById(R.id.showComment);
        showInf=findViewById(R.id.showInf);
        showMenu=findViewById(R.id.showMenu);
        sc = findViewById(R.id.sc);
        ed1 = findViewById(R.id.ed1);
        textView12 = findViewById(R.id.textView12);
        String date = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault()).format(new Date());
        textView12.setText(date);
        textView13 = findViewById(R.id.textView13);
        submit = findViewById(R.id.submit);
        showComment.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {
                findViewById(R.id.sc).getParent().requestDisallowInterceptTouchEvent(false);
                return false;
            }
        });
        ed1.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event)
            {
                // Disallow the touch request for parent scroll on touch of child view
                sc.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });
        ed1.addTextChangedListener(new TextWatcher() {
            private CharSequence temp;
            private int selectionStart;
            private int selectionEnd;
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                temp = s;
            }
            @Override
            public void afterTextChanged(Editable s) {
                int text = limittext - s.length();
                textView13.setText(text+"/100");
                selectionStart = ed1.getSelectionStart();
                selectionEnd = ed1.getSelectionEnd();
                if (temp.length() > limittext || temp.toString().indexOf("%")!=-1) {
                    s.delete(selectionStart - 1, selectionEnd);
                    int tempSelection = selectionEnd;
                    ed1.setText(s);
                    ed1.setSelection(tempSelection);
                }
            }
        });
        mTabLayout = findViewById(R.id.mTabLayout);
        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
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

        status = (LocationManager) (this.getSystemService(LOCATION_SERVICE));
        DisplayMetrics dm = new DisplayMetrics();   //取得螢幕寬度並設定ScrollView尺寸
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        if(dm.widthPixels<=480){
            sp=10;
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
                if(tmp!=null) {
                    json_read = new JSONObject(tmp);
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
                    daytime();
                }else{
                    linkout=true;
                    AlertDialog.Builder b=new AlertDialog.Builder(this);
                    //串聯呼叫法
                    b.setCancelable(false);
                    b.setTitle("警告")
                            .setMessage("連線逾時，請重新連線")
                            .setPositiveButton("連線", this)       //若只是要顯示文字窗，沒有處理事件，第二個參數為null
                            .setNegativeButton("離開", this)
                            .show();
                }
            }
            if(!linkout) {
                if (!b.getBoolean("mode")) {//從隨機、提問進店家頁面
                    setTitle(b.getString("data"));//店名
                    storeaddr.setText("   " + b.getString("dataddr"));
                    storecell.setText("   " + b.getString("datacell"));
                    float starmum = Float.valueOf(b.getString("datastar"));
                    rb.setRating(starmum);
                    json_write.put("action", "Store");
                    json_write.put("Id", sid);
                    globalVariable.c.send(json_write);
                    tmp = globalVariable.c.receive();
                }
                if (tmp != null) {
                    json_read = new JSONObject(tmp);
                    storeaddr.setOnClickListener(new View.OnClickListener() {//點擊地址導向Google map 事件
                        @Override
                        public void onClick(View v) {
                            openMaptw(storeaddr.getText().toString());
                        }
                    });
                    storecell.setOnClickListener(new View.OnClickListener() {//點擊地址導向Google map 事件
                        @Override
                        public void onClick(View v) {
                            Intent it = new Intent(Intent.ACTION_VIEW, Uri.parse("tel:" + storecell.getText().toString()));
                            startActivity(it);
                        }
                    });
                    daytime();
                    storeLayout = (TableLayout) findViewById(R.id.storeLayout);
                    storeLayout.setColumnShrinkable(0, true);
                    storeLayout.setColumnStretchable(0, true);

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
                        tw.setText("  "+s + " 元");//價格
                        tw.setGravity(Gravity.CENTER);
                        tw.setTextColor(Color.rgb(110, 110, 110));
                        row[i].addView(tw);


                        mid[i] = Integer.parseInt(j2.get(2).toString());

                        btn = new Button(this, null, android.R.attr.buttonStyleSmall);
                        btn.setText("推薦");
                        btn.setBackgroundTintList(getResources().getColorStateList(R.color.pink));
                        btn.setTextColor(Color.WHITE);
                        btn.setTextSize(TypedValue.COMPLEX_UNIT_SP, sp);
                        final int ii = i;
                        btn.setOnClickListener(new View.OnClickListener() {//推薦按鍵事件
                            @Override
                            public void onClick(View v) {
                                final Button bt=(Button)v;
                                if (globalVariable.recmdtime < 2) {//推薦次數每日2次
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
                                                            if (!json_read.getBoolean("check")) {//接收失敗原因
                                                                String reason = json_read.getString("data");
                                                                Toast.makeText(StoreAct.this, reason, Toast.LENGTH_SHORT).show();
                                                            } else {
                                                                bt.setEnabled(false);
                                                                bt.setBackgroundTintList(getResources().getColorStateList(R.color.lightPink));
                                                                globalVariable.recmdtime++;
                                                                String reason = json_read.getString("data");
                                                                Toast.makeText(StoreAct.this, reason, Toast.LENGTH_SHORT).show();
                                                            }
                                                        } else {
                                                            AlertDialog.Builder b = new AlertDialog.Builder(StoreAct.this);
                                                            //串聯呼叫法
                                                            b.setCancelable(false);
                                                            b.setTitle("警告")
                                                                    .setMessage("連線逾時，請重新連線")
                                                                    .setPositiveButton("連線", StoreAct.this)       //若只是要顯示文字窗，沒有處理事件，第二個參數為null
                                                                    .setNegativeButton("離開", StoreAct.this)
                                                                    .show();
                                                        }
                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                            });
                                    b.setNegativeButton("Cancel", null);
                                    b.show();
                                } else {
                                    Toast.makeText(StoreAct.this, "本日推薦次數已用完", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                        row[i].addView(btn);
                    }
                } else {
                    AlertDialog.Builder b = new AlertDialog.Builder(this);
                    //串聯呼叫法
                    b.setCancelable(false);
                    b.setTitle("警告")
                            .setMessage("連線逾時，請重新連線")
                            .setPositiveButton("連線", StoreAct.this)       //若只是要顯示文字窗，沒有處理事件，第二個參數為null
                            .setNegativeButton("離開", StoreAct.this)
                            .show();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        //回饋意見=========================================================================


        globalVariable = (GlobalVariable) getApplicationContext().getApplicationContext();


        storecommit = (TableLayout) findViewById(R.id.storecommit);

        try {
            //json_read = new JSONObject(b.getString("data"));
            JSONArray j1 = json_read.getJSONArray("Evaluation");
            JSONArray j2 = new JSONArray();
            row = new TableRow[(j1.length()+1)*3];
            for (int i = 0; i < (j1.length()+1)*3; i++) { //動態產生TableRow
                row[i] = new TableRow(this);
                row[i].setId(i);
                storecommit.addView(row[i]);
            }
            for (int i = 0; i < j1.length(); i++) { //拆解接收的JSON包並製作表格顯示
                j2 = j1.getJSONArray(i);
                int z= i*3;
                TextView tw = new TextView(this);
                tw.setText(j2.get(0).toString());
                tw.setTextSize(TypedValue.COMPLEX_UNIT_SP, sp);
                tw.setTextColor(getResources().getColor(R.color.comment_user));
                tw.setTypeface(null, Typeface.BOLD);
                tw.setLayoutParams(textViewParam);
                tw.setPaddingRelative(10,0,10,10);
                tw.setTextSize(18);
                row[z].addView(tw);
                tw = new TextView(getApplicationContext());
                tw.setText(j2.get(2).toString());
                tw.setTextColor(Color.BLACK);
                tw.setLayoutParams(textViewParam);
                tw.setPaddingRelative(10,0,10,10);
                row[z+1].addView(tw);
                tw = new TextView(this);
                tw.setText(j2.get(1).toString());
                tw.setTextColor(Color.BLACK);
                tw.setLayoutParams(textViewParam);
                tw.setPaddingRelative(10,0,10,50);
                row[z+2].addView(tw);
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
                textView12.setText(j4.get(2).toString());
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
        if (status.isProviderEnabled(LocationManager.GPS_PROVIDER) ||status.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            getGPFromAddress(storeaddr);
            Uri uri = Uri.parse("http://maps.google.com/maps?f=d&saddr=" + String.valueOf(gps3.getGPSLatitude()) + "," + String.valueOf(gps3.getGPSLongitude()) + "&daddr=" + geoLatitude + "," + geoLongitude + "&hl=tw");
            Intent it = new Intent(Intent.ACTION_VIEW);
            it.setData(uri);
            if (it.resolveActivity(getPackageManager()) != null) {
                startActivity(it);
            }
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        gps3.delete();
    }
    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        gps3.update();
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
                        if(tmp!=null){
                            json_read = new JSONObject(tmp);
                            if (!json_read.getBoolean("check")) {//接收失敗原因
                                String reason = json_read.getString("data");
                                Toast.makeText(StoreAct.this, reason, Toast.LENGTH_SHORT).show();
                            } else {
                                Commented();
                            }
                        }else{
                            AlertDialog.Builder b = new AlertDialog.Builder(StoreAct.this);
                            //串聯呼叫法
                            b.setCancelable(false);
                            b.setTitle("警告")
                                    .setMessage("連線逾時，請重新連線")
                                    .setPositiveButton("連線", StoreAct.this)       //若只是要顯示文字窗，沒有處理事件，第二個參數為null
                                    .setNegativeButton("離開", StoreAct.this)
                                    .show();
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
            row = new TableRow[(j1.length()+1)*3];
            for (int i = 0; i < (j1.length()+1)*3; i++) { //動態產生TableRow
                row[i] = new TableRow(this);
                row[i].setId(i);
                storecommit.addView(row[i]);
            }
            for (int i = 0; i < j1.length(); i++) { //拆解接收的JSON包並製作表格顯示
                j2 = j1.getJSONArray(i);
                int z= i*3;
                TextView tw = new TextView(this);
                tw.setText(j2.get(0).toString());
                tw.setTextSize(TypedValue.COMPLEX_UNIT_SP, sp);
                tw.setTextColor(Color.BLACK);
                tw.setLayoutParams(textViewParam);
                tw.setPaddingRelative(10,0,10,10);
                tw.setTextColor(getResources().getColor(R.color.comment_user));
                tw.setTypeface(null, Typeface.BOLD);
                tw.setTextSize(18);
                row[z].addView(tw);
                tw = new TextView(getApplicationContext());
                tw.setText(j2.get(2).toString());
                tw.setTextColor(Color.BLACK);
                tw.setLayoutParams(textViewParam);
                tw.setPaddingRelative(10,0,10,10);
                row[z+1].addView(tw);
                tw = new TextView(this);
                tw.setText(j2.get(1).toString());
                tw.setTextColor(Color.BLACK);
                tw.setLayoutParams(textViewParam);
                tw.setPaddingRelative(10,0,10,50);
                tw.setMaxLines(3);
                row[z+2].addView(tw);
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
    @Override
    public void onClick(DialogInterface dialog, int which) {
        try {
            globalVariable.c.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(which==DialogInterface.BUTTON_POSITIVE) {
            Intent it = new android.content.Intent(this, MainActivity.class);
            startActivity(it);
        }
        if(!Main2Activity.ActivityM.isFinishing()) Main2Activity.ActivityM.finish();

        switch (b.getInt("Activity")){
            case 1:
                if(!randomSuggestRul.ActivityR.isFinishing()) randomSuggestRul.ActivityR.finish();
                if(!randomSuggestAct.ActivityA.isFinishing()) randomSuggestAct.ActivityA.finish();
                break;
            case 2:
                if(!questionSuggestRul.ActivityQ.isFinishing()) questionSuggestRul.ActivityQ.finish();
                if(!questionSuggestAct2.ActivityQ2.isFinishing()) questionSuggestAct2.ActivityQ2.finish();
                if(!questionSuggestAct.Activityqa.isFinishing()) questionSuggestAct.Activityqa.finish();
                break;
            case 3:
                if(!userSuggestAct.ActivityU.isFinishing()) userSuggestAct.ActivityU.finish();
                break;
            case 4:
                if(!SearchAct.ActivityS.isFinishing()) SearchAct.ActivityS.finish();
                break;
            case 5:
                if(!ContentSuggestAct.ActivityC.isFinishing()) ContentSuggestAct.ActivityC.finish();
                break;
            case 6:
                if(!recordAct.ActivityR.isFinishing()) recordAct.ActivityR.finish();
                break;
        }
        this.finish();
    }
    public void daytime(){
        try {
            storeLayout2 = (TableLayout) findViewById(R.id.tbLayout2);
            if(storeLayout2!=null)storeLayout2.removeAllViews();
            JSONArray j3 = json_read.getJSONArray("Time");
            if(j3 == null){
                row2 = new TableRow[1];
                row2[0] = new TableRow(this);
                storeLayout2.addView(row2[0]);
                TextView tw1 = new TextView(this);
                tw1.setText("暫無營業時間");
            }else {
                row2 = new TableRow[j3.length()];
                for (int i = 0; i < j3.length(); i++) { //動態產生TableRow
                    row2[i] = new TableRow(this);
                    row2[i].setPadding(4,4,4,4);
                    storeLayout2.addView(row2[i]);
                }
                JSONArray j4;
                int a=0,b=0,c=0,d=0,e=0,f=0,g=0;
                for (int i = 0; i < j3.length(); i++) { //拆解接收的JSON包並製作表格顯示
                    j4 = j3.getJSONArray(i);
                    TextView tw = new TextView(this);
                    if(j4.get(0).toString().equals("星期一")){
                        a++;
                    }else if(j4.get(0).toString().equals("星期二")){
                        b++;
                    }else if(j4.get(0).toString().equals("星期三")){
                        c++;
                    }else if(j4.get(0).toString().equals("星期四")){
                        d++;
                    }else if(j4.get(0).toString().equals("星期五")){
                        e++;
                    }else if(j4.get(0).toString().equals("星期六")){
                        f++;
                    }else if(j4.get(0).toString().equals("星期日")){
                        g++;
                    }
                    if(a==1||b==1||c==1||d==1||e==1||f==1||g==1){
                        tw.setText(j4.get(0).toString());
                        tw.setPadding(0,24,24,0);
                    }
                    else tw.setPadding(0,0,24,0);
                    tw.setTextSize(16);
                    tw.setTextColor(Color.rgb(110, 110, 110));
                    tw.setTextSize(16);
                    row2[i].addView(tw);
                    tw = new TextView(this);
                    tw.setText(j4.get(1).toString().substring(0,5));
                    tw.setTextColor(Color.rgb(110, 110, 110));
                    tw.setTextSize(16);
                    row2[i].addView(tw);
                    tw = new TextView(this);
                    tw.setText("~");
                    tw.setTextColor(Color.rgb(110, 110, 110));
                    tw.setTextSize(16);
                    row2[i].addView(tw);
                    tw = new TextView(this);
                    tw.setText(j4.get(2).toString().substring(0,5));
                    tw.setTextColor(Color.rgb(110, 110, 110));
                    tw.setTextSize(16);
                    row2[i].addView(tw);
                }
            }
        }catch (JSONException e) {
            e.printStackTrace();
        }
    }
}