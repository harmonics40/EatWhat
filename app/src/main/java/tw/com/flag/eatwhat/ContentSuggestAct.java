package tw.com.flag.eatwhat;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileOutputStream;
import java.io.IOException;

public class ContentSuggestAct extends AppCompatActivity implements DialogInterface.OnClickListener{
    static Activity ActivityC;
    static Context Acontext;
    private JSONObject json_read, json_write;
    private JSONArray j1;
    private GlobalVariable globalVariable;
    private int sp = 14 ,num,idx,tal;
    private RecyclerView recyclerView;
    private RecyclerViewAdapter recyclerAdapter;
    private LinearLayoutManager mLayoutManager;
    private int dataTimes = 1;

    private static final String TAG="LogDemo";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content_suggest);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        Acontext = this.getApplicationContext();
        ActivityC=this;
        globalVariable = (GlobalVariable) getApplicationContext().getApplicationContext();
        num=1;
        idx=0;

        mLayoutManager = new LinearLayoutManager(this);

        try {
            json_write = new JSONObject(); //接收店家資料，並動態產生表格顯示
            json_write.put("action", "Content");
            json_write.put("idx", "0");
            globalVariable.c.send(json_write);
            String tmp = globalVariable.c.receive();
            if (tmp != null) {
                json_read = new JSONObject(tmp);
                if (!json_read.getBoolean("check")) {//當回傳為false
                    String reason;
                    reason = json_read.getString("data");
                    Toast.makeText(ContentSuggestAct.this,reason, Toast.LENGTH_SHORT).show();
                }else{
                    j1 = json_read.getJSONArray("data");
                    if(tal%5==0){
                        tal=j1.length()/5;
                    }else {
                        tal=(j1.length()/5)+1;
                    }
                    info();
                }
            } else {
                AlertDialog.Builder b=new AlertDialog.Builder(this);
                //串聯呼叫法
                b.setCancelable(false);
                b.setTitle("警告")
                        .setMessage("連線逾時，請重新連線")
                        .setPositiveButton("連線", this)       //若只是要顯示文字窗，沒有處理事件，第二個參數為null
                        .setNegativeButton("離開", this)
                        .show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("Exception", "ContentError=" + e.toString());
        }




        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                int visibleItemCount = mLayoutManager.findLastVisibleItemPosition();
                if( visibleItemCount == 50*dataTimes-1 ){
                    addData();
                    dataTimes++;
                }
            }
        });

        try {
            json_write=new JSONObject();
            json_write.put("action", "useLog");
            json_write.put("Fid", 4);
            globalVariable.c.send(json_write);
        }catch (JSONException e){
            e.printStackTrace();
        }
    }
    public void info(){
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerAdapter = new RecyclerViewAdapter(j1);
        recyclerView.setAdapter(recyclerAdapter);
    }

    public void addData() {
        if(50*dataTimes<globalVariable.cnum) {
                idx++;
                try {
                    json_write = new JSONObject(); //接收店家資料，並動態產生表格顯示
                    json_write.put("action", "Content");
                    json_write.put("idx", idx * 50);
                    globalVariable.c.send(json_write);
                    String tmp = globalVariable.c.receive();
                    if (tmp != null) {
                        json_read = new JSONObject(tmp);
                        if (!json_read.getBoolean("check")) {//當回傳為false
                            String reason;
                            reason = json_read.getString("data");
                            Toast.makeText(ContentSuggestAct.this, reason, Toast.LENGTH_SHORT).show();
                        } else {
                            JSONArray j3 = json_read.getJSONArray("data");
                            JSONArray j4;
                            for (int i = 0; i < j3.length(); i++) { //合併json封包
                                j4 = j3.getJSONArray(i);
                                int index = dataTimes*50+i;
                                recyclerAdapter.addItem(j4,index);
                            }
                            if(tal%5==0){
                                tal=j1.length()/5;
                            }else {
                                tal=(j1.length()/5)+1;
                            }
                        }
                    } else {
                        AlertDialog.Builder b=new AlertDialog.Builder(this);
                        //串聯呼叫法
                        b.setCancelable(false);
                        b.setTitle("警告")
                                .setMessage("連線逾時，請重新連線")
                                .setPositiveButton("連線", this)       //若只是要顯示文字窗，沒有處理事件，第二個參數為null
                                .setNegativeButton("離開", this)
                                .show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("Exception", "ContentError=" + e.toString());
                }
        }else{
            Toast.makeText(ContentSuggestAct.this, "已無資料", Toast.LENGTH_LONG).show();
        }
    }



    public boolean onKeyDown(int keyCode, KeyEvent event)//按返回頁面關閉
    {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            this.finish();
        }
        return super.onKeyDown(keyCode, event);
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
        this.finish();
    }
}
