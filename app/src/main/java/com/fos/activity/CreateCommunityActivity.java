package com.fos.activity;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
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
//
////                    Uri uri=saveBitmap(bm);

                    String path = UriToPath(Uri.parse(uri));
                    Log.e("onResponse","判断前"+path);
                    File file=new File(path);
                    if(!file.exists()) {
                        Log.e("onResponse","相册中图片不存在");
                        imageView.setImageBitmap(bm);
                        mThreadPool.execute(new Runnable() {
                            @Override
                            public void run() {
                                updateToCamera(CreateCommunityActivity.this, bm);
                            }
                        });
                    }
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
            if(path==null) {
               path = getPath(CreateCommunityActivity.this, uri);
            }
            if(path!=null) {
                Log.e("onResponse","path====>"+path);
                upLoadPicture(path);
            }else{
                Log.e("onResponse", "onActivityResult: path为空" );
            }
        }
    }

    /**
     * 此方法用来更新到系统相册里面去
     * @param createCommunityActivity
     * @param bm
     */
    private void updateToCamera(CreateCommunityActivity createCommunityActivity, Bitmap bm) {
        File appDir=new File(Environment.getExternalStorageDirectory().getAbsolutePath(),"DCIM/Camera");
        if(!appDir.exists()){
            appDir.mkdirs();
        }
        String fileName=System.currentTimeMillis()+".jpg";
        File file=new File(appDir,fileName);
        try{
            try {
                FileOutputStream fos=new FileOutputStream(file);
                bm.compress(Bitmap.CompressFormat.JPEG,100,fos);
                fos.flush();
                fos.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }finally {
            ScannerByReceiver(createCommunityActivity,file.getAbsolutePath());
            if(!bm.isRecycled()){
                System.gc();
            }
        }
    }

    /**
     * 广播通知系统扫描
     * @param createCommunityActivity
     * @param absoluteFile
     */
    private void ScannerByReceiver(CreateCommunityActivity createCommunityActivity, String absoluteFile) {
        createCommunityActivity.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,Uri.parse("file://"+absoluteFile)));
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

    /**
     * 解决部分手机获取不到图片的问题
     * @param context
     * @param uri
     * @return
     */
    public String getPath(final Context context, final Uri uri){
        final boolean isKitKat=Build.VERSION.SDK_INT>=19;

        ContentResolver resolver=getContentResolver();
        Bitmap bm= null;
        try {
            bm = MediaStore.Images.Media.getBitmap(resolver,uri);
        } catch (IOException e) {
            e.printStackTrace();
        }
        imageView.setImageBitmap(ThumbnailUtils.extractThumbnail(bm,100,100));

        if(isKitKat && DocumentsContract.isDocumentUri(context,uri)){
            if(isExternalStorageDocument(uri)){
                final String docId=DocumentsContract.getDocumentId(uri);
                final String[] split=docId.split(":");
                final String type=split[0];
                
                if("primary".equalsIgnoreCase(type)){
                    return Environment.getExternalStorageDirectory()+"/"+split[1];
                }
            }else if(isDownloadsDocument(uri)){
                final String id=DocumentsContract.getDocumentId(uri);
                final Uri contentUri= ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"),Long.valueOf(id));
                return getDataColumn(context,contentUri,null,null);
            }
            else if(isMediaDocument(uri)){
                final String docId=DocumentsContract.getDocumentId(uri);
                final String[] split=docId.split(":");
                final String type=split[0];
                
                Uri contentUri=null;
                if("image".equals(type)){
                    contentUri=MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                }else if("video".equals(type)){
                    contentUri= MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                }else if("audio".equals(type)){
                    contentUri= MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }
                
                final String selection="_id=?";
                final String[] selectionArgs=new String[]{split[1]};
                return getDataColumn(context,contentUri,selection,selectionArgs);
            }
        }else if("content".equalsIgnoreCase(uri.getScheme())){
//            if(isGooglePhotoUri(uri)){
//                return uri.getLastPathSegment();
//            }
            return getDataColumn(context,uri,null,null);
        }else if("file".equalsIgnoreCase(uri.getScheme())){
            return uri.getPath();
        }else{

        }
        return null;
    }


    private static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    private static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        Cursor cursor=null;
        final String column="_data";
        final String[] projection={column};
        try{
            cursor=context.getContentResolver().query(uri,projection,selection,selectionArgs,null);
            if(cursor!=null&&cursor.moveToFirst()){
                final int index=cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        }finally {
            if(cursor!=null){
                cursor.close();
            }
        }
        return null;
    }

    private static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    private static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
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
            Log.e("onResponse", "getString: "+0 );
            Bitmap bm= MediaStore.Images.Media.getBitmap(resolver,uri);
            Log.e("onResponse", "getString: "+1 );
            imageView.setImageBitmap(ThumbnailUtils.extractThumbnail(bm,100,100));
            Log.e("onResponse", "getString: "+2 );
            String[] proj={MediaStore.Images.Media.DATA};
            Log.e("onResponse", "getString: "+3);
            Cursor cursor;
            if(Build.VERSION.SDK_INT<11){
                /**
                 * cursor 的managedQuery方法过时替换
                 */
                cursor=managedQuery(uri,proj,null,null,null);
                Log.e("onResponse", "getString: "+4.1 );
            }else{
                CursorLoader cursorLoader=new CursorLoader(this,uri,null,null,null,null);
                cursor=cursorLoader.loadInBackground();
                Log.e("onResponse", "getString: "+4.2 );
            }
            int column_index=cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            Log.e("onResponse", "getString: "+5 );
            cursor.moveToFirst();
            path=cursor.getString(column_index);
            Log.e("onResponse", "getString: "+6+path );
        } catch (Exception e) {
            e.printStackTrace();
            return path;
        }
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
