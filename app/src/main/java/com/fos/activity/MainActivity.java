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

        button = findViewById(R.id.button);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DataInfo data = new DataInfo();
                data.setAirHumidity(1);
                data.setId(1);
                data.setDate("sdf");
                data.setSoilHumidity(1);
                data.setLux(1);
                data.setTemperature(1);
                data.setWaterWeight(100);
//              dataInfoDao.save(data);
//              dataInfoDao.updateData(data);
//              data = dataInfoDao.findFirstData();
//              Toast.makeText(MainActivity.this,""+data,Toast.LENGTH_SHORT).show();
//              dataInfoDao.delete();
            }
        });
    }
}
