package com.hyr.equipment.management.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.hyr.equipment.management.R;
import com.hyr.equipment.management.utils.StringUtils;

import java.util.HashSet;

public class InputStuNoActivity extends AppCompatActivity {

    private EditText et_sn1;
    private EditText et_sn2;
    private EditText et_sn3;
    private EditText et_sn4;
    private EditText et_sn5;
    private EditText et_sn6;
    private EditText et_sn7;
    private EditText et_sn8;
    private EditText et_sn9;
    private EditText et_sn10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_stu_no);

        Button button = (Button) findViewById(R.id.bt_submit); //提交

        et_sn1 = (EditText) findViewById(R.id.sn1);
        et_sn2 = (EditText) findViewById(R.id.sn2);
        et_sn3 = (EditText) findViewById(R.id.sn3);
        et_sn4 = (EditText) findViewById(R.id.sn4);
        et_sn5 = (EditText) findViewById(R.id.sn5);
        et_sn6 = (EditText) findViewById(R.id.sn6);
        et_sn7 = (EditText) findViewById(R.id.sn7);
        et_sn8 = (EditText) findViewById(R.id.sn8);
        et_sn9 = (EditText) findViewById(R.id.sn9);
        et_sn10 = (EditText) findViewById(R.id.sn10);


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = getIntent();
                Bundle data = new Bundle();
                HashSet<String> list = new HashSet<String>();

                String sn1 = et_sn1.getText().toString();
                String sn2 = et_sn2.getText().toString();
                String sn3 = et_sn3.getText().toString();
                String sn4 = et_sn4.getText().toString();
                String sn5 = et_sn5.getText().toString();
                String sn6 = et_sn6.getText().toString();
                String sn7 = et_sn7.getText().toString();
                String sn8 = et_sn8.getText().toString();
                String sn9 = et_sn9.getText().toString();
                String sn10 = et_sn10.getText().toString();
                if (!StringUtils.isEmpty(sn1)) {
                    list.add(sn1);
                }
                if (!StringUtils.isEmpty(sn2)) {
                    list.add(sn2);
                }
                if (!StringUtils.isEmpty(sn3)) {
                    list.add(sn3);
                }
                if (!StringUtils.isEmpty(sn4)) {
                    list.add(sn4);
                }
                if (!StringUtils.isEmpty(sn5)) {
                    list.add(sn5);
                }
                if (!StringUtils.isEmpty(sn6)) {
                    list.add(sn6);
                }
                if (!StringUtils.isEmpty(sn7)) {
                    list.add(sn7);
                }
                if (!StringUtils.isEmpty(sn8)) {
                    list.add(sn8);
                }
                if (!StringUtils.isEmpty(sn9)) {
                    list.add(sn9);
                }
                if (!StringUtils.isEmpty(sn10)) {
                    list.add(sn10);
                }
                data.putSerializable("studentNoList", list);
                intent.putExtras(data);
                // 设置该SelectCityActivity结果码，并设置结束之后退回的Activity
                InputStuNoActivity.this.setResult(0, intent);
                // 结束SelectCityActivity
                InputStuNoActivity.this.finish();
            }
        });

    }
}
