package com.fos.activity;

import android.content.ContentResolver;
import android.content.CursorLoader;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fos.R;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
    /**
     * 返回码
     */
    private static int CAMERA_REQUEST_CODE=1;
    private static int GALLY_REQUEST_CODE=2;
    private String mBaseUrl="http://192.168.23.1:8080/ImgDownLoadServlet/";
    OkHttpClient okHttpClient=new OkHttpClient();
    public static ExecutorService mThreadPool= Executors.newCachedThreadPool();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_community);
        init();
        createMyDialog();
    }

    private void init(){
        exit_create = (RelativeLayout)findViewById(R.id.exit_create);
        new_community  =  (Button)findViewById(R.id.new_community);
        edit_community =  (EditText)findViewById(R.id.edit_community);
        count_context =(TextView)findViewById(R.id.count_context);
        addImage = (ImageView)findViewById(R.id.addImage);
        imageView = findViewById(R.id.imageView);

        exit_create.setOnClickListener(onClickListener);
        new_community.setOnClickListener(onClickListener);
        edit_community.setOnClickListener(onClickListener);
        count_context.setOnClickListener(onClickListener);
        addImage.setOnClickListener(onClickListener);
        edit_community.addTextChangedListener(textWatcher);

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
                    break;
                case R.id.edit_community:
                    break;
                case R.id.count_context:
                    break;
                case R.id.addImage:
                    dialog.show();
                    break;
                case R.id.photograph:
                    Intent intent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==CAMERA_REQUEST_CODE) {
            if (data == null) {
                return;
            } else {
                Bundle extras = data.getExtras();
                if (extras != null) {
                    Bitmap bm = extras.getParcelable("data");

                    String uri = MediaStore.Images.Media.insertImage(getContentResolver(), bm, null, null);
                    Log.e("onResponse", "saveBitmap: url=" + uri);
                    Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                    CreateCommunityActivity.this.sendBroadcast(intent);
                    Log.e("onResponse", "插入到相册");

//                    Uri uri=saveBitmap(bm);
                    String path = UriToPath(Uri.parse(uri));
                    imageView.setImageBitmap(bm);
                    if (path != null && path.length() != 0) {
                        upLoadPicture(path);
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
            String path = UriToPath(uri);
            if(path!=null) {
                upLoadPicture(path);
            }
        }
    }

    private void upLoadPicture(final String path) {
        mThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                Log.e("onResponse", Build.VERSION.SDK_INT+"     " +path);
                File file=new File(path);
                doPostFile(file);
            }
        });
    }

    @Nullable
    private String UriToPath(Uri uri) {
        Log.e("onResponse", "getString: URI="+uri );
        String path=null;
        try {
            ContentResolver resolver=getContentResolver();
            Bitmap bm= MediaStore.Images.Media.getBitmap(resolver,uri);
            imageView.setImageBitmap(ThumbnailUtils.extractThumbnail(bm,100,100));

            String[] proj={MediaStore.Images.Media.DATA};
            Cursor cursor;
            if(Build.VERSION.SDK_INT<11){
                /**
                 * cursor 的managedQuery方法过时替换
                 */
                cursor=managedQuery(uri,proj,null,null,null);
            }else{
                CursorLoader cursorLoader=new CursorLoader(this,uri,null,null,null,null);
                cursor=cursorLoader.loadInBackground();
            }
            int column_index=cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            path=cursor.getString(column_index);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.e("onResponse===",path);
        return path;
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
                Log.e("onResponse:",res);
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        mTvResult.setText(res);
//                    }
//                });
            }
        });
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
            count_context.setText(edit_community.getText().length()+"/2000");
        }
    };
}
