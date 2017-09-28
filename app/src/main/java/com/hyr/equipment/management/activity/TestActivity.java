package com.hyr.equipment.management.activity;

import android.os.Bundle;
import android.widget.TextView;

import com.hyr.equipment.management.R;


public class TestActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        String s = getIntent().getStringExtra("infotest");

        TextView tv = (TextView) findViewById(R.id.tvtest1);
        tv.setText(s);
    }
}
