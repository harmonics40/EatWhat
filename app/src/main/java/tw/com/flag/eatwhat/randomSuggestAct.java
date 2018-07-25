package tw.com.flag.eatwhat;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;


public class randomSuggestAct extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener  {
    static Activity ActivityA;
    private GlobalVariable globalVariable;
    private JSONObject json_read, json_write;
   // private Spinner dist, disttime;
    private CheckBox chk;
    private int[] chk_id = {R.id.chk1, R.id.chk2, R.id.chk3, R.id.chk4, R.id.chk5, R.id.chk6, R.id.chk7, R.id.chk8};
    //private String[] eatime;int123
    private int[] eatime={1,2,3};//主食、早餐、點心
    private double[] limit = {100000, 300, 1000, 3000};
    TextView tv;
    Gps gps1;
    RadioGroup radioGroup1;
    RadioGroup radioGroup2;
    private DrawerLayout mDrawerlayout;
    private ActionBarDrawerToggle mToggle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_random_suggest);

        ActivityA=this;
        globalVariable = (GlobalVariable) getApplicationContext().getApplicationContext();
        gps1 = new Gps(this);
        tv = (TextView) findViewById(R.id.textView);

        mDrawerlayout = (DrawerLayout)findViewById(R.id.drawer);
        mToggle = new ActionBarDrawerToggle(this, mDrawerlayout, R.string.open, R.string.close);
        mDrawerlayout.addDrawerListener(mToggle);
        mToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        NavigationView navigationView=(NavigationView)findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        radioGroup1 = findViewById(R.id.radioGroup1);
        radioGroup2 = findViewById(R.id.radioGroup2);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        gps1.delete();
    }
    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        gps1.update();
    }

    public void gotoResult(android.view.View v){
        try {

            if(gps1.isGetGPSService()) {
                //int index = disttime.getSelectedItemPosition();
                int index=0;
                for(int i = 0 ;i < radioGroup1.getChildCount();i++) {
                    RadioButton rb = (RadioButton) radioGroup1.getChildAt(i);
                    if (rb.isChecked()) {
                        index = i;
                        break;
                    }
                }
                int time = eatime[index];

                //int index2 = dist.getSelectedItemPosition();
                int index2=0;
                for(int i = 0 ;i < radioGroup2.getChildCount();i++) {
                    RadioButton rb = (RadioButton) radioGroup2.getChildAt(i);
                    if (rb.isChecked()) {
                        index2 = i;
                        break;
                    }
                }
                double distlimit = limit[index2];
                json_write = new JSONObject();
                json_write.put("action", "Random");
                json_write.put("Longitude", gps1.getGPSLongitude());//經度
                json_write.put("Latitude", gps1.getGPSLatitude());//緯度
                json_write.put("Eatype", time);//主餐1早餐2點心3
                String[] dont1 = new String[10];
                int count =0;
                for (int id : chk_id) {
                    chk = (CheckBox) findViewById(id);
                    if (chk.isChecked()) {
                        dont1[count]= chk.getText().toString().trim();//不要吃的口味
                        count++;
                    }
                }
                String[] dont2 = new String[count];
                for(int i = 0;i < count ; i++){
                    dont2[i] = dont1[i];
                }
                JSONArray j2= new JSONArray(dont2);
                json_write.put("Dontwant", j2);
                json_write.put("Distlimit", distlimit);//距離限制(0為不限距離，分有0, 2, 4, 6)
                globalVariable.c.send(json_write);
                tv.setText("緯度 :" + gps1.getGPSLatitude() + "  , 經度 :  " + gps1.getGPSLongitude());
                String tmp = globalVariable.c.receive();
                json_read = new JSONObject(tmp);
                if(tmp!=null) {
                    if (!json_read.getBoolean("check")) {//接收失敗原因
                        String reason = json_read.getString("data");
                        Toast.makeText(this, reason, Toast.LENGTH_SHORT).show();
                    }else{//成功並關閉
                        Bundle b = new Bundle();
                        Intent i = new Intent(this, randomSuggestRul.class);
                        b.putString("data", tmp);
                        b.putInt("check",1);
                        b.putString("Latitude", String.valueOf(gps1.getGPSLatitude()));
                        b.putString("Longitude", String.valueOf(gps1.getGPSLongitude()));
                        i.putExtras(b);
                        startActivity(i);
                        //this.finish();
                    }
                }else{
                    Toast.makeText(this, "連線逾時", Toast.LENGTH_LONG).show();
                }
            }else{
                Toast.makeText(this,"位址尚未取得",Toast.LENGTH_SHORT).show();
            }
            //tv.setText("經度 : " + gps1.getGPSLatitude() + "  , 緯度 : " + gps1.getGPSLongitude());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void onBackPressed() { //案返回健
        if (mDrawerlayout.isDrawerOpen(findViewById(R.id.nav_view))) //側選單開著
            mDrawerlayout.closeDrawers(); //關抽屜
        else
            super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(mToggle.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        Intent it;
        switch(id){
            case R.id.home:
                it = new android.content.Intent(this, Main2Activity.class);
                startActivity(it);
                break;
            case R.id.setting:
                it = new android.content.Intent(this, settingAct.class);
                startActivity(it);
                break;
            case R.id.comment:
                it = new android.content.Intent(this, commentAct.class);
                startActivity(it);
                break;
        }
        return false;
    }
}
