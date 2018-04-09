package com.fos.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.fos.R;
import com.fos.dao.DataInfoDao;
import com.fos.database.DataInfo;

public class MainActivity extends AppCompatActivity {

    private Button button;

    private DataInfoDao dataInfoDao=DataInfoDao.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}
