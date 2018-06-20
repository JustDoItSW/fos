package com.fos.activity;


import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureFailure;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.aip.imageclassify.AipImageClassify;
import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.fos.R;
import com.fos.entity.AIPlant;
import com.fos.entity.Flower;
import com.fos.service.netty.Client;
import com.fos.tensorflow.Classifier;
import com.fos.tensorflow.TensorFlowImageClassifier;
import com.fos.util.AIPlantUtil;
import com.fos.util.BitmapSetting;
import com.fos.util.GetPath;
import com.fos.util.InfomationAnalysis;
import com.mingle.widget.LoadingView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class CameraActivity extends AppCompatActivity {

    // Classifier
    private Bitmap bitmap;
    private Classifier classifier;
    private static final int INPUT_SIZE = 224;
    private static final int IMAGE_MEAN = 128;
    private static final float IMAGE_STD = 128.0f;
    private static final String INPUT_NAME = "input";
    private static final String OUTPUT_NAME = "final_result";
    private static final String MODEL_FILE = "file:///android_asset/optimized_mobilenet_plant_graph.pb";
    private static final String LABEL_FILE = "file:///android_asset/plant_labels.txt";

    private static String APP_ID=null;
    private static String API_KEY=null;
    private static String SECRET_KEY=null;
    private static AipImageClassify aipImageClassify=null;
    private String resultContent = "";
    public static Handler handler;

    private int CAMERA_REQUEST_CODE=1;
    private int GALLY_REQUEST_CODE=2;
    public GetPath getPath=GetPath.getInstance();
    public ArrayList<AIPlant> al=null;

    private Button  btn1,btn2,remake2;
    private RelativeLayout takePicture,exit_Camera,rl_bottom,rl_top,rl1_bottom,rl2_bottom,remake;
    private TextView fromPhotograph,result_flowerName,result_disease;
    private LoadingView isAppraisal;
    private ImageView changeCamera,picture_local,result_flowerImage;
    private ObjectAnimator objectAnimator1,objectAnimator2,objectAnimator4,objectAnimator5;
    private AnimatorSet animationSet1,animationSet2;
    private View ring_camera;
    private TextureView showCamera;
    private String mCameraId = "0";//摄像头id（通常0代表后置摄像头，1代表前置摄像头）
    private final int RESULT_CODE_CAMERA=1;//判断是否有拍照权限的标识码
    private CameraDevice cameraDevice;
    private CameraCaptureSession mPreviewSession;
    private CaptureRequest.Builder mCaptureRequestBuilder,captureRequestBuilder;
    private CaptureRequest mCaptureRequest;
    private ImageReader imageReader;
    private int height=0,width=0;
    private Size previewSize;
    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();
    static
    {
        ORIENTATIONS.append(Surface.ROTATION_0, 90);
        ORIENTATIONS.append(Surface.ROTATION_90, 0);
        ORIENTATIONS.append(Surface.ROTATION_180, 270);
        ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        allScreen();
        init();
        initObjectAnimator();

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

    private void init(){
        classifier =
                TensorFlowImageClassifier.create(
                        getAssets(),
                        MODEL_FILE,
                        LABEL_FILE,
                        INPUT_SIZE,
                        IMAGE_MEAN,
                        IMAGE_STD,
                        INPUT_NAME,
                        OUTPUT_NAME);

        exit_Camera = (RelativeLayout)findViewById(R.id.exit_Camera) ;
        changeCamera = (ImageView)findViewById(R.id.changeCamera);
        picture_local  = (ImageView)findViewById(R.id.picture_local);
        remake= (RelativeLayout)findViewById(R.id.remake) ;
        rl_top = (RelativeLayout)findViewById(R.id.r1_top) ;
        showCamera = (TextureView) findViewById(R.id.showCamera);
        ring_camera = (View)findViewById(R.id.ring_camera);
        fromPhotograph = (TextView)findViewById(R.id.fromPhotograph);
        takePicture = (RelativeLayout)findViewById(R.id.takePicture) ;
        result_flowerName = (TextView)findViewById(R.id.result_flowerName);
        result_flowerImage = (ImageView)findViewById(R.id.result_flowerImage);
        result_disease = (TextView)findViewById(R.id.result_disease);
        isAppraisal = (LoadingView)findViewById(R.id.isAppraisal);
        btn1  = (Button)findViewById(R.id.btn1);
        btn2  = (Button)findViewById(R.id.btn2);
        remake2  = (Button)findViewById(R.id.remake2);
        rl_bottom = (RelativeLayout)findViewById(R.id.rl_bottom) ;
        rl2_bottom = (RelativeLayout)findViewById(R.id.rl2_bottom) ;
        rl1_bottom = (RelativeLayout)findViewById(R.id.rl1_bottom) ;

        APP_ID=getResources().getString(R.string.AIPlantAPP_ID);
        API_KEY=getResources().getString(R.string.AIPlantAPI_KEY);
        SECRET_KEY=getResources().getString(R.string.AIPlantSECRET_KEY);
        aipImageClassify=AIPlantUtil.getAccess_token(APP_ID,API_KEY,SECRET_KEY);

        showCamera.setSurfaceTextureListener(surfaceTextureListener);//设置TextureView监听
        fromPhotograph.setOnClickListener(onClickListener);
        changeCamera.setOnClickListener(onClickListener);
        takePicture.setOnClickListener(onClickListener);//拍照
        exit_Camera.setOnClickListener(onClickListener);
        remake.setOnClickListener(onClickListener);
        remake2.setOnClickListener(onClickListener);

        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if(msg.what == 0x001){
                    //查询正确
                    Bundle bundle  = msg.getData();
                    String info = bundle.getString("info");
                    final Flower[] flowers  = InfomationAnalysis.jsonToFlower(info);
                    try {
                        if (!flowers[0].getFlowerImage().equals("error")) {
                            result_flowerImage.setImageBitmap(null);
                            Glide.with(CameraActivity.this)
                                    .load(flowers[0].getFlowerImage())
                                    .transform(new BitmapSetting(CameraActivity.this))
                                    .priority(Priority.HIGH)
                                    .into(result_flowerImage);
                            result_flowerImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
                            result_flowerImage.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(CameraActivity.this,FlowerInfo.class);
                                    intent.putExtra("Flower",flowers[0]);
                                    intent.putExtra("isSelect",false);
                                    startActivity(intent);
                                }
                            });
                            //病识别
                            classifyImage(bitmap);
                        }else{
                            result_flowerImage.setOnClickListener(null);
                        }
                    }catch (Exception e){}
                }
                else if(msg.what ==  0x002){
                    //识别成功
                    isAppraisal.setVisibility(View.GONE);
                    rl2_bottom.setVisibility(View.VISIBLE);
                    result_flowerName.setText(resultContent);
                    btn1.setVisibility(View.VISIBLE);
                    btn2.setVisibility(View.VISIBLE);
                    remake2.setVisibility(View.GONE);
                    result_flowerImage.setOnClickListener(null);
                    Client.getClient("search"+resultContent);
                }else {
                    //识别失败
                    isAppraisal.setVisibility(View.GONE);
                    result_flowerImage.setImageBitmap(null);
                    Glide.with(CameraActivity.this)
                            .load(R.mipmap.ic_cry)
                            .transform(new BitmapSetting(CameraActivity.this))
                            .priority(Priority.HIGH)
                            .into(result_flowerImage);
                    result_flowerImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    result_flowerImage.setOnClickListener(null);
                    rl2_bottom.setVisibility(View.VISIBLE);
                    result_flowerName.setText(resultContent);
                    btn1.setVisibility(View.GONE);
                    btn2.setVisibility(View.GONE);
                    remake2.setVisibility(View.VISIBLE);
                }
            }
        };

    }

    private void initObjectAnimator(){
        DisplayMetrics dm = getResources().getDisplayMetrics();
        objectAnimator1 = ObjectAnimator.ofFloat(rl_top, "translationY", 0, -dm.heightPixels*10/42);
        objectAnimator1.setDuration(500);
        objectAnimator2  = ObjectAnimator.ofFloat(rl_bottom, "translationY", 0, -dm.heightPixels*10/21);
        objectAnimator2.setDuration(500);

        animationSet1 = new AnimatorSet ();
        animationSet1.setDuration(500);
        animationSet1.setInterpolator(new DecelerateInterpolator());
        animationSet1.play(objectAnimator2).with(objectAnimator1);

        objectAnimator4 = ObjectAnimator.ofFloat(rl_top, "translationY",  -dm.heightPixels*10/42,0);
        objectAnimator4.setDuration(500);
        objectAnimator5 = ObjectAnimator.ofFloat(rl_bottom, "translationY", -dm.heightPixels*10/21,0);
        objectAnimator5.setDuration(500);

        animationSet2 = new AnimatorSet ();
        animationSet2.setDuration(500);
        animationSet2.setInterpolator(new DecelerateInterpolator());
        animationSet2.play(objectAnimator5).with(objectAnimator4);
    }

    View.OnClickListener onClickListener  = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.takePicture:
                    takePicture();
                    break;
                case R.id.exit_Camera:
                    finish();
                    break;
                case R.id.fromPhotograph:
                    Intent intent1=new Intent(Intent.ACTION_GET_CONTENT);
                    intent1.setType("image/*");
                    startActivityForResult(intent1,GALLY_REQUEST_CODE);
                    break;
                case R.id.remake:
                    remake();
                    break;
                case R.id.remake2:
                    remake();
                    break;
                case R.id.changeCamera:
                    if(mCameraId.equals("0"))
                        mCameraId = "1";
                    else
                        mCameraId ="0";
                    stopCamera();
                    startCamera();
                    break;
                    default:
                        break;
            }
        }
    };

    private void  remake(){
        startCamera();
        bitmap = null;
        picture_local.setImageBitmap(null);
        rl1_bottom.setVisibility(View.VISIBLE);
        rl2_bottom.setVisibility(View.INVISIBLE);
        isAppraisal.setVisibility(View.INVISIBLE);
        result_disease.setVisibility(View.INVISIBLE);
        result_disease.setText("植物可能生病啦,点击查看详情");
        remake.setVisibility(View.INVISIBLE);
        ring_camera.setVisibility(View.VISIBLE);
        animationSet2.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(cameraDevice!=null) {
            stopCamera();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        startCamera();
    }

    /**TextureView的监听*/
    private TextureView.SurfaceTextureListener surfaceTextureListener= new TextureView.SurfaceTextureListener() {

        //可用
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
            CameraActivity.this.width=width;
            CameraActivity.this.height=height;
            openCamera();
        }


        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

        }

        //释放
        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
            stopCamera();
            return true;
        }

        //更新
        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surface) {

        }
    };


    /**打开摄像头*/
    private void openCamera() {
        CameraManager manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        //设置摄像头特性
        setCameraCharacteristics(manager);
        try {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                //提示用户开户权限
                String[] perms = {"android.permission.CAMERA"};
                ActivityCompat.requestPermissions(CameraActivity.this,perms, RESULT_CODE_CAMERA);

            }else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    manager.openCamera(mCameraId, stateCallback, null);
                }
            }

        } catch (Exception e){
            e.printStackTrace();
        }
    }


    /**设置摄像头的参数*/
    private void setCameraCharacteristics(CameraManager manager)
    {
        try
        {
            // 获取指定摄像头的特性
            CameraCharacteristics characteristics
                    = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                characteristics = manager.getCameraCharacteristics(mCameraId);

                // 获取摄像头支持的配置属性
                StreamConfigurationMap map = characteristics.get(
                        CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
                // 获取摄像头支持的最大尺寸
                Size largest = Collections.max(
                        Arrays.asList(map.getOutputSizes(ImageFormat.JPEG)), new CompareSizesByArea());
                // 创建一个ImageReader对象，用于获取摄像头的图像数据
                imageReader = ImageReader.newInstance(largest.getWidth(), largest.getHeight(),
                        ImageFormat.JPEG, 2);
                //设置获取图片的监听
                imageReader.setOnImageAvailableListener(imageAvailableListener, null);
                // 获取最佳的预览尺寸
                previewSize = chooseOptimalSize(map.getOutputSizes(
                        SurfaceTexture.class), width, height, largest);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }
    private static Size chooseOptimalSize(Size[] choices
            , int width, int height, Size aspectRatio)
    {
        // 收集摄像头支持的大过预览Surface的分辨率
        List<Size> bigEnough = new ArrayList<>();
        int w = 0;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            w = aspectRatio.getWidth();

            int h = aspectRatio.getHeight();
            for (Size option : choices) {
                if (option.getHeight() == option.getWidth() * h / w &&
                        option.getWidth() >= width && option.getHeight() >= height) {
                    bigEnough.add(option);
                }
            }
        }
        // 如果找到多个预览尺寸，获取其中面积最小的
        if (bigEnough.size() > 0)
        {
            return Collections.min(bigEnough, new CompareSizesByArea());
        }
        else
        {
            //没有合适的预览尺寸
            return choices[0];
        }
    }


    // 为Size定义一个比较器Comparator
    static class CompareSizesByArea implements Comparator<Size>
    {
        @Override
        public int compare(Size lhs, Size rhs)
        {
            // 强转为long保证不会发生溢出
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                return Long.signum((long) lhs.getWidth() * lhs.getHeight() -
                        (long) rhs.getWidth() * rhs.getHeight());
            }
            return -1;
        }
    }



    /**摄像头状态的监听*/
    private CameraDevice.StateCallback stateCallback = new CameraDevice. StateCallback()
    {
        // 摄像头被打开时触发该方法
        @Override
        public void onOpened(CameraDevice cameraDevice){
            CameraActivity.this.cameraDevice = cameraDevice;
            // 开始预览
            takePreview();
        }

        // 摄像头断开连接时触发该方法
        @Override
        public void onDisconnected(CameraDevice cameraDevice)
        {
            CameraActivity.this.cameraDevice.close();
            CameraActivity.this.cameraDevice = null;

        }
        // 打开摄像头出现错误时触发该方法
        @Override
        public void onError(CameraDevice cameraDevice, int error)
        {
            cameraDevice.close();
        }
    };

    /**
     * 开始预览
     */
    private void takePreview() {
        SurfaceTexture mSurfaceTexture = showCamera.getSurfaceTexture();
        //设置TextureView的缓冲区大小
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mSurfaceTexture.setDefaultBufferSize(previewSize.getWidth(), previewSize.getHeight());

        //获取Surface显示预览数据
        Surface mSurface = new Surface(mSurfaceTexture);
        try {
            //创建预览请求
            mCaptureRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            // 设置自动对焦模式
            mCaptureRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
            //设置Surface作为预览数据的显示界面
            mCaptureRequestBuilder.addTarget(mSurface);
            //创建相机捕获会话，第一个参数是捕获数据的输出Surface列表，第二个参数是CameraCaptureSession的状态回调接口，当它创建好后会回调onConfigured方法，第三个参数用来确定Callback在哪个线程执行，为null的话就在当前线程执行
            cameraDevice.createCaptureSession(Arrays.asList(mSurface,imageReader.getSurface()),new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(CameraCaptureSession session) {
                    try {
                        //开始预览
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            mCaptureRequest = mCaptureRequestBuilder.build();

                        mPreviewSession = session;
                        //设置反复捕获数据的请求，这样预览界面就会一直有数据显示
                        mPreviewSession.setRepeatingRequest(mCaptureRequest, null, null);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onConfigureFailed(CameraCaptureSession session) {

                }
            }, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
        }

    }


    /**拍照*/
    private void takePicture()
    {
        try
        {
            if (cameraDevice == null)
            {
                return;
            }
            // 创建拍照请求
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                captureRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);

            // 设置自动对焦模式
            captureRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
            // 将imageReader的surface设为目标
            captureRequestBuilder.addTarget(imageReader.getSurface());
            // 获取设备方向
            int rotation = getWindowManager().getDefaultDisplay().getRotation();
            // 根据设备方向计算设置照片的方向
            captureRequestBuilder.set(CaptureRequest.JPEG_ORIENTATION
                    , ORIENTATIONS.get(rotation));
            // 停止连续取景
            mPreviewSession.stopRepeating();
            //拍照
            CaptureRequest captureRequest = captureRequestBuilder.build();
            //设置拍照监听
            mPreviewSession.capture(captureRequest,captureCallback, null);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**监听拍照结果*/
    private CameraCaptureSession.CaptureCallback captureCallback= new CameraCaptureSession.CaptureCallback()
    {
        // 拍照成功
        @Override
        public void onCaptureCompleted(CameraCaptureSession session,CaptureRequest request,TotalCaptureResult result)
        {
            // 重设自动对焦模式
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                captureRequestBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER, CameraMetadata.CONTROL_AF_TRIGGER_CANCEL);

            // 设置自动曝光模式
            captureRequestBuilder.set(CaptureRequest.CONTROL_AE_MODE, CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH);
            try {
                //重新进行预览
                mPreviewSession.setRepeatingRequest(mCaptureRequest, null, null);
            } catch (Exception e) {
                e.printStackTrace();
            }
            }

        }

        @Override
        public void onCaptureFailed(CameraCaptureSession session, CaptureRequest request, CaptureFailure failure) {
            super.onCaptureFailed(session, request, failure);
        }
    };

    /**监听拍照的图片*/
    private ImageReader.OnImageAvailableListener imageAvailableListener= new ImageReader.OnImageAvailableListener()
    {
        // 当照片数据可用时激发该方法
        @Override
        public void onImageAvailable(ImageReader reader) {
            if(al!=null&&al.size()!=0)
            {
                al.clear();
                Log.e("aiplant==",""+al.size());
            }
            al=new ArrayList<AIPlant>();
            //先验证手机是否有sdcard
            String status = Environment.getExternalStorageState();
            if (!status.equals(Environment.MEDIA_MOUNTED)) {
                Toast.makeText(getApplicationContext(), "你的sd卡不可用。", Toast.LENGTH_SHORT).show();
                return;
            }
            // 获取捕获的照片数据
            Image image = reader.acquireNextImage();
            ByteBuffer buffer = image.getPlanes()[0].getBuffer();
            byte[] data = new byte[buffer.remaining()];
            buffer.get(data);

            //手机拍照都是存到这个路径
            String filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/DCIM/Camera/";
            String picturePath = System.currentTimeMillis() + ".jpg";
            File file = new File(filePath, picturePath);
            try {
                //存到本地相册
                FileOutputStream fileOutputStream = new FileOutputStream(file);
                fileOutputStream.write(data);
                fileOutputStream.flush();
                fileOutputStream.close();

                //获得图片
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 2;
                bitmap = BitmapFactory.decodeByteArray(data, 0, data.length, options);



                Log.e("onResponse","判断前"+picturePath);
                afterTakePicture();
                getResult(filePath+picturePath);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                image.close();
            }
        }


    };

    private void afterTakePicture(){
        stopCamera();

        rl1_bottom.setVisibility(View.INVISIBLE);
        isAppraisal.setVisibility(View.VISIBLE);
      //  rl2_bottom.setVisibility(View.VISIBLE);
        remake.setVisibility(View.VISIBLE);
        ring_camera.setVisibility(View.GONE);
        animationSet1.start();

    }

    @Override
    public void onRequestPermissionsResult(int permsRequestCode, String[] permissions, int[] grantResults){
        switch(permsRequestCode){
            case RESULT_CODE_CAMERA:
                boolean cameraAccepted = grantResults[0]==PackageManager.PERMISSION_GRANTED;
                if(cameraAccepted){
                    //授权成功之后，调用系统相机进行拍照操作等
                    openCamera();
                }else{
                    //用户授权拒绝之后，友情提示一下就可以了
                    Toast.makeText(CameraActivity.this,"请开启应用拍照权限",Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(al!=null&&al.size()!=0)
        {
            al.clear();
            Log.e("aiplant==",""+al.size());
        }
        al=new ArrayList<AIPlant>();
        if(requestCode==GALLY_REQUEST_CODE)
        {
            if(data==null){
                return;
            }
            /**
             * 获取图片的同意资源标识符
             */
            Uri uri=data.getData();
            String path = getPath.UriToPath(uri,CameraActivity.this);
            Log.e("onResponse","UriToPath====>"+path);
            if(path==null) {
                path = getPath.getPath(CameraActivity.this, uri);
            }
            if(path!=null) {
                Log.e("onResponse","getPath====>"+path);
                afterTakePicture();
                updateUI(path);
                getResult(path);
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), data.getData());
                    bitmap = Bitmap.createScaledBitmap(bitmap, 224, 224, false);
                }catch (Exception  e){e.printStackTrace();}
            }else{
                Log.e("onResponse", "onActivityResult: path为空" );
            }
        }

    }
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
            try {
                for (int i = 0; i < al.size(); i++) {
                    Log.e("aiplant", i + " name:" + al.get(i).getName() + " score:" + al.get(i).getScore());
                    //resultContent += "为" + al.get(i).getName() + "的概率为:" + al.get(i).getScore() + "\n";
                }
                resultContent = al.get(0).getName() ;
                if(resultContent.equals("非植物")) {
                    resultContent = "对不起，没有识别出来。";
                    handler.sendEmptyMessage(0x003);
                }else{
                    handler.sendEmptyMessage(0x002);
                }
            }catch (Exception  e){
                Log.e("aiplant", "图片异常");
                resultContent  =  "对不起，没有识别出来。";
                handler.sendEmptyMessage(0x003);
                e.printStackTrace();
            }finally {
            }
        }
    }

    private void updateUI(final String path){
       // picture_local.setVisibility(View.VISIBLE);
        showCamera.setVisibility(View.INVISIBLE);
        afterTakePicture();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {;
                Glide.with(CameraActivity.this)
                        .load(path)
                        .transform(new BitmapSetting(CameraActivity.this))
                        .into(picture_local);
                picture_local.setScaleType(ImageView.ScaleType.CENTER_CROP);
            }
        });

    }


    /**启动拍照*/
    private void startCamera(){
        showCamera.setVisibility(View.VISIBLE);
        if (showCamera.isAvailable()) {
            if(cameraDevice==null) {
                openCamera();
            }
        } else {
            showCamera.setSurfaceTextureListener(surfaceTextureListener);
        }
    }

    /**
     * 停止拍照释放资源*/
    private void stopCamera(){
        if(cameraDevice!=null){
            cameraDevice.close();
            cameraDevice=null;

        }

    }

    private void classifyImage(Bitmap bitmap) {

        final List<Classifier.Recognition> results = classifier.recognizeImage(bitmap);
        final String title = results.get(0).getTitle();
        final float confidence = results.get(0).getConfidence();
        Log.e("info",results.get(0).getTitle()+results.get(0).getConfidence());
        if(results.size()>0 && confidence>=0.4 ) {
            if(!"健康".equals(title.substring(title.length()-2))) {
                result_disease.setVisibility(View.VISIBLE);
                result_disease.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        result_disease.setText("可能为:" + title);
                    }
                });
            }else{
                result_disease.setVisibility(View.VISIBLE);
                result_disease.setText("你的植物很健康哦！");
            }
        }

    }
}
