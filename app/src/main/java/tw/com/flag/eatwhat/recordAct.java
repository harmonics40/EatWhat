package tw.com.flag.eatwhat;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.AttrRes;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;
import org.xml.sax.Attributes;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class recordAct extends AppCompatActivity
    implements DialogInterface.OnClickListener{
    private GlobalVariable globalVariable;
    private JSONObject json_read, json_write;
    private TableLayout tblayout, tblayout2;
    ArrayList<TableRow> row;
    private int sp=14;
    private int count, count2;
    private boolean change=true, change2=false;
    private boolean isDel=false;
    private Button ebtn,btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);

        globalVariable = (GlobalVariable) getApplicationContext().getApplicationContext();
        DisplayMetrics dm = new DisplayMetrics();   //取得螢幕寬度並設定ScrollView尺寸
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        if(dm.widthPixels<=480){
            sp=12;
        }
        tblayout2 = (TableLayout) findViewById(R.id.tb2Layout);

        try {
            FileInputStream in=openFileInput("think.txt");
            int idx;
            byte[] buffer = new byte[1024];
            String s="";
            idx=in.read(buffer);
            while(idx!=-1){
                s=s+new String(buffer, 0, idx);
                idx=in.read(buffer);
            }
            in.close();

            if(s!="") {
                tblayout = (TableLayout) findViewById(R.id.tbLayout);
                tblayout.setColumnShrinkable(0,true);
                tblayout.setColumnShrinkable(1,true);
                tblayout.setColumnStretchable(0, true);
                tblayout.setColumnStretchable(1, true);
                tblayout.setColumnStretchable(3, true);

                count=0;
                row=new ArrayList<>();
                while (s.contains(",")){
                    row.add(new TableRow(this));
                    row.get(count).setBackgroundResource(R.drawable.ripple);
                    String sid,mid;
                    idx=s.indexOf(",");
                    sid = s.substring(0, idx);
                    s=s.substring(idx+1);
                    idx=s.indexOf(",");
                    mid = s.substring(0, idx);
                    s=s.substring(idx+1);
                    TextView[] tv=new TextView[3];
                    for(int i=0;i<3;i++){
                        idx=s.indexOf(",");
                        tv[i]=new TextView(this);
                        tv[i].setText(s.substring(0, idx));
                        if(i==0){
                            tv[i].setTag(sid);
                        }
                        if(i==1){
                            tv[i].setTag(mid);
                        }
                        s=s.substring(idx+1);
                        row.get(count).addView(tv[i]);
                    }
                    Button btn=new Button(this, null, android.R.attr.buttonStyleSmall);
                    btn.setText("吃");
                    btn.setId(count);
                    btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ebtn=(Button)v;
                            AlertDialog.Builder b=new AlertDialog.Builder(recordAct.this);
                            //串聯呼叫法
                            b.setTitle("確認")
                                    .setMessage("確定要吃這個嗎?")
                                    .setPositiveButton("GO", recordAct.this)       //若只是要顯示文字窗，沒有處理事件，第二個參數為null
                                    .setNegativeButton("Cancel", null)
                                    .show();
                        }
                    });
                    btn.setTextSize(TypedValue.COMPLEX_UNIT_SP, sp);
                    row.get(count).addView(btn);
                    row.get(count).setTag(count);
                    row.get(count).setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            if(!isDel) {
                                isDel = true;
                                Button btn = (Button) findViewById(R.id.clearBtn);
                                btn.setVisibility(View.VISIBLE);

                                CheckBox cb;
                                for (int i = 0; i < row.size(); i++) {
                                    cb = new CheckBox(recordAct.this);
                                    row.get(i).addView(cb);
                                }
                                return false;
                            }
                            return true;
                        }
                    });
                    row.get(count).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            TableRow tr=(TableRow)v;
                            if(isDel){
                                for(int i=0;i<row.size();i++){
                                    if(row.get(i).getTag()==tr.getTag()){
                                        if(((CheckBox)row.get(i).getChildAt(4)).isChecked()){
                                            ((CheckBox)row.get(i).getChildAt(4)).setChecked(false);
                                        }else{
                                            ((CheckBox)row.get(i).getChildAt(4)).setChecked(true);
                                        }
                                        break;
                                    }
                                }
                            }else{
                                gotostore(tr.getChildAt(0).getTag().toString());
                            }
                        }
                    });
                    tblayout.addView(row.get(count));
                    count++;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void switchClick(View v){
        Button b=(Button)v;
        switch (b.getId()){
            case R.id.button22:
                b.setEnabled(false);
                b=findViewById(R.id.button30);
                b.setEnabled(true);
                ScrollView sc=findViewById(R.id.sc1);
                sc.setVisibility(View.VISIBLE);
                sc=findViewById(R.id.sc2);
                sc.setVisibility(View.INVISIBLE);
                break;
            case R.id.button30:
                b.setEnabled(false);
                b=findViewById(R.id.button22);
                b.setEnabled(true);
                ScrollView sc2=findViewById(R.id.sc2);
                sc2.setVisibility(View.VISIBLE);
                sc2=findViewById(R.id.sc1);
                sc2.setVisibility(View.INVISIBLE);

                if(change){
                    change=false;
                    if(tblayout2!=null) tblayout2.removeAllViews();
                    try {
                        FileInputStream in = openFileInput("eat.txt");
                        byte[] buffer = new byte[1024];
                        String s = "";
                        int idx = in.read(buffer);
                        while (idx != -1) {
                            s = s + new String(buffer, 0, idx);
                            idx = in.read(buffer);
                        }
                        in.close();

                        if(s!="") {
                            tblayout2.setColumnShrinkable(0,true);
                            tblayout2.setColumnShrinkable(1,true);
                            tblayout2.setColumnStretchable(0, true);
                            tblayout2.setColumnStretchable(1, true);
                            tblayout2.setColumnStretchable(3, true);

                            ArrayList<TableRow> row2=new ArrayList<>();
                            count2=0;
                            while (s.contains(",")){
                                row2.add(new TableRow(this));
                                final String sid,mid;
                                idx=s.indexOf(",");
                                sid = s.substring(0, idx);
                                s=s.substring(idx+1);
                                idx=s.indexOf(",");
                                mid = s.substring(0, idx);
                                s=s.substring(idx+1);
                                TextView[] tv=new TextView[3];
                                for(int i=0;i<3;i++){
                                    idx=s.indexOf(",");
                                    tv[i]=new TextView(this);
                                    tv[i].setText(s.substring(0, idx));
                                    if(i==0){
                                        tv[i].setTag(sid);
                                    }
                                    s=s.substring(idx+1);
                                    row2.get(count2).addView(tv[i]);
                                }
                                btn=new Button(this, null, android.R.attr.buttonStyleSmall);
                                btn.setText("推薦");
                                btn.setId(count2);
                                btn.setTag(mid);
                                btn.setTextSize(TypedValue.COMPLEX_UNIT_SP, sp);
                                if(tv[1].getText().toString().equals("-")){
                                    btn.setEnabled(false);
                                }else{
                                    btn.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            if( globalVariable.recmdtime <2) {
                                                final AlertDialog.Builder b = new AlertDialog.Builder(recordAct.this);
                                                b.setTitle("確認")
                                                        .setMessage("確定要推薦這道菜嗎?")
                                                        .setPositiveButton("GO", new DialogInterface.OnClickListener() {
                                                            public void onClick(DialogInterface dialog, int id) {
                                                                json_write = new JSONObject();
                                                                try {
                                                                    json_write.put("action", "Recommend");
                                                                    json_write.put("Mid", btn.getTag());
                                                                    globalVariable.c.send(json_write);
                                                                    String tmp = globalVariable.c.receive();
                                                                    if (tmp != null) {
                                                                        json_read = new JSONObject(tmp);
                                                                        String reason = json_read.getString("data");
                                                                        if (!json_read.getBoolean("check")) {//接收失敗原因
                                                                        } else {//成功並關閉
                                                                            btn.setEnabled(false);
                                                                            globalVariable.recmdtime++;
                                                                        }
                                                                        Toast.makeText(recordAct.this, reason, Toast.LENGTH_SHORT).show();
                                                                    }
                                                                } catch (Exception e) {
                                                                    e.printStackTrace();
                                                                }
                                                            }
                                                        });
                                                b.setNegativeButton("Cancel", null);
                                                b.show();
                                            }else{
                                                Toast.makeText(recordAct.this, "本日推薦次數已用完", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }
                                row2.get(count2).addView(btn);
                                row2.get(count2).setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        TableRow tr=(TableRow)v;
                                        gotostore(tr.getChildAt(0).getTag().toString());
                                    }
                                });
                                tblayout2.addView(row2.get(count2));
                                count2++;
                            }
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
                break;
        }
    }
    public void clearClick(View v){
        AlertDialog.Builder b=new AlertDialog.Builder(this);
        //串聯呼叫法
        b.setTitle("刪除確認")
                .setMessage("確定要刪除嗎?")
                .setPositiveButton("GO", this)       //若只是要顯示文字窗，沒有處理事件，第二個參數為null
                .setNegativeButton("Cancel", null)
                .show();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            if(change2){
                FileOutputStream out = openFileOutput("think.txt", MODE_PRIVATE);
                String s="";
                for(int i=0;i<row.size();i++){
                    s+=row.get(i).getChildAt(0).getTag().toString()+","+row.get(i).getChildAt(1).getTag().toString()+","+((TextView)row.get(i).getChildAt(0)).getText().toString()+","+((TextView)row.get(i).getChildAt(1)).getText().toString()+","+((TextView)row.get(i).getChildAt(2)).getText().toString()+",";
                }
                out.write(s.getBytes());
                out.close();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if(isDel) {
                isDel = false;
                Button btn=(Button)findViewById(R.id.clearBtn);
                btn.setVisibility(View.INVISIBLE);

                for (int i = 0; i < row.size(); i++) {
                    row.get(i).removeViewAt(4);
                }
                return true;
            }
        }
        return super.onKeyDown(keyCode, event); //需在最後面
    }
    public void gotostore(String id){
        Bundle b = new Bundle();
        Intent i = new Intent(this, StoreAct.class);
        b.putBoolean("mode", true);
        b.putString("datanum", id);
        i.putExtras(b);
        startActivity(i);
    }
    public boolean onCreateOptionMenu(Menu menu){
        super.onCreateOptionsMenu(menu);
        menu.add(0,0,menu.NONE,"刪除");

        return true;
    }
    public void gotoRandomSuggestAct(View v){
        android.content.Intent it = new android.content.Intent(this,randomSuggestAct.class);
        startActivity(it);
    }
    public void gotoQuestionSuggestAct(View v){
        android.content.Intent it = new android.content.Intent(this,questionSuggestAct.class);
        startActivity(it);
    }
    public void gotoRecordAct(View v){
        android.content.Intent it = new android.content.Intent(this,recordAct.class);
        startActivity(it);
    }
    public void gotoSearchAct(View v){
        android.content.Intent it = new android.content.Intent(this,SearchAct.class);
        startActivity(it);
    }
    public void gotoMain2Activity(View v){
        android.content.Intent it = new android.content.Intent(this,Main2Activity.class);
        startActivity(it);
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        if(isDel) {
            for (int i = 0; i < row.size(); i++) {
                if (((CheckBox) row.get(i).getChildAt(4)).isChecked()) {
                    tblayout.removeView(row.get(i));
                    row.remove(i);
                    i -= 1;
                    change2 = true;
                }
            }
            Button btn = (Button) findViewById(R.id.clearBtn);
            btn.setVisibility(View.INVISIBLE);

            for (int i = 0; i < row.size(); i++) {
                row.get(i).removeViewAt(4);
            }
            isDel = false;
        }else{
            try {
                int i;
                for(i=0;i<row.size();i++){
                    if(((Button)row.get(i).getChildAt(3)).getId()==ebtn.getId()){
                        FileOutputStream out = openFileOutput("eat.txt", MODE_APPEND);
                        String s=row.get(i).getChildAt(0).getTag().toString()+","+row.get(i).getChildAt(1).getTag().toString()+","+((TextView)row.get(i).getChildAt(0)).getText().toString()+","+((TextView)row.get(i).getChildAt(1)).getText().toString()+","+((TextView)row.get(i).getChildAt(2)).getText().toString()+",";
                        out.write(s.getBytes());
                        out.close();

                        tblayout.removeView(row.get(i));
                        row.remove(i);
                        change=true; change2=true;
                        break;
                    }
                }
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }
}
