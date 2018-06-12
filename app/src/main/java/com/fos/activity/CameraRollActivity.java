package com.fos.activity;


import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.aip.imageclassify.AipImageClassify;
import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.fos.R;
import com.fos.entity.AIPlant;

import com.fos.util.AIPlantUtil;
import com.fos.util.GetPath;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

public class CameraRollActivity extends AppCompatActivity {

    private TextView resultView,photograph,album;
    private RelativeLayout exit_recognition;
    private Button chooseImage;
    private AlertDialog dialog;
    private static String APP_ID=null;
    private static String API_KEY=null;
    private static String SECRET_KEY=null;
    private static AipImageClassify aipImageClassify=null;
    private String resultContent = "";
    private Handler  handler;
    /**
     * 返回码
     */
    private int CAMERA_REQUEST_CODE=1;
    private int GALLY_REQUEST_CODE=2;
    private ImageView image;
    public GetPath getPath=CreateCommunityActivity.getPath;
    public ArrayList<AIPlant> al=null;


    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_roll);
        init();
        createMyDialog();
    }

    private void init(){
        chooseImage = (Button) findViewById(R.id.choose_image);
        resultView = (TextView) findViewById(R.id.results);//显示结果
        exit_recognition = (RelativeLayout)findViewById(R.id.exit_recognition);
        image=findViewById(R.id.image);
        APP_ID=getResources().getString(R.string.AIPlantAPP_ID);
        API_KEY=getResources().getString(R.string.AIPlantAPI_KEY);
        SECRET_KEY=getResources().getString(R.string.AIPlantSECRET_KEY);
        aipImageClassify=AIPlantUtil.getAccess_token(APP_ID,API_KEY,SECRET_KEY);

        exit_recognition.setOnClickListener(onClickListener);
        chooseImage.setOnClickListener(onClickListener);

        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if(msg.what ==  0x001){
                    resultView.setText(resultContent);
                    resultContent = "";
                }
            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    /**
     * 拍照和相册的回调函数
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(al!=null&&al.size()!=0)
        {
                al.clear();
                Log.e("aiplant==",""+al.size());
        }
            al=new ArrayList<AIPlant>();
        if(requestCode==CAMERA_REQUEST_CODE) {
            if (data == null) {
                return;
            } else {
                Bundle extras = data.getExtras();
                if (extras != null) {
                    final Bitmap bm = extras.getParcelable("data");

                    String uri = MediaStore.Images.Media.insertImage(getContentResolver(), bm, null, null);
                    /**
                     * 广播更新相册
                     */
                    Log.e("onResponse", "saveBitmap: url="+uri );
                    Intent intent=new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                    CameraRollActivity.this.sendBroadcast(intent);
                    Log.e("onResponse", "插入到相册" );

                    String path = getPath.UriToPath(Uri.parse(uri),CameraRollActivity.this);
                    Log.e("onResponse","判断前"+path);
                    File file=new File(path);
                    if(!file.exists()) {
                        Log.e("onResponse","相册中图片不存在");
                        CreateCommunityActivity.mThreadPool.execute(new Runnable() {
                            @Override
                            public void run() {
                                getPath.updateToCamera(CameraRollActivity.this, bm);
                            }
                        });
                    }
                    if (path != null && path.length() != 0) {
                        updateUI(path);
                        getResult(path);
                    }
                }
            }
        }
        else if(requestCode==GALLY_REQUEST_CODE)
        {
            if(data==null){
                return;
            }
            /**
             * 获取图片的同意资源标识符
             */
            Uri uri=data.getData();
            String path = getPath.UriToPath(uri,CameraRollActivity.this);
            Log.e("onResponse","UriToPath====>"+path);
            if(path==null) {
                path = getPath.getPath(CameraRollActivity.this, uri);
            }
            if(path!=null) {
                Log.e("onResponse","getPath====>"+path);
                updateUI(path);
                getResult(path);
            }else{
                Log.e("onResponse", "onActivityResult: path为空" );
            }
        }

    }

    private void createMyDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(CameraRollActivity.this);
        dialog  = builder.create();
        View dialogView = View.inflate(CameraRollActivity.this,R.layout.selectimage,null);
        dialog.setView(dialogView);
        photograph = (TextView)dialogView.findViewById(R.id.photograph);
        album = (TextView)dialogView.findViewById(R.id.album);

        photograph.setOnClickListener(onClickListener);//拍照
        album.setOnClickListener(onClickListener);//相册

    }

    View.OnClickListener  onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.exit_recognition:
                    finish();
                    break;
                case R.id.choose_image:
                    dialog.show();
                    break;
                case R.id.album:
                    dialog.dismiss();
                    Intent intent1=new Intent(Intent.ACTION_GET_CONTENT);
                    intent1.setType("image/*");
                    startActivityForResult(intent1,GALLY_REQUEST_CODE);
                    break;
                case R.id.photograph:
                    dialog.dismiss();
                    Intent intent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent,CAMERA_REQUEST_CODE);

                    break;
                    default:
                        break;
            }
        }
    };

    /**
     * 得到百度端返回的JSON数据
     * @param path 图片路径
     */
    public void getResult(final String path){
        CreateCommunityActivity.mThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                String result= AIPlantUtil.sample(aipImageClassify,path);
                if(result==null)
                    return;
                Log.e("result_plant",result);
                analyzeJSONArray(result);

            }
        });
    }

    /**
     * 解析返回的jsonArray
     * @param result
     */
    private void analyzeJSONArray(String result) {
        try {
            JSONObject jsonObject=new JSONObject(result);
            JSONArray jsonArray=jsonObject.getJSONArray("result");
            for(int i=0;i<jsonArray.length();i++){
                JSONObject json=jsonArray.getJSONObject(i);
                AIPlant aiPlant=new AIPlant();
                aiPlant.setName(json.getString("name"));
                aiPlant.setScore(json.getString("score"));
                al.add(aiPlant);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }finally{
            for(int i=0;i<al.size();i++){
                Log.e("aiplant",i+" name:"+al.get(i).getName()+" score:"+al.get(i).getScore());
                resultContent+="为"+al.get(i).getName()+"的概率为:"+al.get(i).getScore()+"\n";
            }
            resultContent+="该图片为"+al.get(0).getName()+"的概率最高，所以可能为"+al.get(0).getName()+"\n";
            handler.sendEmptyMessage(0x001);

        }

    }

    /**
     * 把图片更新到界面上去
     * @param path 图片的路径
     */
    private void updateUI(final String path){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Glide.with(CameraRollActivity.this)
                        .load(path)
                        .priority(Priority.HIGH)
                        .into(image);
            }
        });
    }
}
