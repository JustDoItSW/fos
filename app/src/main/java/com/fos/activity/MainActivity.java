package com.fos.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;

import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.fos.R;
import com.fos.entity.Flower;
import com.fos.service.Client_phone;
import com.fos.service.MainService;
import com.fos.util.ActivityCollector;
import com.fos.util.GuideView;
import com.fos.util.MyFragmentPagerAdapter;
import com.fos.util.MyViewPager;

public class MainActivity extends AppCompatActivity {



    public static SharedPreferences.Editor editor;
    public static Flower flower;
    //Preferece机制操作的文件名
    public static final String PREFERENCE_NAME = "SaveContent";
    //Preferece机制的操作模式
    public static int MODE = MODE_PRIVATE;
    public static LinearLayout left_linearLayout,menu_tab;
    private SharedPreferences  sharedPreferences;
    private MyViewPager myViewPager;
    private ImageView left_menu,text_control,text_data,text_flower;
    private Switch loginControl;
    private EditText ip,port;
    private DrawerLayout dl;
    private RelativeLayout main_relativeLayout;
    private MyFragmentPagerAdapter myFragmentPagerAdapter;
    private Button btn_selectFlower;
    private Intent intent;
    private MainService infomationService;
    private ServiceConnection serviceConnection;
    public static Handler  handler;
    private Thread queryThread;
    public static Client_phone Client_phone;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        allScreen();

        ActivityCollector.finishAll();
        ActivityCollector.addActivity(this);

        initFlower();
        init();
        setupViewPager();//初始化viewpager


    }

    private void allScreen(){
        if(Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            //设置让应用主题内容占据状态栏和导航栏
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|View.SYSTEM_UI_FLAG_HIDE_NAVIGATION|View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);
            //设置状态栏和导航栏颜色为透明
            getWindow().setStatusBarColor(Color.TRANSPARENT);
            getWindow().setNavigationBarColor(Color.TRANSPARENT);
        }
    }
    private void initFlower(){
        sharedPreferences = getSharedPreferences(PREFERENCE_NAME,MODE);
        editor = sharedPreferences.edit();
        if((sharedPreferences.getString("flowerName","")).equals(""))
            setGuideView();
        flower = new Flower();
        flower.setFlowerName(sharedPreferences.getString("flowerName","未选择"));
        flower.setFlowerLux(sharedPreferences.getString("light","500"));
        flower.setFlowerSoilHum(sharedPreferences.getString("hum","50"));
        flower.setFlowerTemp(sharedPreferences.getString("temp","25"));
        flower.setFlowerImage(sharedPreferences.getString("image",""));
    }
    private void init(){

        intent = new Intent(MainActivity.this, MainService.class);

        dl = (DrawerLayout)findViewById(R.id.dl);
        main_relativeLayout =  (RelativeLayout)findViewById(R.id.main_relativeLayout);
        left_linearLayout  =(LinearLayout)findViewById(R.id.left_relativeLayout);
        menu_tab=(LinearLayout)findViewById(R.id.menu_tab);
        left_menu =  (ImageView)findViewById(R.id.left_menu);
        text_control =  (ImageView)findViewById(R.id.text_control);
        text_data =  (ImageView)findViewById(R.id.text_data);
        text_flower =  (ImageView)findViewById(R.id.text_flower);
        myViewPager = (MyViewPager)findViewById(R.id.vp);
        btn_selectFlower  = (Button)findViewById(R.id.btn_selectFlower);
        loginControl = (Switch)findViewById(R.id.loginControl);
        ip =  (EditText)findViewById(R.id.ip);
        port =  (EditText)findViewById(R.id.port);

        ip.setText("47.106.161.42");
        port.setText("8000");

        menu_tab.setOnClickListener(onClickListener);
        loginControl.setOnCheckedChangeListener(checkedChangeListener);
        text_control.setSelected(true);
        serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                infomationService = ((MainService.LocalBinder)service).getService();
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                infomationService = null;
            }
        };
        left_menu.setOnClickListener(onClickListener);
        text_control.setOnClickListener(onClickListener);
        text_data.setOnClickListener(onClickListener);
        text_flower.setOnClickListener(onClickListener);
        btn_selectFlower.setOnClickListener(onClickListener);
        dl.setDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                WindowManager manager = (WindowManager)getSystemService(Context.WINDOW_SERVICE);
                Display display  = manager.getDefaultDisplay();
                main_relativeLayout.layout(left_linearLayout.getRight(),0,left_linearLayout.getRight()+display.getWidth(),display.getHeight());
            }

            @Override
            public void onDrawerOpened(View drawerView) {

            }

            @Override
            public void onDrawerClosed(View drawerView) {

            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });
        startService(intent);//开启服务
        bindService(intent,serviceConnection, Context.BIND_AUTO_CREATE);//绑定服务

    }

    /**
     *  获得当前的所需要的温湿度信息（每隔60s）
     */
    CompoundButton.OnCheckedChangeListener  checkedChangeListener =  new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if(isChecked) {
                if (MainActivity.Client_phone == null) {
                    MainActivity.Client_phone = new Client_phone(ip.getText().toString(), Integer.parseInt(port.getText().toString()));
                    queryThread = new Thread(){
                        @Override
                        public void run() {
                            super.run();
                            try {
                                while (true){
                                    sleep(10000);
                                    Log.e("info","开始查询");
                                    if (MainActivity.Client_phone != null) {
                                        MainActivity.Client_phone.clientSendMessage("i");
                                    }
                                    sleep(50000);
                                }
                            }catch(Exception e){
                                e.printStackTrace();
                            }
                        }
                    };
                    queryThread.start();
                }
            }
            else {
                if (MainActivity.Client_phone != null) {
                    if(queryThread!=null) {
                        queryThread.interrupt();
                        queryThread = null;
                    }
                    MainActivity.Client_phone.close();
                    MainActivity.Client_phone = null;
                }
            }
        }

    };
    View.OnClickListener onClickListener  = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.text_control:
                    setAllSelected();
                    text_control.setSelected(true);
                    myViewPager.setCurrentItem(0);
                    break;
                case R.id.text_data:
                    setAllSelected();
                    text_data.setSelected(true);
                    myViewPager.setCurrentItem(1);
                    MainActivity.menu_tab.setVisibility(View.VISIBLE);
                    break;
                case R.id.text_flower:
                    setAllSelected();
                    text_flower.setSelected(true);
                    myViewPager.setCurrentItem(2);
                    MainActivity.menu_tab.setVisibility(View.VISIBLE);
                    break;
                case R.id.left_menu:
                    if(!dl.isDrawerOpen(left_linearLayout)){
                        dl.openDrawer(left_linearLayout);
                    }
                    break;
                case R.id.btn_selectFlower:
                    Intent intent  = new Intent(MainActivity.this,SelectFlower.class);
                    startActivity(intent);
                default:
                        break;
            }
        }
    };
    ViewPager.OnPageChangeListener onPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}
        @Override
        public void onPageSelected(int position) {
            switch (position){
                case 0:
                    setAllSelected();
                    text_control.setSelected(true);
                    break;
                case 1:
                    setAllSelected();
                    text_data.setSelected(true);
                    MainActivity.menu_tab.setVisibility(View.VISIBLE);
                    break;
                case 2:
                    setAllSelected();
                    text_flower.setSelected(true);
                    MainActivity.menu_tab.setVisibility(View.VISIBLE);
                    break;
                    default:
                        break;

            }
        }
        @Override
        public void onPageScrollStateChanged(int state) {}
    };
    private void setAllSelected(){
        text_control.setSelected(false);
        text_data.setSelected(false);
        text_flower.setSelected(false);
    }
    /**
     * 配置viewpager
     */
    private void setupViewPager(){
        myFragmentPagerAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager());
        myViewPager.setOffscreenPageLimit(3);//最大缓存三个Fragment
        if(myFragmentPagerAdapter != null &&  myViewPager != null)
            myViewPager.setAdapter(myFragmentPagerAdapter);//设置适配器
        myViewPager.addOnPageChangeListener(onPageChangeListener);
    }
    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        MenuInflater inflater = getMenuInflater();
        Log.e("info","success");
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();

    }
    GuideView guideView_leftBtn;
    private GuideView guideView_selectFlowerBtn;
    private void setGuideView() {
        /**
         * leftButton 的引导
         */
        final ImageView leftBtn_img=new ImageView(this);
        leftBtn_img.setBackgroundResource(R.drawable.img_guide_leftbtn);
        RelativeLayout.LayoutParams params=new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        leftBtn_img.setLayoutParams(params);

        guideView_leftBtn=GuideView.Builder
                .newInstance(this)
                .setTargetView(left_menu)
                .setCustomGuideView(leftBtn_img)
                .setDirction(GuideView.Direction.RIGHT_BOTTOM)
                .setShape(GuideView.MyShape.CIRCULAR)
                .setBgColor(getResources().getColor(R.color.shadow))
                .setOnclickListener(new GuideView.OnClickCallback() {
                    @Override
                    public void onClickedGuideView() {
                        guideView_leftBtn.hide();
                        if(!dl.isDrawerOpen(left_linearLayout)){
                            dl.openDrawer(left_linearLayout);
                        }
                        guideView_selectFlowerBtn.show();
                    }
                })
                .build();

        /**
         * selectFlowerButton 的引导
         */
        final ImageView selectFlowerBtn_img=new ImageView(this);
        selectFlowerBtn_img.setBackgroundResource(R.drawable.img_guide_selectflowerbtn);
        RelativeLayout.LayoutParams params1=new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        selectFlowerBtn_img.setLayoutParams(params1);

        /**
         * 引导显示文字
         */
//        final TextView tv=new TextView(this);
//        tv.setText("点击这里选择植物哦");
//        tv.setTextColor(getResources().getColor(R.color.white));
//        tv.setTextSize(30);
//        tv.setGravity(Gravity.CENTER);

        guideView_selectFlowerBtn=GuideView.Builder
                .newInstance(this)
                .setTargetView(btn_selectFlower)
                .setCustomGuideView(selectFlowerBtn_img)
                .setDirction(GuideView.Direction.RIGHT_BOTTOM)
                .setShape(GuideView.MyShape.RECTANGULAR)
                .setRadius(80)
                .setBgColor(getResources().getColor(R.color.shadow))
                .setOnclickListener(new GuideView.OnClickCallback() {
                    @Override
                    public void onClickedGuideView() {
                        guideView_selectFlowerBtn.hide();
                    }
                })
                .build();

        guideView_leftBtn.show();
    }

    protected void onDestroy() {
        super.onDestroy();

        ActivityCollector.removeActivity(this);
        stopService(intent);//关闭服务
        unbindService(serviceConnection);//解除服务绑定
    }
}
