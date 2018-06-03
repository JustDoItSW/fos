package com.fos.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fos.R;
import com.fos.entity.Community;
import com.fos.entity.UserInfo;
import com.fos.service.netty.Client;
import com.fos.util.InfomationAnalysis;
import com.fos.util.MyGridViewAdapter2;
import com.fos.util.GetPath;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import id.zelory.compressor.Compressor;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CreateCommunityActivity extends AppCompatActivity {

    private RelativeLayout exit_create;
    private Button new_community;
    private EditText edit_community;
    private TextView count_context,photograph,album;
    private ImageView addImage,imageView;
    private AlertDialog dialog;
    private GridView create_gridView;
    private DisplayMetrics dm;
    private List<Bitmap> datas ;
    private List<String > datas2;
    private String allUri = "";
    private MyGridViewAdapter2 myGridViewAdapter2;
    public static Handler handler;
    /**
     * 返回码
     */
    private static int CAMERA_REQUEST_CODE=1;
    private static int GALLY_REQUEST_CODE=2;
//      192.168.23.1
    public static String mBaseUrl="http://47.106.161.42:8080/ImgServlet/";
    public static OkHttpClient okHttpClient=new OkHttpClient();
    public static ExecutorService mThreadPool= Executors.newCachedThreadPool();
    public static GetPath getPath= GetPath.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_community);
        init();
        createMyDialog();
        initGridView();
    }

    private void init(){
        dm = CreateCommunityActivity.this.getResources().getDisplayMetrics();
        exit_create = (RelativeLayout)findViewById(R.id.exit_create);
        new_community  =  (Button)findViewById(R.id.new_community);
        edit_community =  (EditText)findViewById(R.id.edit_community);
        count_context =(TextView)findViewById(R.id.count_context);
        addImage = (ImageView)findViewById(R.id.addImage);
        imageView = findViewById(R.id.imageView);
        create_gridView = (GridView)findViewById(R.id.create_gridView);

        exit_create.setOnClickListener(onClickListener);
        new_community.setOnClickListener(onClickListener);
        edit_community.setOnClickListener(onClickListener);
        count_context.setOnClickListener(onClickListener);
        addImage.setOnClickListener(onClickListener);
        edit_community.addTextChangedListener(textWatcher);

        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                Bundle bundle  =  msg.getData();
                String str = bundle.getString("info");
                if(str.equals("submitCommunitySuccessful")) {
                    Toast.makeText(CreateCommunityActivity.this, "发布成功！", Toast.LENGTH_LONG).show();
                    Client.getClient("getCommunity");
                    finish();
                }else if(str.equals("submitCommunityFailure")){
                    Toast.makeText(CreateCommunityActivity.this, "发布失败！", Toast.LENGTH_LONG).show();
                    edit_community.setEnabled(true);
                    create_gridView.setEnabled(true);
                    new_community.setEnabled(true);
                }
            }
        };


    }

    private void initGridView(){
        datas  =  new ArrayList<>();
        datas2  = new ArrayList<>();
        myGridViewAdapter2 = new MyGridViewAdapter2(datas2,CreateCommunityActivity.this);
        create_gridView.setAdapter(myGridViewAdapter2);
        create_gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.e("info","点击的图片为："+position);
              //  if(datas.size() == (position))
                dialog.show();
            }
        });
    }

    /**
     * 创建选择图片方式对话框
     */
    private void createMyDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(CreateCommunityActivity.this);
        dialog  = builder.create();
        View dialogView = View.inflate(CreateCommunityActivity.this,R.layout.selectimage,null);
        dialog.setView(dialogView);
        photograph = (TextView)dialogView.findViewById(R.id.photograph);
        album = (TextView)dialogView.findViewById(R.id.album);

        photograph.setOnClickListener(onClickListener);//拍照
        album.setOnClickListener(onClickListener);//相册

    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.exit_create:
                    finish();
                    break;
                case R.id.new_community:
                    if(edit_community.getText().toString().equals("")){
                        Toast.makeText(CreateCommunityActivity.this,"动态内容不能为空！",Toast.LENGTH_SHORT).show();
                    }else {
                        edit_community.setEnabled(false);
                        create_gridView.setEnabled(false);
                        new_community.setEnabled(false);
                        if(datas2.size() == 0){
                        sendCommunityToService(null);
                        }else {
                            for (int i = 0; i < datas2.size(); i++) {
                                upLoadPicture(datas2.get(i));
                            }
                        }
                    }
                    break;
                case R.id.edit_community:
                    break;
                case R.id.count_context:
                    break;
                case R.id.addImage:
                    dialog.show();
                    break;
                case R.id.photograph:
                    dialog.dismiss();
                    Intent intent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent,CAMERA_REQUEST_CODE);

                    break;
                case R.id.album:
                    dialog.dismiss();
                    Intent intent1=new Intent(Intent.ACTION_GET_CONTENT);
                    intent1.setType("image/*");
                    startActivityForResult(intent1,GALLY_REQUEST_CODE);

                    break;
                    default:
                        break;
            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
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
                    CreateCommunityActivity.this.sendBroadcast(intent);
                    Log.e("onResponse", "插入到相册" );

//                Uri uri=saveBitmap(bm);
                    String path = getPath.UriToPath(Uri.parse(uri),CreateCommunityActivity.this);
                    Log.e("onResponse","判断前"+path);
                    File file=new File(path);
                    if(!file.exists()) {
                        Log.e("onResponse","相册中图片不存在");
                       // imageView.setImageBitmap(bm);
                        mThreadPool.execute(new Runnable() {
                            @Override
                            public void run() {
                                getPath.updateToCamera(CreateCommunityActivity.this, bm);
                            }
                        });
                    }
                    if (path != null && path.length() != 0) {
                     //   upLoadPicture(path);
                        datas.add(bm);
                        datas2.add(path);
                        myGridViewAdapter2.notifyDataSetChanged();
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
            String path = getPath.UriToPath(uri,CreateCommunityActivity.this);
            Log.e("onResponse","UriToPath====>"+path);
            if(path==null) {
               path = getPath.getPath(CreateCommunityActivity.this, uri);
            }
            if(path!=null) {
                Log.e("onResponse","getPath====>"+path);
                datas2.add(path);
                myGridViewAdapter2.notifyDataSetChanged();
           //     upLoadPicture(path);
            }else{
                Log.e("onResponse", "onActivityResult: path为空" );
            }
        }
    }


//    private void updatecim(Uri uri){
//        Log.e("onResponse", "saveBitmap: url=" + uri);
//        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
//        intent.setData(uri);
//        CreateCommunityActivity.this.sendBroadcast(intent);
//
////        Bitmap bitmap= BitmapFactory.decodeFile(uri.getPath());
////        MediaStore.Images.Media.insertImage(getContentResolver(),bitmap,"","");
//
//        Log.e("onResponse", "插入到相册");
//    }

    private void upLoadPicture(final String path) {
        mThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                Log.e("onResponse", Build.VERSION.SDK_INT+"     " +path);
             //   File file=new File(path);
                try {
                    File file = new Compressor(CreateCommunityActivity.this)
                            .setMaxWidth(640)
                            .setMaxHeight(480)
                            .setQuality(75)
                            .setCompressFormat(Bitmap.CompressFormat.WEBP)
                            .setDestinationDirectoryPath(Environment.getExternalStoragePublicDirectory(
                                    Environment.DIRECTORY_PICTURES).getAbsolutePath())
                            .compressToFile(new File(path));
                    doPostFile(file);
                }catch (Exception  e){
                    e.printStackTrace();
                }

            }
        });
    }

    public void doPostFile(File file){
        RequestBody requestBody=RequestBody.create(MediaType.parse("application/octet-stream"),file);
        Request.Builder builder=new Request.Builder();
        Request request=builder
                .url(mBaseUrl+"postFile")
                .post(requestBody)
                .build();
        executeRequest(request);
    }

    private void executeRequest(Request request) {
        //3.将request封装为call
        //执行call
        Call call=okHttpClient.newCall(request);
//        call.execute();
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("onFailure",e.getMessage());
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String res=response.body().string();
                Log.e("onResponse from tomcat:",res);
                allUri= allUri+res+";";
                Log.e("info","allUri.split(\";\").length "+allUri.split(";").length);
                if(allUri.split(";").length  == datas2.size()) {
                    sendCommunityToService(allUri);
                    allUri = "";
                }
            }
        });
    }
    private void sendCommunityToService(String res){
        UserInfo userInfo   = new UserInfo();
        userInfo.setUserName(MainActivity.userInfo.getUserName());
        userInfo.setClassName( "UserInfo");
        userInfo.setUserId(MainActivity.userInfo.getUserId());
        Community community = new Community();
        community.setPicture(res);
        community.setClassName("Community");
        community.setTime(new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date(System.currentTimeMillis())));
        community.setContent(edit_community.getText().toString());
        community.setUserInfo(userInfo);
        Client.getClient(InfomationAnalysis.BeanToCommunity(community));
    }

    TextWatcher textWatcher  =  new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            count_context.setText(edit_community.getText().length()+"/200");
        }
    };
}
