package com.fos.activity;

import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fos.R;

public class CreateCommunityActivity extends AppCompatActivity {

    private RelativeLayout exit_create;
    private Button new_community;
    private EditText edit_community;
    private TextView count_context,photograph,album;
    private ImageView addImage;
    private AlertDialog dialog;
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
                    break;
                case R.id.album:
                    dialog.dismiss();
                    break;
                    default:
                        break;
            }
        }
    };

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