package com.fos.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.fos.R;
import com.fos.dao.UserInfoDao;
import com.fos.entity.User;
import com.fos.entity.UserInfo;
import com.fos.service.netty.Client;
import com.fos.util.BitmapSetting;
import com.fos.util.InfomationAnalysis;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class UserInfoActivity extends AppCompatActivity {

    private RelativeLayout  exit_userInfo;
    private ListView listView_userInfo;
    private TextView changeIcon,userInfo_userName,photograph,album;
    private ImageView userInfo_icon;
    private Intent intent;
    private String  userInfo[];
    private List<Map<String,Object>> mapList;
    private Map<String,Object> item;
    private SimpleAdapter simpleAdapter;
    private AlertDialog dialog;
    File cameraFile;
    private static int CAMERA_REQUEST_CODE=1;
    private static int GALLY_REQUEST_CODE=2;
    private static int CROP_REQUEST_CODE=3;
    private String userID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
        createMyDialog();
        init();
        initListView();
    }

    private void init(){
        intent = getIntent();
        exit_userInfo =  (RelativeLayout)findViewById(R.id.exit_userInfo);
        listView_userInfo = (ListView)findViewById(R.id.listView_userInfo);
        changeIcon= (TextView)findViewById(R.id.changeIcon);
        userInfo_userName = (TextView)findViewById(R.id.userInfo_userName);
        userInfo_icon = (ImageView)findViewById(R.id.userInfo_icon);

        userInfo_userName.setText(intent.getExtras().getString("userName"));
        Glide.with(UserInfoActivity.this)
                .load(MainActivity.userInfo.getUserHeadImage())
                .priority(Priority.HIGH)
                .into(userInfo_icon);
        userInfo_icon.setScaleType(ImageView.ScaleType.CENTER_CROP);

        exit_userInfo.setOnClickListener(onClickListener);
        changeIcon.setOnClickListener(onClickListener);
    }

    private void initListView(){
        String content[] = {"账号","昵称"};
        userID=MainActivity.userInfo.getUserId();
        userInfo = new  String[]{userID,MainActivity.userInfo.getUserName()};
        mapList =new   ArrayList<>();
        for(int i =0;i<content.length;i++){
            item = new HashMap<>();
            item.put("content",content[i]);
            item.put("userInfo",userInfo[i]);
            mapList.add(item);
        }
        simpleAdapter = new SimpleAdapter(UserInfoActivity.this,mapList,R.layout.layout_userinfolist
                ,new String[]{"content","userInfo"},new int[]{R.id.userInfoList_content,R.id.userInfoList_userInfo});
        listView_userInfo.setAdapter(simpleAdapter);


    }

    private void createMyDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(UserInfoActivity.this);
        dialog  = builder.create();
        View dialogView = View.inflate(UserInfoActivity.this,R.layout.selectimage,null);
        dialog.setView(dialogView);
        photograph = (TextView)dialogView.findViewById(R.id.photograph);
        album = (TextView)dialogView.findViewById(R.id.album);

        photograph.setOnClickListener(onClickListener);//拍照
        album.setOnClickListener(onClickListener);//相册

    }

    /**
     * 获得 url后调用次方法
     * @param uri
     */

    private void resetIcon(String uri){
        /**
         * 更新 ui
         */
        Glide.with(UserInfoActivity.this)
                .load(uri)
                .priority(Priority.HIGH)
                .into(userInfo_icon);
        Glide.with(UserInfoActivity.this)
                .load(uri)
                .transform(new BitmapSetting(UserInfoActivity.this))
                .priority(Priority.HIGH)
                .into(MainActivity.user_icon);
        MainActivity.userInfo.setUserHeadImage(uri);

        /**
         * 发送数据至服务器
         */
        UserInfo user = new UserInfo();
        user.setUserId(MainActivity.userInfo.getUserId());
        user.setUserName(MainActivity.userInfo.getUserName());
        user.setUserHeadImage(uri);
        Client.getClient(InfomationAnalysis.BeantoUserInfo(user));

        /**
         * 更新 数据库
         */
        User user2 = new User();
        user2.setUserId(MainActivity.userInfo.getUserId());
        user2.setUserName(MainActivity.userInfo.getUserName());
        user2.setUserHeadImage(uri);
        UserInfoDao.getInstance().insertUserInfo(user2);
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.exit_userInfo:
                    finish();
                    break;
                case R.id.changeIcon:
                    dialog.show();
                    break;
                case R.id.photograph:
                    /**
                     * 解决缩略图
                     */
                    Intent intent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    String filename=userID+".jpg";
                    cameraFile=new File(Environment.getExternalStorageDirectory()+File.separator+Environment.DIRECTORY_DCIM+File.separator+"Camera"+File.separator+filename);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(cameraFile));
                    startActivityForResult(intent,CAMERA_REQUEST_CODE);
                    dialog.dismiss();
                    break;
                case R.id.album:
                    Intent intent1=new Intent(Intent.ACTION_GET_CONTENT);
                    intent1.setType("image/*");
                    startActivityForResult(intent1,GALLY_REQUEST_CODE);
                    dialog.dismiss();
                    break;
                default:
                    break;
            }
        }
    };


    /**
     *
     * @param requestCode
     * @param resultCode
     * @param data 用户选择的图片
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode==CAMERA_REQUEST_CODE) {
            if (!cameraFile.exists()) {
                Log.i("onResponse", "摄像头剪裁");
                Bitmap bitmap = null;
                try {
                    FileInputStream fis = new FileInputStream(cameraFile);
                    bitmap = BitmapFactory.decodeStream(fis);
                    fis.close();
                    Uri uri = saveBitmap(bitmap);
                    startImageZoom(uri);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
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
            Uri fileUri=convertUri(uri);
            startImageZoom(fileUri);
        }
        else if (requestCode==CROP_REQUEST_CODE){
            if(data==null){
                return;
            }
            Uri uri=data.getData();
          //  resetIcon(uri.toString());
            String path=CreateCommunityActivity.getPath.UriToPath(uri,UserInfoActivity.this);
            if(path==null){
                path=CreateCommunityActivity.getPath.getPath(UserInfoActivity.this, uri);
            }
            if(path!=null){
                final String finalPath = path;
                Log.e("onResponse", "onActivityResult: "+path );
                CreateCommunityActivity.mThreadPool.execute(new Runnable() {
                    @Override
                    public void run() {
                        File file=new File(finalPath);
                        doPost(file);
                    }
                });
            }
        }
    }

    /**
     * 把bitmap存储到SD卡并转换成File型Uri
     * @param bm
     * @return
     */
    private Uri saveBitmap(Bitmap bm){
        /**
         * 用时间戳方式命名拍照文件
         */
        String fileName;
        SimpleDateFormat format=new SimpleDateFormat("yyyyMMddHHmmss");
        Date date =new Date(System.currentTimeMillis());
        fileName=format.format(date)+".jpg";

        String photoPath=Environment.getExternalStorageDirectory()+File.separator+Environment.DIRECTORY_DCIM+File.separator+"Camera"+File.separator;
        File img=new File(photoPath,fileName);
        Log.e("onResponse",img.toString());
        String url=MediaStore.Images.Media.insertImage(getContentResolver(),bm,null,null);
        Log.e("onResponse", "saveBitmap: url="+url );
        Intent intent=new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        UserInfoActivity.this.sendBroadcast(intent);
        Log.e("onResponse", "插入到相册" );
        return Uri.parse(url);
    }
    /**
     * 调用系统自带的剪裁功能
     * @param uri
     */
    private void startImageZoom(Uri uri){
        Intent intent=new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri,"image/*");
        intent.putExtra("crop","true");
        intent.putExtra("aspectX",1);
        intent.putExtra("aspectY",1);
        intent.putExtra("outputX",150);
        intent.putExtra("outputY",150);
        intent.putExtra("return-data",true);
        startActivityForResult(intent,CROP_REQUEST_CODE);
    }

    /**
     * 把Content Uri 转换为File型的Uri
     * @param uri
     * @return
     */
    private Uri convertUri(Uri uri){
        InputStream is=null;
        try {
            /**
             * 从Uri中获取InputStream
             */
            is=getContentResolver().openInputStream(uri);
            Bitmap bitmap= BitmapFactory.decodeStream(is);
            is.close();
            return saveBitmap(bitmap);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    private void doPost(File img) {
        Log.e("onResponse","开始上传");
        MultipartBody.Builder bodyBuilder=new MultipartBody.Builder();
        RequestBody requestBody=bodyBuilder
                .setType(MultipartBody.FORM)
                .addFormDataPart("userName",userID)
                .addFormDataPart("mPhoto",userID+".jpg",RequestBody.create(MediaType.parse("application/octet-stream"),img))
                .build();
        Request.Builder builder=new Request.Builder();
        Request request=builder
                .url(CreateCommunityActivity.mBaseUrl+"uploadInfo")
                .post(requestBody)
                .build();
        executeRequest(request);
    }

    private void executeRequest(final Request request) {
        Call call=CreateCommunityActivity.okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("onResponse===",e.getMessage());
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String res=response.body().string();
                Log.e("onResponse",res);
                resetIcon(res);
            }
        });
    }
}
