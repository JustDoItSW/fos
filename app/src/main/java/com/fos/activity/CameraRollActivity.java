package com.fos.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fos.R;
import com.fos.tensorflow.Classifier;
import com.fos.tensorflow.RecognitionScoreView;
import com.fos.tensorflow.TensorFlowImageClassifier;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.List;

public class CameraRollActivity extends AppCompatActivity {

    private TextView resultView,photograph,album;
    private Bitmap bitmap;
    private RelativeLayout exit_recognition;
    private Button chooseImage;
    private AlertDialog dialog;



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

        exit_recognition.setOnClickListener(onClickListener);
        chooseImage.setOnClickListener(onClickListener);
    }

    @Override protected void onResume() {
        super.onResume();
    }

    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

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
                    break;
                case R.id.photograph:
                    break;
                    default:
                        break;
            }
        }
    };


}
