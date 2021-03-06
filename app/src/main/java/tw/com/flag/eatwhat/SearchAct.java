package tw.com.flag.eatwhat;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.LocationManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.design.widget.TabLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileOutputStream;
import java.io.IOException;

public class SearchAct extends AppCompatActivity
        implements DialogInterface.OnClickListener{
    static Activity ActivityS;
    private JSONObject json_read, json_write;
    private GlobalVariable globalVariable;
    private TableRow[] row,row2;
    private int sp=14;
    private TableLayout tblayout, tblayout2;
    private Boolean isStore=true,isSort=false, sort=true,isSort2=false, sort2=true,isSort3=false, sort3=true;
    private boolean linkout=false;
    private int times=0;
    private Button ebtn;
    private TabLayout mTabLayout;
    private android.support.v7.widget.SearchView editText10; //搜尋
    private RadioButton radioButton10,radioButton9,radioButton8;

    private LocationManager status;
    Gps gps4;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityS=this;
        setContentView(R.layout.activity_search);

        globalVariable = (GlobalVariable) getApplicationContext().getApplicationContext();
        status = (LocationManager) (this.getSystemService(LOCATION_SERVICE));

        DisplayMetrics dm = new DisplayMetrics();   //取得螢幕寬度並設定ScrollView尺寸
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        if(dm.widthPixels<=480){
            sp=12;
        }

        gps4 = new Gps(this);

        mTabLayout = findViewById(R.id.mTabLayout);
        radioButton10 = findViewById(R.id.radioButton10);
        radioButton9 = findViewById(R.id.radioButton9);
        radioButton8 = findViewById(R.id.radioButton8);
        radioButton10.setEnabled(false);
        radioButton9.setEnabled(false);
        radioButton8.setEnabled(false);
        editText10 = findViewById(R.id.editText10);
        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch(tab.getPosition()){
                    case 0:
                        isStore=true;
                        NestedScrollView sc = findViewById(R.id.sc1);
                        sc.setVisibility(View.VISIBLE);
                        sc = findViewById(R.id.sc2);
                        sc.setVisibility(View.INVISIBLE);
                        radioButton10.setEnabled(false);
                        radioButton9.setEnabled(false);
                        radioButton8.setEnabled(false);
                        if(row != null){
                            radioButton9.setEnabled(true);
                            radioButton8.setEnabled(true);
                        }
                        editText10.setQuery("", false);
                        break;
                    case 1:
                        isStore=false;
                        NestedScrollView sc2 = findViewById(R.id.sc2);
                        sc2.setVisibility(View.VISIBLE);
                        sc2 = findViewById(R.id.sc1);
                        sc2.setVisibility(View.INVISIBLE);
                        radioButton10.setEnabled(false);
                        radioButton9.setEnabled(false);
                        radioButton8.setEnabled(false);
                        if(row2 != null){
                            radioButton10.setEnabled(true);
                        }
                        editText10.setQuery("", false);
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


        editText10.setOnQueryTextListener(new android.support.v7.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
//                radioButton10.setChecked(false);
//                radioButton9.setChecked(false);
//                radioButton8.setChecked(false);
                isSort=false;isSort2=false;isSort3=false;
                times++;
                if(query.indexOf("%")!=-1){
                    String query1="";
                    String[] tokens = query.split("%");
                    for(String token : tokens) {
                        query1+= token;
                    }
                    query = query1;
                    editText10.setQuery(query, true);
                }
                if(isStore){
                    try {
                        if(tblayout!=null) tblayout.removeAllViews();
                        json_write=new JSONObject(); //接收店家資料，並動態產生表格顯示
                        json_write.put("action", "show");
                        if (status.isProviderEnabled(LocationManager.GPS_PROVIDER) || status.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                            json_write.put("Longitude", gps4.getGPSLongitude());
                            json_write.put("Latitude", gps4.getGPSLatitude());
                        }else{
                            json_write.put("Longitude", 0.0);
                            json_write.put("Latitude", 0.0);
                        }
                        json_write.put("data",query);
                        globalVariable.c.send(json_write);
                        String tmp=globalVariable.c.receive();
                        if(tmp!=null) {
                            json_read = new JSONObject(tmp);
                            tblayout = (TableLayout) findViewById(R.id.tbLayout);
                            tblayout.setColumnShrinkable(0,true);
                            tblayout.setColumnStretchable(0, true);

                            if (!json_read.getBoolean("check")) {//當回傳為false
                                String reason;
                                reason = json_read.getString("data");
                                Toast.makeText(SearchAct.this,reason, Toast.LENGTH_SHORT).show();
                            }else{
                                JSONArray j1 = json_read.getJSONArray("data");
                                JSONArray j2;
                                row = new TableRow[j1.length()];
                                for (int i = 0; i < j1.length(); i++) { //動態產生TableRow
                                    row[i] = new TableRow(SearchAct.this);
                                    row[i].setBackgroundResource(R.drawable.ripple);
                                    row[i].setId(i);
                                    tblayout.addView(row[i]);
                                    TableLayout.LayoutParams params=(TableLayout.LayoutParams)row[i].getLayoutParams();
                                    params.setMargins(0,12,0,12);
                                }
                                for (int i = 0; i < j1.length(); i++) { //拆解接收的JSON包並製作表格顯示
                                    j2 = j1.getJSONArray(i);
                                    row[i].setTag(j2.get(0).toString());
                                    row[i].setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            TableRow t=(TableRow) v;
                                            gotostore(t.getTag().toString());
                                        }
                                    });
                                    TextView tw = new TextView(SearchAct.this);
                                    tw.setText(j2.get(1).toString());
                                    tw.setTextSize(TypedValue.COMPLEX_UNIT_SP, sp);
                                    float ratecount;
                                    ratecount = Float.valueOf(j2.get(2).toString());
                                    row[i].addView(tw);
                                    RatingBar rb=new RatingBar(SearchAct.this, null, android.R.attr.ratingBarStyleSmall);
                                    rb.setProgressTintList(getResources().getColorStateList(R.color.searchRatingbar));
                                    rb.setNumStars(5);
                                    rb.setRating(ratecount);
                                    //rb.setIsIndicator(true);
                                    row[i].addView(rb);
                                    TableRow.LayoutParams tlp=(TableRow.LayoutParams) rb.getLayoutParams();
                                    tlp.gravity=Gravity.CENTER_VERTICAL;
                                    tw = new TextView(SearchAct.this);
                                    float dist = Float.parseFloat(j2.get(3).toString())/1000;
                                    tw.setText(dist+"km");
                                    tw.setPadding(16,0,0,0);
                                    tw.setTextSize(TypedValue.COMPLEX_UNIT_SP, sp);
                                    row[i].addView(tw);
                                    Button btn=new Button(SearchAct.this, null, android.R.attr.buttonStyleSmall);
                                    btn.setText("考慮");
                                    btn.setTextColor(Color.WHITE);
                                    btn.setTypeface(null, Typeface.BOLD);
                                    btn.setBackgroundTintList(getResources().getColorStateList(R.color.pink));
                                    btn.setId(i);
                                    btn.setTag(j2.get(0).toString()+",-,");
                                    btn.setTextSize(TypedValue.COMPLEX_UNIT_SP, sp);
                                    btn.setSingleLine();
                                    btn.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Button b=(Button)v;
                                            try {
                                                FileOutputStream out = openFileOutput(globalVariable.account+"think.txt", MODE_APPEND);
                                                String s="5,"+b.getTag().toString()+((TextView)row[b.getId()].getChildAt(0)).getText().toString()+",-,-,";
                                                out.write(s.getBytes());
                                                out.close();

                                                b.setBackgroundTintList(getResources().getColorStateList(R.color.lightPink));
                                                b.setEnabled(false);
                                            }catch (IOException e){
                                                e.printStackTrace();
                                            }
                                        }
                                    });
                                    row[i].addView(btn);
                                    btn=new Button(SearchAct.this, null, android.R.attr.buttonStyleSmall);
                                    btn.setText("吃");
                                    btn.setTextColor(Color.WHITE);
                                    btn.setTypeface(null, Typeface.BOLD);
                                    btn.setBackgroundTintList(getResources().getColorStateList(R.color.waterBlue));
                                    btn.setId(i);
                                    btn.setTag(j2.get(0).toString()+",-,");
                                    btn.setTextSize(TypedValue.COMPLEX_UNIT_SP, sp);
                                    btn.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            ebtn=(Button)v;
                                            AlertDialog.Builder b=new AlertDialog.Builder(SearchAct.this);
                                            //串聯呼叫法
                                            b.setTitle("確認")
                                                    .setMessage("確定要吃這個嗎?")
                                                    .setPositiveButton("GO", SearchAct.this)       //若只是要顯示文字窗，沒有處理事件，第二個參數為null
                                                    .setNegativeButton("Cancel", null)
                                                    .show();
                                        }
                                    });
                                    row[i].addView(btn);
                                }
                            }
                        }else{
                            linkout=true;
                            //Toast.makeText(SearchAct.this, "連線逾時", Toast.LENGTH_LONG).show();
                            AlertDialog.Builder b=new AlertDialog.Builder(SearchAct.this);
                            //串聯呼叫法
                            b.setCancelable(false);
                            b.setTitle("警告")
                                    .setMessage("連線逾時，請重新連線")
                                    .setPositiveButton("連線", SearchAct.this)       //若只是要顯示文字窗，沒有處理事件，第二個參數為null
                                    .setNegativeButton("離開", SearchAct.this)
                                    .show();
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                        Log.e("Exception","StoreError="+e.toString());
                    }
                    if(row != null){
                        radioButton10.setEnabled(false);
                        radioButton9.setEnabled(true);
                        radioButton8.setEnabled(true);
                    }
                }else{
                    try {
                        if(tblayout2!=null) tblayout2.removeAllViews();
                        json_write=new JSONObject();                                //接收店家資料，並動態產生表格顯示
                        json_write.put("action", "show2");
                        if (status.isProviderEnabled(LocationManager.GPS_PROVIDER) || status.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                            json_write.put("Longitude", gps4.getGPSLongitude());
                            json_write.put("Latitude", gps4.getGPSLatitude());
                        }else{
                            json_write.put("Longitude", 0.0);
                            json_write.put("Latitude", 0.0);
                        }
                        json_write.put("data",query);
                        globalVariable.c.send(json_write);
                        String tmp=globalVariable.c.receive();
                        if(tmp!=null) {
                            json_read = new JSONObject(tmp);
                            tblayout2 = (TableLayout) findViewById(R.id.tb2Layout);
                            tblayout2.setColumnShrinkable(1,true);
                            tblayout2.setColumnStretchable(1, true);

                            if (!json_read.getBoolean("check")) {//當回傳為false
                                String reason;
                                reason = json_read.getString("data");
                                Toast.makeText(SearchAct.this,reason, Toast.LENGTH_SHORT).show();
                            }else{
                                JSONArray j1 = json_read.getJSONArray("data");
                                JSONArray j2;
                                row2 = new TableRow[j1.length()];
                                for (int i = 0; i < j1.length(); i++) { //動態產生TableRow
                                    row2[i] = new TableRow(SearchAct.this);
                                    row2[i].setBackgroundResource(R.drawable.ripple);
                                    row2[i].setId(i);
                                    tblayout2.addView(row2[i]);
                                    TableLayout.LayoutParams params=(TableLayout.LayoutParams)row2[i].getLayoutParams();
                                    params.setMargins(0,12,0,12);
                                }
                                for (int i = 0; i < j1.length(); i++) { //拆解接收的JSON包並製作表格顯示
                                    j2 = j1.getJSONArray(i);
                                    row2[i].setTag(j2.get(0).toString());
                                    row2[i].setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            TableRow t=(TableRow) v;
                                            gotostore(t.getTag().toString());
                                        }
                                    });


                                    TextView[] tw = new TextView[j2.length()];
                                    for(int j=0;j<j2.length()-2;j++){
                                        tw[j] = new TextView(SearchAct.this);
                                        tw[j].setText(j2.get(j+2).toString());
                                        tw[j].setTextSize(TypedValue.COMPLEX_UNIT_SP, sp);
                                        if(j==1){
                                            ScrollView sc=new ScrollView(SearchAct.this);
                                            sc.addView(tw[j]);
                                            sc.setPadding(8,0,8,0);
                                            final int ii = i;
                                            tw[j].setOnClickListener(new View.OnClickListener() {
                                                public void onClick(View v) {
                                                    TableRow t=(TableRow) row2[ii];
                                                    gotostore(t.getTag().toString());
//                                                    String TAG = "123";
//                                                    Log.v(TAG,"123");
                                                }
                                            });
                                            row2[i].addView(sc);
                                            TableRow.LayoutParams params=(TableRow.LayoutParams)sc.getLayoutParams();
                                            params.gravity=Gravity.CENTER;
                                        }else {
                                            row2[i].addView(tw[j]);
                                        }
                                    }
                                    Button btn=new Button(SearchAct.this, null, android.R.attr.buttonStyleSmall);
                                    btn.setText("考慮");
                                    btn.setTextColor(Color.WHITE);
                                    btn.setTypeface(null, Typeface.BOLD);
                                    btn.setBackgroundTintList(getResources().getColorStateList(R.color.pink));
                                    btn.setId(i);
                                    btn.setTag(j2.get(0).toString()+","+j2.get(1).toString()+",");
                                    btn.setTextSize(TypedValue.COMPLEX_UNIT_SP, sp);
                                    btn.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Button b=(Button)v;
                                            try {
                                                FileOutputStream out = openFileOutput(globalVariable.account+"think.txt", MODE_APPEND);
                                                String s="5,"+b.getTag().toString()+((TextView)row2[b.getId()].getChildAt(0)).getText().toString()+","+((TextView) ((ScrollView) row2[b.getId()].getChildAt(1)).getChildAt(0)).getText().toString()+","+((TextView)row2[b.getId()].getChildAt(2)).getText().toString()+",";
                                                out.write(s.getBytes());
                                                out.close();

                                                b.setBackgroundTintList(getResources().getColorStateList(R.color.lightPink));
                                                b.setEnabled(false);
                                            }catch (IOException e){
                                                e.printStackTrace();
                                            }
                                        }
                                    });
                                    row2[i].addView(btn);
                                    btn=new Button(SearchAct.this, null, android.R.attr.buttonStyleSmall);
                                    btn.setText("吃");
                                    btn.setBackgroundTintList(getResources().getColorStateList(R.color.waterBlue));
                                    btn.setTextColor(Color.WHITE);
                                    btn.setTypeface(null, Typeface.BOLD);
                                    btn.setId(i);
                                    btn.setTag(j2.get(0).toString()+","+j2.get(1).toString()+",");
                                    btn.setTextSize(TypedValue.COMPLEX_UNIT_SP, sp);
                                    btn.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            ebtn=(Button)v;
                                            AlertDialog.Builder b=new AlertDialog.Builder(SearchAct.this);
                                            //串聯呼叫法
                                            b.setTitle("確認")
                                                    .setMessage("確定要吃這個嗎?")
                                                    .setPositiveButton("GO", SearchAct.this)       //若只是要顯示文字窗，沒有處理事件，第二個參數為null
                                                    .setNegativeButton("Cancel", null)
                                                    .show();
                                        }
                                    });
                                    row2[i].addView(btn);
                                }
                            }
                        }else{
                            //Toast.makeText(SearchAct.this, "連線逾時", Toast.LENGTH_LONG).show();
                            linkout=true;
                            AlertDialog.Builder b=new AlertDialog.Builder(SearchAct.this);
                            //串聯呼叫法
                            b.setCancelable(false);
                            b.setTitle("警告")
                                    .setMessage("連線逾時，請重新連線")
                                    .setPositiveButton("連線", SearchAct.this)       //若只是要顯示文字窗，沒有處理事件，第二個參數為null
                                    .setNegativeButton("離開", SearchAct.this)
                                    .show();
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                        Log.e("Exception","MenuError="+e.toString());
                    }
                    if(row2 != null){
                        radioButton10.setEnabled(true);
                        radioButton9.setEnabled(false);
                        radioButton8.setEnabled(false);
                    }
                }
                return false;
            }
            @Override
            public boolean onQueryTextChange(String query) {
                return true;
            }
        });

        try {
            json_write=new JSONObject();
            json_write.put("action", "useLog");
            json_write.put("Fid", 5);
            globalVariable.c.send(json_write);
        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    public void onRadioButtonClicked(View v){
        int id= v.getId();
        switch (id){
            case R.id.radioButton10:
                tblayout2.removeAllViews();
                if(!isSort) {
                    isSort = true; sort=true;
                    Sort(row2, 2,1);
                }
                if (sort) {
                    sort = false;
                    for (int i = 0; i < row2.length; i++) {
                        tblayout2.addView(row2[i]);
                    }
                } else {
                    sort = true;
                    for (int i = row2.length - 1; i > -1; i--) {
                        tblayout2.addView(row2[i]);
                    }
                }
//                radioButton10.setChecked(true);
                break;
            case R.id.radioButton8:
                isSort3=false; sort3=false;
                tblayout.removeAllViews();
                if(!isSort2) {
                    isSort2 = true; sort2=true;
                    Sort(row, 1,2);
                }
                if (sort2) {
                    sort2 = false;
                    for (int i = 0; i < row.length; i++) {
                        tblayout.addView(row[i]);
                    }
                } else {
                    sort2 = true;
                    for (int i = row.length - 1; i > -1; i--) {
                        tblayout.addView(row[i]);
                    }
                }
//                radioButton8.setChecked(true);
                break;
            case R.id.radioButton9:
                isSort2=false; sort2=false;
                tblayout.removeAllViews();
                if(!isSort3) {
                    isSort3 = true; sort3=true;
                    Sort(row, 2,3);
                }
                if (sort3) {
                    sort3 = false;
                    for (int i = 0; i < row.length; i++) {
                        tblayout.addView(row[i]);
                    }
                } else {
                    sort3 = true;
                    for (int i = row.length - 1; i > -1; i--) {
                        tblayout.addView(row[i]);
                    }
                }
//                radioButton9.setChecked(true);
                break;
        }
    }
    public void Sort(TableRow[] tr, int in,int s) {
        float p1 = 0, p2 = 0;
        for (int i = tr.length-1; i > 0; --i){
            for (int j = 0; j < i; ++j) {
                if(s == 1){
                    p1 = Float.parseFloat(((TextView) tr[j].getChildAt(in)).getText().toString());
                    p2 = Float.parseFloat(((TextView) tr[j+1].getChildAt(in)).getText().toString());
                }else if(s == 2){
                    p1 = (float) ((RatingBar) tr[j].getChildAt(in)).getRating();
                    p2 = (float) ((RatingBar)  tr[j+1].getChildAt(in)).getRating();
                }else{
                    p1 = Float.parseFloat(((TextView) tr[j].getChildAt(in)).getText().toString().substring(0,((TextView) tr[j].getChildAt(in)).getText().toString().indexOf("km")));
                    p2 = Float.parseFloat(((TextView) tr[j+1].getChildAt(in)).getText().toString().substring(0,((TextView) tr[j+1].getChildAt(in)).getText().toString().indexOf("km")));
                }
                if (p1 > p2) {
                    Button t1=((Button) tr[j].getChildAt(3));
                    Button t2=((Button) tr[j+1].getChildAt(3));
                    int t=t1.getId();
                    t1.setId(t2.getId());
                    t2.setId(t);
                    t1=((Button) tr[j].getChildAt(4));
                    t2=((Button) tr[j+1].getChildAt(4));
                    t=t1.getId();
                    t1.setId(t2.getId());
                    t2.setId(t);

                    TableRow tmp = tr[j];
                    tr[j] = tr[j + 1];
                    tr[j + 1] = tmp;
                }
            }
        }
    }
    public void gotostore(String id){
        Bundle b = new Bundle();
        Intent i = new Intent(this, StoreAct.class);
        b.putBoolean("mode", true);
        b.putString("datanum", id);
        b.putInt("Activity", 4);
        i.putExtras(b);
        startActivity(i);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        gps4.delete();
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        gps4.update();
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        if(!linkout) {
            try {
                FileOutputStream out = openFileOutput(globalVariable.account+"eat.txt", MODE_APPEND);
                String s;
                if (isStore) {
                    s = ebtn.getTag().toString() + ((TextView) row[ebtn.getId()].getChildAt(0)).getText().toString() + ",-,-,";
                } else {
                    s = ebtn.getTag().toString() + ((TextView) row2[ebtn.getId()].getChildAt(0)).getText().toString() + "," + ((TextView) ((ScrollView) row2[ebtn.getId()].getChildAt(1)).getChildAt(0)).getText().toString() + "," + ((TextView) row2[ebtn.getId()].getChildAt(2)).getText().toString() + ",";
                    String[] ad = ebtn.getTag().toString().split(",");
                    json_write = new JSONObject();
                    json_write.put("action", "eat");
                    json_write.put("mid", Integer.parseInt(ad[1]));
                    globalVariable.c.send(json_write);
                    String tmp = globalVariable.c.receive();
                    if(tmp != null) {
                        json_read = new JSONObject(tmp);
                        if (!json_read.getBoolean("check")) {
                            String reason;
                            reason = json_read.getString("data");
                            Toast.makeText(SearchAct.this, reason, Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        linkout=true;
                        AlertDialog.Builder b=new AlertDialog.Builder(SearchAct.this);
                        //串聯呼叫法
                        b.setCancelable(false);
                        b.setTitle("警告")
                                .setMessage("連線逾時，請重新連線")
                                .setPositiveButton("連線", this)       //若只是要顯示文字窗，沒有處理事件，第二個參數為null
                                .setNegativeButton("離開", this)
                                .show();
                    }
                }
                out.write(s.getBytes());
                out.close();
                ebtn.setBackgroundTintList(getResources().getColorStateList(R.color.lightBlue));
                ebtn.setEnabled(false);

                json_write = new JSONObject();
                json_write.put("action", "eatLog");
                json_write.put("Fid", 5);
                globalVariable.c.send(json_write);
            }catch (Exception e){
                e.printStackTrace();
            }
        }else{
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
            this.finish();
        }
    }
}

